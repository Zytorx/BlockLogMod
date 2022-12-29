package net.zytorx.minecraft.blocklog.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zytorx.minecraft.blocklog.BlockLog;
import net.zytorx.minecraft.blocklog.commands.BlockLogCommand;
import net.zytorx.minecraft.blocklog.logging.GlobalLogCache;

@Mod.EventBusSubscriber(modid = BlockLog.MOD_ID, value = Dist.DEDICATED_SERVER)
public class ServerEvents {
    private static int tick = 0;

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        new BlockLogCommand(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent event) {
        tick++;
        if (tick == 20 * 60) {
            GlobalLogCache.deleteOverhead();
            tick = 0;
        }
    }
}
