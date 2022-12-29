package net.zytorx.minecraft.blocklog.events;

import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zytorx.minecraft.blocklog.BlockLog;
import net.zytorx.minecraft.blocklog.logging.GlobalLogCache;
import net.zytorx.minecraft.blocklog.logging.filter.Filter;

@Mod.EventBusSubscriber(modid = BlockLog.MOD_ID, value = Dist.DEDICATED_SERVER)
public class PlayerEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightclick(PlayerInteractEvent.RightClickBlock event) {
        var player = event.getPlayer();
        if (GlobalLogCache.isInspector(player.getUUID())) {
            var pos = event.getPos();
            var filter = Filter.blank();
            filter.setPos(pos);
            var loaded = GlobalLogCache.loadBlockLogCache(filter, 0);
            for (var toPrint : loaded) {
                player.sendMessage(new TextComponent(toPrint), player.getUUID());
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        GlobalLogCache.removeInspector(event.getPlayer().getUUID());
    }

}
