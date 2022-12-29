package net.zytorx.minecraft.blocklog.cache;


import net.zytorx.minecraft.blocklog.cache.model.blocks.BlockInteraction;
import net.zytorx.minecraft.blocklog.cache.model.common.Interaction;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

public class LocalFileSystemCache implements Cache {

    private final AutoFileReadList<BlockInteraction> blockInteractions;
    private final Path path;
    private final Timer timer = new Timer(true);
    private boolean isDirty = false;

    public LocalFileSystemCache(Path path) {
        this.path = path;
        blockInteractions = new AutoFileReadList<BlockInteraction>().load("_bl_blocks");
    }

    public void addInteraction(Interaction interaction) {
        if (interaction == null) {
            return;
        }
        if (interaction instanceof BlockInteraction block) {
            synchronized (blockInteractions) {
                blockInteractions.add(block);
            }
        }
        markDirty();
    }

    @Override
    public Stream<? extends Interaction> getInteractions() {
        return blockInteractions.stream();
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

        isDirty = false;
    }

    private class AutoFileReadList<VALUE extends Interaction> extends ArrayList<VALUE> {

        private Path path = null;
        private boolean isDirty = false;

        public AutoFileReadList() {
            super();
        }

        private AutoFileReadList(ArrayList<VALUE> readObject, Path path) {
            super(readObject);
            this.path = path;
        }

        @Override
        public boolean add(VALUE value) {
            isDirty = true;
            return super.add(value);
        }

        public AutoFileReadList<VALUE> load(String suffix) {
            path = Path.of(LocalFileSystemCache.this.path.toAbsolutePath() + suffix);

            AutoFileReadList<VALUE> temp = null;
            try {
                var reader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path.toFile())));
                temp = new AutoFileReadList<>((ArrayList<VALUE>) reader.readObject(), this.path);
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
                    writer.writeObject(new ArrayList<>(this));
                }
                writer.close();

                isDirty = false;
            } catch (Exception ignored) {
            }
        }
    }
}
