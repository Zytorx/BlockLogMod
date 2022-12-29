package net.zytorx.minecraft.blocklog.commands;

import net.minecraftforge.common.UsernameCache;

import java.util.UUID;

public class Utils {
    public static UUID getUuid(String name) {
        var map = UsernameCache.getMap();
        for (var id : map.keySet()) {
            if (map.get(id).equals(name)) {
                return id;
            }
        }
        return null;
    }
}
