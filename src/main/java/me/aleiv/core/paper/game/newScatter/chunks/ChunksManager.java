package me.aleiv.core.paper.game.newScatter.chunks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitTask;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.aleiv.core.paper.Core;

public class ChunksManager {
    public static ConcurrentHashMap<String, Chunk> chunksForceLoaded = new ConcurrentHashMap<>();
    private @NonNull @Getter Core instance;

    private @Getter @Setter int distanceThresHold = 100;

    private final @Getter ArrayList<Location> locations = new ArrayList<>();

    private final @Getter LinkedList<ChunkLoadTask> pendingChunkLoadTasks = new LinkedList<>();
    private @Getter BukkitTask autoChunkScheduler;

    public ChunksManager(Core instance) {
        this.instance = instance;

    }

    public static Collection<ChunkObject> getNeighbouringChunks(final ChunkObject chunkObject, final int distance) {
        return getNeighbouringChunks(chunkObject.getX(), chunkObject.getZ(), distance);
    }

    public static Collection<ChunkObject> getNeighbouringChunks(final int x, final int z, final int distance) {
        var chunksCollection = new ArrayList<ChunkObject>();

        var size = (distance * 2) + 1;
        var offsetX = x - distance;
        var offsetZ = z + distance;

        for (var xx = 0; xx < size; xx++)
            for (var zz = 0; zz < size; zz++)
                chunksCollection.add(ChunkObject.of(offsetX + xx, offsetZ - zz));

        return chunksCollection;
    }

    public static Location findScatterLocation(final World world, final int radius) {
        Location loc = new Location(world, 0, 0, 0);
        // Use Math#Random to obtain a random integer that can be used as a location.
        loc.setX(loc.getX() + Math.random() * radius * 2.0 - radius);
        loc.setZ(loc.getZ() + Math.random() * radius * 2.0 - radius);
        loc = loc.getWorld().getHighestBlockAt(loc).getLocation();

        if (!isSafe(loc)) {
            return findScatterLocation(world, radius);
        }
        // A location object is returned once we reach this step, next step is to
        // validate the location from others.
        return centerLocation(loc);
    }

    public static Location findScatterLocation(final World world, final int radius, int max) {
        Location loc = new Location(world, 0, 0, 0);
        // Use Math#Random to obtain a random integer that can be used as a location.
        loc.setX(loc.getX() + Math.random() * radius * 2.0 - radius);
        loc.setZ(loc.getZ() + Math.random() * radius * 2.0 - radius);
        loc = loc.getWorld().getHighestBlockAt(loc).getLocation();

        if (!isSafe(loc) && ++max < 5) {
            return findScatterLocation(world, radius, max);
        }
        // A location object is returned once we reach this step, next step is to
        // validate the location from others.
        return centerLocation(loc);
    }

    public static Location findLateScatterLocation(final World world) {
        return findScatterLocation(world, (int) (world.getWorldBorder().getSize() / 2));
    }

    public static Location centerLocation(final Location loc) {
        loc.setX(loc.getBlockX() + 0.5);
        loc.setY(loc.getBlockY() + 1.5);
        loc.setZ(loc.getBlockZ() + 0.5);
        return loc;
    }

    public static boolean isSafe(final Location loc) {
        return !(loc.getBlock().isLiquid() || loc.getBlock().getRelative(BlockFace.DOWN).isLiquid()
                || loc.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).isLiquid());
    }

}