package net.zytorx.minecraft.blocklog;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zytorx.minecraft.blocklog.cache.Cache;
import net.zytorx.minecraft.blocklog.cache.LocalFileSystemCache;
import net.zytorx.minecraft.blocklog.commands.CommandRegistrar;
import net.zytorx.minecraft.blocklog.logging.Logger;

import java.nio.file.Path;

@Mod(BlockLog.MOD_ID)
@Mod.EventBusSubscriber(modid = BlockLog.MOD_ID)
public class BlockLog {
    public static final String MOD_ID = "blocklog";
    private static Cache cache;

    public BlockLog() {
        if (cache != null) {
            throw new RuntimeException("BlockLog can only be created once");
        }
        cache = new LocalFileSystemCache(Path.of("/home/zytorx/testing/local"));
        Logger.register(cache);

        MinecraftForge.EVENT_BUS.addListener(CommandRegistrar::register);

        //ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,);
    }

    @SubscribeEvent
    public static void onShutdown(ServerStoppedEvent event) {
        cache.save();
    }
}
