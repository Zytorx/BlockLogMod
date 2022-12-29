package net.zytorx.minecraft.blocklog;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkConstants;
import net.zytorx.minecraft.blocklog.cache.Cache;
import net.zytorx.minecraft.blocklog.cache.LocalFileSystemCache;
import net.zytorx.minecraft.blocklog.config.BlockLogServerConfig;

import java.nio.file.Path;

@Mod(BlockLog.MOD_ID)
public class BlockLog {
    public static final String MOD_ID = "blocklog";
    public static Cache CACHE;

    public BlockLog() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {

                    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BlockLogServerConfig.SPEC, "blocklog.toml");
                    CACHE = new LocalFileSystemCache(Path.of(BlockLogServerConfig.LOCAL_FILE_DIRECTORY.get(), BlockLogServerConfig.LOCAL_FILE_NAME.get()));
                    MinecraftForge.EVENT_BUS.addListener(this::onShutdown);
                }
        );
    }

    private void onShutdown(ServerStoppedEvent event) {
        if (CACHE != null)
            CACHE.save();
    }
}
