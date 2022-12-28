package net.zytorx.minecraft.blocklog.cache;


import net.zytorx.minecraft.blocklog.cache.model.blocks.MultiBlockInteraction;
import net.zytorx.minecraft.blocklog.cache.model.blocks.SingleBlockInteraction;
import net.zytorx.minecraft.blocklog.cache.model.common.Interaction;
import net.zytorx.minecraft.blocklog.cache.model.common.MapKey;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

public class LocalFileSystemCache implements Cache {

    private final AutoFileReadMap<SingleBlockInteraction> blockInteractions;
    private final AutoFileReadMap<MultiBlockInteraction> explosionInteractions;
    private final Path path;
    private final Timer timer = new Timer(true);
    private boolean isDirty = false;

    public LocalFileSystemCache(Path path) {
        this.path = path;
        blockInteractions = new AutoFileReadMap<SingleBlockInteraction>().load("_bl_blocks");
        explosionInteractions = new AutoFileReadMap<MultiBlockInteraction>().load("_bl_explosion");
    }

    public void addInteraction(Interaction interaction) {
        if (interaction == null) {
            return;
        }
        if (interaction instanceof SingleBlockInteraction block) {
            synchronized (blockInteractions) {
                blockInteractions.put(block);
            }
        }

        if (interaction instanceof MultiBlockInteraction explosion) {
            synchronized (explosionInteractions) {
                explosionInteractions.put(explosion);
            }
        }
        markDirty();
    }

    @Override
    public Stream<Interaction> getInteractions() {
        return Stream.concat(explosionInteractions.values().stream(), blockInteractions.values().stream());
    }

    public void markDirty() {
        if (isDirty) {
            return;
        }
        isDirty = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                save();
            }
        }, 1000 * 60 * 10);
    }

    public void save() {
        if (!isDirty) return;
        path.toFile().getParentFile().mkdirs();

        blockInteractions.write();
        explosionInteractions.write();

        isDirty = false;
    }

    private class AutoFileReadMap<VALUE extends Interaction> extends HashMap<MapKey, VALUE> {

        private Path path = null;
        private boolean isDirty = false;

        public AutoFileReadMap() {
            super();
        }

        private AutoFileReadMap(HashMap<MapKey, VALUE> readObject, Path path) {
            super(readObject);
            this.path = path;
        }

        public VALUE put(VALUE value) {
            isDirty = true;
            return super.put(value.asKey(), value);
        }

        public AutoFileReadMap<VALUE> load(String suffix) {
            path = Path.of(LocalFileSystemCache.this.path.toAbsolutePath() + suffix);

            AutoFileReadMap<VALUE> temp = null;
            try {
                var reader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path.toFile())));
                temp = new AutoFileReadMap<>((HashMap<MapKey, VALUE>) reader.readObject(), this.path);
                reader.close();
            } catch (Exception e) {
                if (temp == null) {
                    temp = this;
                }
            }
            return temp;
        }

        public void write() {
            if (!isDirty) {
                return;
            }
            try {
                var writer = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(path.toFile())));
                writer.reset();
                synchronized (this) {
                    writer.writeObject(new HashMap<>(this));
                }
                writer.close();

                isDirty = false;
            } catch (Exception ignored) {
            }
        }
    }
}
