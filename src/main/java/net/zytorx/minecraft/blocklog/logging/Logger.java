package net.zytorx.minecraft.blocklog.logging;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zytorx.minecraft.blocklog.BlockLog;
import net.zytorx.minecraft.blocklog.cache.Cache;
import net.zytorx.minecraft.blocklog.cache.model.InteractionUtils;
import net.zytorx.minecraft.blocklog.cache.model.blocks.BlockInteraction;
import net.zytorx.minecraft.blocklog.cache.model.common.Interaction;
import net.zytorx.minecraft.blocklog.cache.model.common.OldNewTuple;

import java.util.stream.Stream;

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
        var levelString = level.dimension().toString();

        for (var pos : toBlow) {
            var state = level.getBlockState(pos);
            if (state.isAir()) {
                continue;
            }
            var block = InteractionUtils.writeBlockState(state);
            log(new BlockInteraction(time, entity, levelString, block, null, pos.getX(), pos.getY(), pos.getZ()));
        }
    }

    public static Stream<? extends Interaction> getAllInteractions() {
        return cache.getInteractions();
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
