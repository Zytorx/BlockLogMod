package net.zytorx.minecraft.blocklog;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;
import net.zytorx.minecraft.blocklog.cache.Cache;
import net.zytorx.minecraft.blocklog.cache.LocalFileSystemCache;
import net.zytorx.minecraft.blocklog.commands.CommandRegistrar;

import java.nio.file.Path;

@Mod(BlockLog.MOD_ID)
public class BlockLog {
    public static final String MOD_ID = "blocklog";
    public static Cache CACHE;

    public BlockLog() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {

                    CACHE = new LocalFileSystemCache(Path.of("/home/zytorx/testing/local"));

                    MinecraftForge.EVENT_BUS.addListener(CommandRegistrar::register);
                    MinecraftForge.EVENT_BUS.addListener(this::onShutdown);
                }
        );
        //ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,);
    }

    private void onShutdown(ServerStoppedEvent event) {
        if (CACHE != null)
            CACHE.save();
    }
}
