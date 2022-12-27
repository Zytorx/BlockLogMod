package net.zytorx.minecraft.blocklog.database;


import net.zytorx.minecraft.blocklog.database.model.BlockInteraction;
import net.zytorx.minecraft.blocklog.database.model.ExplosionInteraction;
import net.zytorx.minecraft.blocklog.database.model.Interaction;
import net.zytorx.minecraft.blocklog.database.model.MapKey;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class LocalFileSystemCache implements Cache {

    private final AutoFileReadMap<BlockInteraction> blockInteractions;
    private final AutoFileReadMap<ExplosionInteraction> explosionInteractions;
    private final Path path;
    private final Timer timer = new Timer(true);
    private boolean isDirty = false;

    public LocalFileSystemCache(Path path) {
        this.path = path;
        blockInteractions = new AutoFileReadMap<BlockInteraction>().load("_bl_blocks");
        explosionInteractions = new AutoFileReadMap<ExplosionInteraction>().load("_bl_explosion");
    }

    public void addInteraction(Interaction interaction) {
        if (interaction == null) {
            return;
        }
        if (interaction instanceof BlockInteraction block) {
            synchronized (blockInteractions) {
                blockInteractions.put(block);
            }
        }

        if (interaction instanceof ExplosionInteraction explosion) {
            synchronized (explosionInteractions) {
                explosionInteractions.put(explosion);
            }
        }
        markDirty();
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

            AutoFileReadMap<VALUE> temp;
            try {
                var reader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path.toFile())));
                temp = new AutoFileReadMap<>((HashMap<MapKey, VALUE>) reader.readObject(), this.path);
                reader.close();
            } catch (Exception e) {
                temp = this;
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
                System.out.println("FAILURE");
            }
        }
    }
}
