package net.zytorx.minecraft.blocklog.logging.filter;

import net.minecraft.core.BlockPos;
import net.zytorx.minecraft.blocklog.cache.model.blocks.BlockInteraction;
import net.zytorx.minecraft.blocklog.cache.model.common.Interaction;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class Filter {
    private final int radius;
    private final String entity;
    private final UUID player;
    private int x;
    private int y;
    private int z;

    public Filter(int x, int y, int z, int radius, String entity, UUID player) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.entity = entity;
        this.player = player;
    }

    private boolean hasRadius() {
        return radius > 0;
    }

    private boolean hasPos() {
        return x != Integer.MIN_VALUE && y != Integer.MIN_VALUE && z != Integer.MIN_VALUE;
    }

    public void setPos(BlockPos pos) {
        if (!hasPos()) {
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
        }
    }

    public <T extends Interaction> Stream<T> filter(Stream<T> interactions) {
        var filtered = interactions;
        if (player != null) {
            filtered = filtered.filter(interaction -> interaction.getEntityID().equals(player));
        }
        if (entity != null && !entity.isBlank()) {
            filtered = filtered.filter(interaction -> interaction.getEntityName().equals(entity));
        }
        if (hasRadius() && hasPos()) {
            filtered = filtered.filter(interaction -> !(interaction instanceof BlockInteraction block) || isInRadius(block));
        } else if (hasPos()) {
            filtered = filtered.filter(interaction -> !(interaction instanceof BlockInteraction block) || isPos(block));
        }
        return filtered;
    }

    private boolean isInRadius(BlockInteraction interaction) {
        return isInRange(x - radius, x + radius, interaction.getX()) &&
                isInRange(y - radius, y + radius, interaction.getY()) &&
                isInRange(z - radius, z + radius, interaction.getZ());
    }

    private boolean isInRange(int min, int max, int toCheck) {
        return min <= toCheck && toCheck <= max;
    }

    private boolean isPos(BlockInteraction interaction) {
        return interaction.getX() == x && interaction.getY() == y && interaction.getZ() == z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filter filter = (Filter) o;
        return x == filter.x && y == filter.y && z == filter.z && radius == filter.radius && Objects.equals(entity, filter.entity) && Objects.equals(player, filter.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, radius, entity, player);
    }

    public static Filter blank() {
        return new Filter(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, 0, null, null);
    }
}
