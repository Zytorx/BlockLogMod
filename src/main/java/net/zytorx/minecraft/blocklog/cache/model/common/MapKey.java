package net.zytorx.minecraft.blocklog.cache.model.common;

import java.io.Serializable;
import java.util.UUID;

public class MapKey implements Serializable {
    private final long time;
    private final UUID player;

    public MapKey(long time, UUID player) {
        this.time = time;
        this.player = player;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(time) * 235 + player.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MapKey key) {
            return key.time == time && key.player.equals(player);
        }
        return false;
    }

    public static MapKey of(Interaction interaction) {
        return new MapKey(interaction.getTime(), interaction.getEntity());
    }
}
