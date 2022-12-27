package net.zytorx.minecraft.blocklog.database.model;

import java.io.Serializable;
import java.util.UUID;

public interface Interaction extends Serializable {

    long getTime();

    UUID getPlayer();

    default MapKey asKey() {
        return MapKey.of(this);
    }
}
