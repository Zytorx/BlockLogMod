package net.zytorx.minecraft.blocklog.logging;

import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;
import net.zytorx.minecraft.blocklog.database.Database;
import net.zytorx.minecraft.blocklog.database.model.BlockInteraction;
import net.zytorx.minecraft.blocklog.database.model.Interaction;

public class Logger {
    private static Database database = null;

    private Logger() {
    }

    public static void register(Database database) {
        if (Logger.database != null) {
            throw new RuntimeException("Should only be called once");
        }

        Logger.database = database;
    }

    public static void breakBlock(BlockEvent.BreakEvent event) {
        var interaction = defaultInteraction(event);
        interaction.setPlayer(event.getPlayer().getUUID());
        interaction.setBlockOld(readBlock(event.getState()));
        log(interaction);
    }

    public static void placeBlock(BlockEvent.EntityPlaceEvent event) {

    }

    public static void placeMultiBlock(BlockEvent.EntityMultiPlaceEvent event) {

    }

    public static void trampleFarmland(BlockEvent.FarmlandTrampleEvent event) {

    }

    public static void toolModification(BlockEvent.BlockToolModificationEvent event) {

    }

    private static String readBlock(BlockState state) {
        if (state == null) {
            return null;
        }
        return NbtUtils.writeBlockState(state).toString();
    }

    private static BlockInteraction defaultInteraction(BlockEvent event) {
        var time = System.currentTimeMillis();
        var level = ((Level) event.getWorld()).dimension().toString();
        var pos = event.getPos();
        return new BlockInteraction(time, null, level, null, null, pos.getX(), pos.getY(), pos.getZ());
    }

    private static void log(Interaction interaction) {
        if (database != null) {
            database.addInteraction(interaction);
        }
    }
}
