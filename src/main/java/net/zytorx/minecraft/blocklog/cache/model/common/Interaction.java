package net.zytorx.minecraft.blocklog.cache.model.common;

import java.io.Serializable;
import java.util.UUID;

public interface Interaction extends Serializable {

    long getTime();

    UUID getEntity();

    default MapKey asKey() {
        return MapKey.of(this);
    }
}
