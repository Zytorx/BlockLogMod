package net.zytorx.minecraft.blocklog.database.model;

import java.util.UUID;

public class BlockInteraction implements Interaction {
    private long time;
    private UUID player;
    private String level;
    private String blockOld;
    private String blockNew;
    private int x;
    private int y;
    private int z;

    public BlockInteraction() {
    }

    public BlockInteraction(long time, UUID player, String level, String blockOld, String blockNew, int x, int y, int z) {
        this.time = time;
        this.player = player;
        this.level = level;
        this.blockOld = blockOld;
        this.blockNew = blockNew;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getBlockNew() {
        return blockNew;
    }

    public void setBlockNew(String blockNew) {
        this.blockNew = blockNew;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public UUID getPlayer() {
        return player;
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }

    public String getBlockOld() {
        return blockOld;
    }

    public void setBlockOld(String blockOld) {
        this.blockOld = blockOld;
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
}

