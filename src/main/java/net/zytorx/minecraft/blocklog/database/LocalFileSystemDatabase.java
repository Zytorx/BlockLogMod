package net.zytorx.minecraft.blocklog.database;


import net.zytorx.minecraft.blocklog.database.model.BlockInteraction;
import net.zytorx.minecraft.blocklog.database.model.Interaction;
import net.zytorx.minecraft.blocklog.database.model.MapKey;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LocalFileSystemDatabase implements Database {

    private final Map<MapKey, BlockInteraction> blockInteractions;
    private final Path path;

    public LocalFileSystemDatabase(Path path) {
        this.path = path;
        Map<MapKey, BlockInteraction> temp;
        try {
            var reader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path.toFile())));
            temp = (HashMap<MapKey, BlockInteraction>) reader.readObject();
            reader.close();
        } catch (IOException | ClassNotFoundException e) {
            temp = new HashMap<>();
        }
        blockInteractions = temp;
    }

    public void addInteraction(Interaction interaction) {
        if (interaction == null) {
            return;
        }
        if (interaction instanceof BlockInteraction block) {
            blockInteractions.put(block.asKey(), block);
        }
        save();
    }

    private void save() {
        var file = path.toFile();
        file.getParentFile().mkdirs();
        try {
            var writer = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            writer.reset();
            writer.writeObject(blockInteractions);
            writer.close();
        } catch (Exception ignored) {
            System.out.println("FAILURE");
        }
    }
}
