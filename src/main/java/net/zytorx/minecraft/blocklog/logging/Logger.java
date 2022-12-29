package net.zytorx.minecraft.blocklog.logging;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zytorx.minecraft.blocklog.BlockLog;
import net.zytorx.minecraft.blocklog.cache.model.InteractionUtils;
import net.zytorx.minecraft.blocklog.cache.model.blocks.BlockInteraction;
import net.zytorx.minecraft.blocklog.cache.model.common.Interaction;
import net.zytorx.minecraft.blocklog.cache.model.common.OldNewTuple;

@Mod.EventBusSubscriber(modid = BlockLog.MOD_ID, value = Dist.DEDICATED_SERVER)
public class Logger {

    private Logger() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void breakBlock(BlockEvent.BreakEvent event) {
        if (event.getState().isAir()) {
            return;
        }

        var interaction = defaultBlockInteraction(event);
        interaction.setEntity(event.getPlayer());
        interaction.setBlock(new OldNewTuple(InteractionUtils.writeBlockState(event.getState()), null));
        log(interaction);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void placeBlock(BlockEvent.EntityPlaceEvent event) {
        var oldState = event.getBlockSnapshot().getReplacedBlock();
        if (event.getPlacedBlock().equals(oldState) || event.getPlacedBlock().isAir()) {
            return;
        }
        var interaction = defaultBlockInteraction(event);
        interaction.setEntity(event.getEntity());
        interaction.setBlock(new OldNewTuple(InteractionUtils.writeBlockState(oldState), InteractionUtils.writeBlockState(event.getPlacedBlock())));
        log(interaction);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void placeMultiBlock(BlockEvent.EntityMultiPlaceEvent event) {
        placeBlock(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void trampleFarmland(BlockEvent.FarmlandTrampleEvent event) {
        var interaction = defaultBlockInteraction(event);
        interaction.setEntity(event.getEntity());
        interaction.setBlock(new OldNewTuple(InteractionUtils.writeBlockState(Blocks.FARMLAND.defaultBlockState()), InteractionUtils.writeBlockState(Blocks.DIRT.defaultBlockState())));
        log(interaction);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void toolModification(BlockEvent.BlockToolModificationEvent event) {
        if (event.getState().equals(event.getFinalState())) {
            return;
        }
        var interaction = defaultBlockInteraction(event);
        interaction.setEntity(event.getPlayer());
        interaction.setBlock(new OldNewTuple(InteractionUtils.writeBlockState(event.getState()), InteractionUtils.writeBlockState(event.getFinalState())));
        log(interaction);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void explosion(ExplosionEvent event) {
        var explosion = event.getExplosion();
        var toBlow = explosion.getToBlow();
        var time = System.currentTimeMillis();
        var entity = explosion.getSourceMob();
        var level = event.getWorld();
        var levelString = level.dimension().location().toString();

        for (var pos : toBlow) {
            var state = level.getBlockState(pos);
            if (state.isAir()) {
                continue;
            }
            var id = BlockLog.CACHE == null ? null : BlockLog.CACHE.createUniqueBlockId();
            var block = InteractionUtils.writeBlockState(state);
            log(new BlockInteraction(id, time, entity, levelString, block, null, pos.getX(), pos.getY(), pos.getZ()));
        }
    }

    private static BlockInteraction defaultBlockInteraction(BlockEvent event) {
        var id = BlockLog.CACHE == null ? null : BlockLog.CACHE.createUniqueBlockId();
        var time = System.currentTimeMillis();
        var level = ((Level) event.getWorld()).dimension().location().toString();
        var pos = event.getPos();
        return new BlockInteraction(id, time, null, level, null, null, pos.getX(), pos.getY(), pos.getZ());
    }

    private static void log(Interaction interaction) {
        if (BlockLog.CACHE != null) {
            BlockLog.CACHE.addInteraction(interaction);
        }
    }
}
