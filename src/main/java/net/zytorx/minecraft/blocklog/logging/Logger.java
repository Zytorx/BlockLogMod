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
import net.zytorx.minecraft.blocklog.cache.model.blocks.MultiBlockInteraction;
import net.zytorx.minecraft.blocklog.cache.model.blocks.SingleBlockInteraction;
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
        interaction.setEntity(event.getPlayer().getUUID());
        interaction.setBlock(new OldNewTuple(InteractionUtils.writeBlockState(event.getState()), null));
        log(interaction);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void placeBlock(BlockEvent.EntityPlaceEvent event) {
        if (event.getPlacedAgainst().equals(event.getPlacedBlock()) || event.getPlacedBlock().isAir()) {
            return;
        }
        var interaction = defaultBlockInteraction(event);
        interaction.setEntity(event.getEntity().getUUID());
        interaction.setBlock(new OldNewTuple(InteractionUtils.writeBlockState(event.getPlacedAgainst()), InteractionUtils.writeBlockState(event.getPlacedBlock())));
        log(interaction);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void placeMultiBlock(BlockEvent.EntityMultiPlaceEvent event) {
        placeBlock(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void trampleFarmland(BlockEvent.FarmlandTrampleEvent event) {
        var interaction = defaultBlockInteraction(event);
        interaction.setEntity(event.getEntity().getUUID());
        interaction.setBlock(new OldNewTuple(InteractionUtils.writeBlockState(Blocks.FARMLAND.defaultBlockState()), InteractionUtils.writeBlockState(Blocks.DIRT.defaultBlockState())));
        log(interaction);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void toolModification(BlockEvent.BlockToolModificationEvent event) {
        if (event.getState().equals(event.getFinalState())) {
            return;
        }
        var interaction = defaultBlockInteraction(event);
        interaction.setEntity(event.getPlayer().getUUID());
        interaction.setBlock(new OldNewTuple(InteractionUtils.writeBlockState(event.getState()), InteractionUtils.writeBlockState(event.getFinalState())));
        log(interaction);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void explosion(ExplosionEvent event) {
        var explosion = event.getExplosion();
        var toBlow = explosion.getToBlow();
        if (toBlow.isEmpty()) {
            return;
        }
        var time = System.currentTimeMillis();
        var entity = explosion.getSourceMob().getUUID();
        var level = event.getWorld();
        var interaction = new MultiBlockInteraction(time, entity, level.dimension().toString());
        for (var pos : toBlow) {
            interaction.addBlock(pos, InteractionUtils.writeBlockState(level.getBlockState(pos)), null);
        }
        log(interaction);
    }

    public static Stream<Interaction> getAllInteractions() {
        return cache.getInteractions();
    }

    private static SingleBlockInteraction defaultBlockInteraction(BlockEvent event) {
        var time = System.currentTimeMillis();
        var level = ((Level) event.getWorld()).dimension().toString();
        var pos = event.getPos();
        return new SingleBlockInteraction(time, null, level, null, null, pos.getX(), pos.getY(), pos.getZ());
    }

    private static void log(Interaction interaction) {
        if (cache != null) {
            cache.addInteraction(interaction);
        }
    }
}
