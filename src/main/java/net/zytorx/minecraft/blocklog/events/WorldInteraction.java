package net.zytorx.minecraft.blocklog.events;

import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zytorx.minecraft.blocklog.BlockLog;
import net.zytorx.minecraft.blocklog.logging.Logger;

@Mod.EventBusSubscriber(modid = BlockLog.MOD_ID)
public class WorldInteraction {

    @SubscribeEvent
    public static void blockBreakEvent(BlockEvent.BreakEvent event) {
        if (event.isCanceled()) {
            return;
        }
        Logger.breakBlock(event);
    }

    @SubscribeEvent
    public static void blockPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        if (event.isCanceled()) {
            return;
        }
        Logger.placeBlock(event);
    }

}
