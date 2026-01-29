package com.github.skriptdev.skript.api.hytale;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a block in a world.
 * Hytale doesn't appear to have a representation of a block in a world.
 * This class provides a wrapper around Hytale's block system, allowing for easy interaction with blocks in a world.
 * This may be changed/removed in the future.
 */
@SuppressWarnings("unused")
public class Block {

    private final @NotNull World world;
    private @NotNull BlockType type;
    private final @NotNull Vector3i pos;

    public Block(@NotNull World world, @NotNull Vector3i pos, @NotNull BlockType type) {
        this.world = world;
        this.pos = pos;
        this.type = type;
    }

    public @NotNull BlockType getType() {
        return this.type;
    }

    public void setType(@NotNull BlockType type) {
        this.type = type;
        this.world.setBlock(this.pos.getX(), this.pos.getY(), this.pos.getZ(), type.getId());
    }

    public void breakBlock() {
        int setting = 0; // TODO not sure what to actually use here
        this.world.breakBlock(this.pos.getX(), this.pos.getY(), this.pos.getZ(), setting);
    }

    public @NotNull World getWorld() {
        return this.world;
    }

    public @NotNull Vector3i getPos() {
        return this.pos;
    }

    public String toTypeString() {
        return String.format("[%s] block at (%s,%s,%s) in '%s'",
            this.type.getId(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.world.getName());
    }

    @Override
    public String toString() {
        return "Block{" +
            "world=" + world.getName() +
            ", type=" + type +
            ", pos=" + pos +
            '}';
    }

}
