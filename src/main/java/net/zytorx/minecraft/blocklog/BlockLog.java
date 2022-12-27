package net.zytorx.minecraft.blocklog;

import net.minecraftforge.fml.common.Mod;
import net.zytorx.minecraft.blocklog.database.LocalFileSystemDatabase;
import net.zytorx.minecraft.blocklog.logging.Logger;

import java.nio.file.Path;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BlockLog.MOD_ID)
public class BlockLog {
    // Directly reference a slf4j logger

    public static final String MOD_ID = "blocklog";

    public BlockLog() {
        Logger.register(new LocalFileSystemDatabase(Path.of("/home/zytorx/testing/local")));

    }
}
