package net.zytorx.minecraft.blocklog.cache;

import net.zytorx.minecraft.blocklog.cache.model.common.Interaction;

import java.util.stream.Stream;

public interface Cache {

    void addInteraction(Interaction interaction);

    Stream<Interaction> getInteractions();

    void markDirty();

    void save();
}
