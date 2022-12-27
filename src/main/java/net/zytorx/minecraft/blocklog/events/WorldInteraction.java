package net.zytorx.minecraft.blocklog.events;

import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zytorx.minecraft.blocklog.BlockLog;
import net.zytorx.minecraft.blocklog.logging.Logger;

@Mod.EventBusSubscriber(modid = BlockLog.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldInteraction {

    @SubscribeEvent
    public void blockBreakEvent(BlockEvent.BreakEvent event) {
        if (event.isCanceled()) {
            return;
        }
        Logger.breakBlock(event);
    }

    @SubscribeEvent
    public void blockPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        if (event.isCanceled()) {
            return;
        }
        Logger.placeBlock(event);
    }

}
