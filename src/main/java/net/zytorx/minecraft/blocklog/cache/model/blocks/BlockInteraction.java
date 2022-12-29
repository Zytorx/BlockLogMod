package net.zytorx.minecraft.blocklog.cache.model.blocks;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.zytorx.minecraft.blocklog.cache.model.common.Interaction;
import net.zytorx.minecraft.blocklog.cache.model.common.OldNewTuple;

import java.util.UUID;

public class BlockInteraction implements Interaction {
    private String id;
    private long time;
    private UUID entityId;
    private String entityName;
    private String level;
    private OldNewTuple block;
    private int x;
    private int y;
    private int z;

    public BlockInteraction() {
    }

    public BlockInteraction(String id, long time, Entity entityId, String level, String blockOld, String blockNew, int x, int y, int z) {
        this.id = id;
        this.time = time;
        setEntity(entityId);
        this.level = level;
        this.block = new OldNewTuple(blockOld, blockNew);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public UUID getEntityID() {
        return entityId;
    }

    public void setEntity(Entity entity) {
        if (entity == null) {
            return;
        }
        this.entityId = entity.getUUID();
        this.entityName = entity instanceof Player ? entity.getName().getString() : "Mob." + entity.getName().getString();
    }

    public String getEntityName() {
        return entityName;
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

