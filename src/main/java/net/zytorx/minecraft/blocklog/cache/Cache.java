package net.zytorx.minecraft.blocklog.cache;

import net.zytorx.minecraft.blocklog.cache.model.common.Interaction;

import java.util.stream.Stream;

public interface Cache {

    void addInteraction(Interaction interaction);

    Interaction removeInteraction(String id);

    String createUniqueBlockId();

    Stream<? extends Interaction> getInteractions();

    void markDirty();

    void save();
}
