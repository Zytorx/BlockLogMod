package net.zytorx.minecraft.blocklog.cache.model;

import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class InteractionUtils {
    private InteractionUtils() {
    }

    public static String writeBlockState(BlockState state) {
        if (state == null || state.isAir()) {
            return null;
        }
        return NbtUtils.writeBlockState(state).toString();
    }

    public static BlockState readBlockState(String state) {
        try {
            return NbtUtils.readBlockState(TagParser.parseTag(state));
        } catch (Exception e) {
            return Blocks.AIR.defaultBlockState();
        }
    }
}
