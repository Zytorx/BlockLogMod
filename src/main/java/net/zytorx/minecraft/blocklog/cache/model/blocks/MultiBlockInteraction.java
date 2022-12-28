package net.zytorx.minecraft.blocklog.cache.model.blocks;

import net.minecraft.core.BlockPos;
import net.zytorx.minecraft.blocklog.cache.model.common.Interaction;
import net.zytorx.minecraft.blocklog.cache.model.common.OldNewTuple;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MultiBlockInteraction implements Interaction {
    private final HashMap<CustomBlockPos, OldNewTuple> interactions = new HashMap<>();
    private long time;
    private UUID entity;
    private String level;

    public MultiBlockInteraction() {
    }

    public MultiBlockInteraction(long time, UUID entity, String level) {
        this.time = time;
        this.entity = entity;
        this.level = level;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void addBlock(BlockPos pos, String oldState, String newState) {
        if (oldState == null && newState == null) {
            return;
        }
        interactions.put(CustomBlockPos.fromBlockPos(pos), new OldNewTuple(oldState, newState));
    }

    public Map<BlockPos, OldNewTuple> getInteractions() {
        var toReturn = new HashMap<BlockPos, OldNewTuple>();
        interactions.keySet().forEach(key -> toReturn.put(key.toBlockPos(), interactions.get(key)));
        return toReturn;

    }

    private record CustomBlockPos(int x, int y, int z) implements Serializable {
        BlockPos toBlockPos() {
            return new BlockPos(x, y, z);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            if (this == o) return true;
            CustomBlockPos that = (CustomBlockPos) o;
            return x == that.x && y == that.y && z == that.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }

        static CustomBlockPos fromBlockPos(BlockPos creator) {
            return new CustomBlockPos(creator.getX(), creator.getY(), creator.getZ());
        }
    }
}
