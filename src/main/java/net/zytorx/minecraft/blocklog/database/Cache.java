package net.zytorx.minecraft.blocklog.database;

import net.zytorx.minecraft.blocklog.database.model.Interaction;

public interface Cache {

    void addInteraction(Interaction interaction);

    void markDirty();

    void save();
}
