package net.zytorx.minecraft.blocklog.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class BlockLogServerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<String> LOCAL_FILE_DIRECTORY;
    public static final ForgeConfigSpec.ConfigValue<String> LOCAL_FILE_NAME;
    public static final ForgeConfigSpec.ConfigValue<String> BLOCK_SUFFIX;

    static {
        BUILDER.push("BlockLog");

        LOCAL_FILE_DIRECTORY = BUILDER.comment("Directory where the local file should be saved, normally doesnt need to be changed").define("LocalDirectory", "blocklog");
        LOCAL_FILE_NAME = BUILDER.comment("Name of the local file (without suffix)").define("LocalFile", "local");
        BLOCK_SUFFIX = BUILDER.comment("Suffix for the block interactions file").define("BlockSuffix", "_blocks");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
