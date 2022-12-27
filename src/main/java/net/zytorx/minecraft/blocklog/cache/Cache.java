package net.zytorx.minecraft.blocklog.cache;

import net.zytorx.minecraft.blocklog.cache.model.common.Interaction;

public interface Cache {

    void addInteraction(Interaction interaction);

    void markDirty();

    void save();
}
