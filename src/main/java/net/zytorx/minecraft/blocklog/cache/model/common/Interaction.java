package net.zytorx.minecraft.blocklog.cache.model.common;

import net.minecraft.world.entity.Entity;

import java.io.Serializable;
import java.util.UUID;

public interface Interaction extends Serializable {

    String getId();

    long getTime();

    void setEntity(Entity entityId);

    UUID getEntityID();

    String getEntityName();
}
