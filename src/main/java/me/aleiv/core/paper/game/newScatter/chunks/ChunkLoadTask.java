package me.aleiv.core.paper.game.newScatter.chunks;

import java.util.UUID;
import java.util.concurrent.Semaphore;

import org.bukkit.Location;
import org.bukkit.World;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public @RequiredArgsConstructor class ChunkLoadTask implements Runnable {
    private @Getter UUID locationID = UUID.randomUUID();
    private @NonNull @Getter World world;
    private @NonNull ChunksManager chunksManager;
    private @Getter boolean isDone = false;
    private @Getter boolean isRunning = false;
    private @NonNull CoordinatePair pair;
    private @Getter int chunksLeft = -1;
    private static @Getter @Setter int radius = 3;

    @Override
    public void run() {
        isRunning = true;
        var coordinatePair = pair;
        // Once this point is reach, lots of chunks must be loaded.
        var neighbours = ChunksManager.getNeighbouringChunks(coordinatePair.toChunkObject(), radius);
        chunksLeft = neighbours.size();
        var iterator = neighbours.iterator();
        var semaphore = new Semaphore(50);
        // Start iterating.
        while (iterator.hasNext()) {
            try {
                semaphore.acquire();
                var chunkObj = iterator.next();
                world.getChunkAtAsync(chunkObj.getX(), chunkObj.getZ()).thenAccept(chunk -> {
                    semaphore.release();
                    // Set forceloaded
                    chunk.setForceLoaded(true);
                    // Keep track of the chunks force loaded
                    ChunksManager.chunksForceLoaded.put(chunk.toString(), chunk);
                    chunksLeft--;
                    // Changed 1 to 5 to allow for error margin and avoid getting stuck
                    if (chunksLeft <= 5) {
                        isDone = true;
                    }
                    // Notify every 25 chunks to reduce spam.
                    if (chunksLeft % 25 == 0) {
                        System.out.println("Another chunk loaded for task " + getLocationID().toString() + ". "
                                + chunksLeft + " left.");
                    }

                });
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }

    }

    CoordinatePair getRandomCoordinatePair(int min, int max) {
        return CoordinatePair.of((int) ((Math.random() * (max - min)) + min),
                (int) ((Math.random() * (max - min)) + min));
    }

    CoordinatePair getCoordinatePair(Location loc) {
        return CoordinatePair.of(loc.getBlockX(), loc.getBlockZ());
    }

}