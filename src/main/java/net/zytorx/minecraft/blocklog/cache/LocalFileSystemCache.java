package net.zytorx.minecraft.blocklog.cache;


import net.zytorx.minecraft.blocklog.cache.model.blocks.BlockInteraction;
import net.zytorx.minecraft.blocklog.cache.model.common.Interaction;
import net.zytorx.minecraft.blocklog.config.BlockLogServerConfig;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

public class LocalFileSystemCache implements Cache {

    private final AutoFileReadMap<BlockInteraction> blockInteractions;
    private final Path path;
    private final Timer timer = new Timer(true);
    private boolean isDirty = false;

    public LocalFileSystemCache(Path path) {
        this.path = path;
        blockInteractions = new AutoFileReadMap<BlockInteraction>().load(path, BlockLogServerConfig.BLOCK_SUFFIX.get());
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

    public Interaction removeInteraction(String id) {
        if (blockInteractions.containsKey(id)) {
            markDirty();
            return blockInteractions.remove(id);
        }
        return null;
    }

    @Override
    public Stream<? extends Interaction> getInteractions() {
        return blockInteractions.values().stream();
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

    public String createUniqueBlockId() {
        return "b" + blockInteractions.getCounterAndCount();
    }

    public void save() {
        if (!isDirty) return;
        path.toFile().getParentFile().mkdirs();

        blockInteractions.write();

        isDirty = false;
    }

    private static class AutoFileReadMap<VALUE extends Interaction> extends HashMap<String, VALUE> {

        private transient Path path = null;
        private transient boolean isDirty = false;

        private long counter = 0;

        public AutoFileReadMap() {
            super();
        }

        public VALUE add(VALUE value) {
            isDirty = true;
            return super.put(value.getId(), value);
        }

        public VALUE remove(VALUE value) {
            return remove(value.getId());
        }

        @Override
        public VALUE remove(Object key) {
            isDirty = true;
            return super.remove(key);
        }

        public long getCounterAndCount() {
            return counter++;
        }

        public AutoFileReadMap<VALUE> load(Path path, String suffix) {
            this.path = Path.of(path.toAbsolutePath() + suffix);

            AutoFileReadMap<VALUE> temp = null;
            try {
                var reader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(this.path.toFile())));
                temp = (AutoFileReadMap<VALUE>) reader.readObject();
                temp.path = this.path;
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
                    writer.writeObject(this);
                }
                writer.close();

                isDirty = false;
            } catch (Exception ignored) {
            }
        }
    }
}
