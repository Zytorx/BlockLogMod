package net.zytorx.minecraft.blocklog.cache.model.blocks;

import net.zytorx.minecraft.blocklog.cache.model.common.Interaction;
import net.zytorx.minecraft.blocklog.cache.model.common.OldNewTuple;

import java.util.UUID;

public class SingleBlockInteraction implements Interaction {
    private long time;
    private UUID entity;
    private String level;
    private OldNewTuple block;
    private int x;
    private int y;
    private int z;

    public SingleBlockInteraction() {
    }

    public SingleBlockInteraction(long time, UUID entity, String level, String blockOld, String blockNew, int x, int y, int z) {
        this.time = time;
        this.entity = entity;
        this.level = level;
        this.block = new OldNewTuple(blockOld, blockNew);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public UUID getEntity() {
        return entity;
    }

    public void setEntity(UUID entity) {
        this.entity = entity;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public OldNewTuple getBlock() {
        return block;
    }

    public void setBlock(OldNewTuple block) {
        this.block = block;
    }
}

