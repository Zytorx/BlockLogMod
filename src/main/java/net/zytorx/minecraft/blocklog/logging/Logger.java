package net.zytorx.minecraft.blocklog.logging;

import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zytorx.minecraft.blocklog.BlockLog;
import net.zytorx.minecraft.blocklog.database.Cache;
import net.zytorx.minecraft.blocklog.database.model.BlockInteraction;
import net.zytorx.minecraft.blocklog.database.model.ExplosionInteraction;
import net.zytorx.minecraft.blocklog.database.model.Interaction;
import net.zytorx.minecraft.blocklog.database.model.OldNewTuple;

@Mod.EventBusSubscriber(modid = BlockLog.MOD_ID)
public class Logger {
    private static Cache cache = null;

    private Logger() {
    }

    public static void register(Cache cache) {
        if (Logger.cache != null) {
            throw new RuntimeException("Should only be called once");
        }

        Logger.cache = cache;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void breakBlock(BlockEvent.BreakEvent event) {
        var interaction = defaultBlockInteraction(event);
        interaction.setEntity(event.getPlayer().getUUID());
        interaction.setBlock(new OldNewTuple(writeBlockState(event.getState()), null));
        log(interaction);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void placeBlock(BlockEvent.EntityPlaceEvent event) {
        var interaction = defaultBlockInteraction(event);
        interaction.setEntity(event.getEntity().getUUID());
        interaction.setBlock(new OldNewTuple(writeBlockState(event.getPlacedAgainst()), writeBlockState(event.getPlacedBlock())));
        log(interaction);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void placeMultiBlock(BlockEvent.EntityMultiPlaceEvent event) {
        var interaction = defaultBlockInteraction(event);
        interaction.setEntity(event.getEntity().getUUID());
        interaction.setBlock(new OldNewTuple(writeBlockState(event.getPlacedAgainst()), writeBlockState(event.getPlacedBlock())));
        log(interaction);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void trampleFarmland(BlockEvent.FarmlandTrampleEvent event) {
        var interaction = defaultBlockInteraction(event);
        interaction.setEntity(event.getEntity().getUUID());
        interaction.setBlock(new OldNewTuple(writeBlockState(Blocks.FARMLAND.defaultBlockState()), writeBlockState(Blocks.DIRT.defaultBlockState())));
        log(interaction);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void toolModification(BlockEvent.BlockToolModificationEvent event) {
        var interaction = defaultBlockInteraction(event);
        interaction.setEntity(event.getPlayer().getUUID());
        interaction.setBlock(new OldNewTuple(writeBlockState(event.getState()), writeBlockState(event.getFinalState())));
        log(interaction);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void explosion(ExplosionEvent event) {
        var explosion = event.getExplosion();
        var time = System.currentTimeMillis();
        var entity = explosion.getSourceMob().getUUID();
        var level = event.getWorld();
        var interaction = new ExplosionInteraction(time, entity, level.dimension().toString());
        for (var pos : explosion.getToBlow()) {
            interaction.addBlock(pos, writeBlockState(level.getBlockState(pos)), null);
        }

        log(interaction);
    }

    private static String writeBlockState(BlockState state) {
        if (state == null || state.isAir()) {
            return null;
        }
        return NbtUtils.writeBlockState(state).toString();
    }

    private static BlockState readBlockState(String state) {
        try {
            return NbtUtils.readBlockState(TagParser.parseTag(state));
        } catch (Exception e) {
            return Blocks.AIR.defaultBlockState();
        }
    }

    private static BlockInteraction defaultBlockInteraction(BlockEvent event) {
        var time = System.currentTimeMillis();
        var level = ((Level) event.getWorld()).dimension().toString();
        var pos = event.getPos();
        return new BlockInteraction(time, null, level, null, null, pos.getX(), pos.getY(), pos.getZ());
    }

    private static void log(Interaction interaction) {
        if (cache != null) {
            cache.addInteraction(interaction);
        }
    }
}
