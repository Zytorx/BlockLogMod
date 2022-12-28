package net.zytorx.minecraft.blocklog.events;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zytorx.minecraft.blocklog.BlockLog;
import net.zytorx.minecraft.blocklog.commands.CommandsCache;

@Mod.EventBusSubscriber(modid = BlockLog.MOD_ID)
public class ServerEvents {
    private static int tick = 0;

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent event) {
        tick++;
        if (tick == 20 * 60) {
            CommandsCache.deleteOverhead();
            tick = 0;
        }
    }
}
