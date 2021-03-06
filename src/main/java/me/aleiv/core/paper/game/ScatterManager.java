package me.aleiv.core.paper.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Data;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.game.newScatter.chunks.ChunkLoadTask;
import me.aleiv.core.paper.game.newScatter.chunks.ChunksManager;
import me.aleiv.core.paper.game.newScatter.chunks.CoordinatePair;
import me.aleiv.core.paper.game.objects.Teleporter;
import me.aleiv.core.paper.teams.objects.Team;
import me.aleiv.core.paper.utilities.Frames;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;

@Data
public class ScatterManager {
    Core instance;

    Random random = new Random();

    List<Location> safeLocations = new ArrayList<>();

    int minSafeLocation = 500;
    List<Character> preTeleport = Frames.getFramesCharsIntegers(261, 308);
    List<Character> postTeleport = Frames.getFramesCharsIntegers(310, 359);
    String fullTeleport = Character.toString('\uE309');
    String startTeleport = Character.toString('\uE260');

    private ChunksManager chunkmanager;

    public ScatterManager(Core instance) {
        this.instance = instance;
        this.chunkmanager = new ChunksManager(instance);

    }

    public List<Team> getTeamsForScatter() {
        return null;
    }

    public CompletableFuture<Boolean> loadAllChunks() {

        var taskChain = new BukkitTCT();
        //Combine the saved locations 
        var chunks = transformLocationsToLoadTask();
        
        taskChain.add(() -> {
            var iter = chunks.iterator();

            while (iter.hasNext()) {
                var next = iter.next();
                // Run the task one tick later
                Bukkit.getScheduler().runTaskLaterAsynchronously(instance, next, 1);

                while (!next.isDone()) {
                    // Hold for one second to check again if the previous load task is done
                    System.out.println("Waiting for chunk " +next.getLocationID() +" to load");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        return taskChain.execute();

    }

    public List<ChunkLoadTask> transformLocationsToLoadTask() {
        List<ChunkLoadTask> loadTasks = new ArrayList<>();
        // Loop through all the locations of safeLocations
        for (var loc : safeLocations) {
            loadTasks.add(fromLocationToLoadTask(loc));
        }
        return loadTasks;
    }

    public ChunkLoadTask fromLocationToLoadTask(Location loc) {
        return new ChunkLoadTask(loc.getWorld(), chunkmanager, CoordinatePair.fromLocation(loc));
    }

    public void runKernelScatter() {
        var teamsOnline = instance.getTeamManager().getTeamsOnlineList();
        var scatter = instance.getScatterManager();
        HashMap<Player, Location> map = new HashMap<>();

        teamsOnline.forEach(team -> {
            var list = team.getPlayerStream().toList();

            Location loc;
            if (safeLocations.size() == 0) {
                loc = scatter.generateLocation();

            } else {
                loc = safeLocations.get(0);
                safeLocations.remove(0);
            }

            list.forEach(player -> {
                map.put(player, loc);
            });

        });

        Teleporter bukkitTask = new Teleporter(instance, map);
        bukkitTask.runTaskTimerAsynchronously(instance, 0L, 5L);

    }

    public void runScatter() {
        var teamsOnline = instance.getTeamManager().getTeamsOnlineList();
        var task = new BukkitTCT();

        task.addWithDelay(new BukkitRunnable() {
            @Override
            public void run() {
                teamsOnline.forEach(team -> {
                    team.getPlayerStream().forEach(player -> {
                        var loc = player.getLocation();
                        instance.showTitle(player, startTeleport + "", "", 0, 20, 0);
                        player.playSound(loc, "bingo.tpstart", 1, 1);
                    });
                });
            }
        }, 50);

        preTeleport.forEach(frame -> {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    teamsOnline.forEach(team -> {
                        team.getPlayerStream().forEach(player -> {
                            instance.showTitle(player, frame + "", "", 0, 20, 0);
                        });
                    });

                }

            }, 50);
        });

        task.addWithDelay(new BukkitRunnable() {
            @Override
            public void run() {
                runKernelScatter();
            }

        }, 50);

        postTeleport.forEach(frame -> {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    teamsOnline.forEach(team -> {
                        team.getPlayerStream().forEach(player -> {
                            instance.showTitle(player, frame + "", "", 0, 20, 0);
                        });
                    });
                }

            }, 50);
        });

        task.execute();
    }

    public CompletableFuture<Boolean> Qteleport(Player player, Location loc) {
        var task = new BukkitTCT();

        var location = loc.clone();
        location.setY(200);

        task.addWithDelay(new BukkitRunnable() {
            @Override
            public void run() {

                instance.showTitle(player, startTeleport + "", "", 0, 20, 0);
                player.playSound(location, "bingo.tpstart", 1, 1);

            }

        }, 50);

        preTeleport.forEach(frame -> {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    instance.showTitle(player, frame + "", "", 0, 20, 0);
                }

            }, 50);
        });

        task.addWithDelay(new BukkitRunnable() {
            @Override
            public void run() {
                instance.showTitle(player, fullTeleport + "", "", 0, 20, 0);
                
                player.teleport(location);

                player.setGameMode(GameMode.SURVIVAL);
                player.playSound(location, "bingo.tpfinish", 1, 1);

                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 20, 20));
                player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20 * 10000, 255));

            }

        }, 50);

        postTeleport.forEach(frame -> {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    instance.showTitle(player, frame + "", "", 0, 20, 0);
                }

            }, 50);
        });

        return task.execute();

    }

    public CompletableFuture<Boolean> QteleportS(Player player, Location loc) {
        var task = new BukkitTCT();

        task.addWithDelay(new BukkitRunnable() {
            @Override
            public void run() {

                instance.showTitle(player, startTeleport + "", "", 0, 20, 0);
                player.playSound(loc, "bingo.tpstart", 1, 1);

            }

        }, 50);

        preTeleport.forEach(frame -> {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    instance.showTitle(player, frame + "", "", 0, 20, 0);
                }

            }, 50);
        });

        task.addWithDelay(new BukkitRunnable() {
            @Override
            public void run() {
                instance.showTitle(player, fullTeleport + "", "", 0, 20, 0);
                player.teleport(loc);
                player.setGameMode(GameMode.SURVIVAL);
                player.playSound(loc, "bingo.tpfinish", 1, 1);

                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 20, 20));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 5, 20));

            }

        }, 50);

        postTeleport.forEach(frame -> {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    instance.showTitle(player, frame + "", "", 0, 20, 0);
                }

            }, 50);
        });

        return task.execute();

    }

    public boolean isSafe(final Location loc) {
        var biome = loc.getBlock().getBiome().toString();
        return !biome.contains("OCEAN");
    }

    public boolean isSafeDistance(final Location loc) {

        for (Location location : safeLocations) {
            if (getDistance(loc, location) <= minSafeLocation)
                return false;
        }

        return true;
    }

    private double getDistance(Location loc1, Location loc2) {
        final int x1 = (int) loc1.getX();
        final int z1 = (int) loc1.getZ();

        final int x2 = (int) loc2.getX();
        final int z2 = (int) loc2.getZ();

        return Math.abs(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(z2 - z1, 2)));
    }

    public boolean isSafeLocation(Location loc) {
        return isSafe(loc) && isSafeDistance(loc);
    }

    public void generateLocations(int i) {
        var world = Bukkit.getWorlds().get(0);
        for (int j = 0; j < i; j++) {
            var loc = genLoc(world);
            while (!isSafeLocation(loc)) {
                loc = genLoc(world);
            }
            safeLocations.add(loc);
        }
    }

    public Location generateLocation() {
        var world = Bukkit.getWorlds().get(0);

        var loc = genLoc(world);
        while (!isSafeLocation(loc)) {
            loc = genLoc(world);
        }
        loc.setY(200);

        return loc;
    }

    public int getR(int i) {
        var neg = random.nextBoolean();
        var rand = random.nextInt(i) + 1;
        return neg ? rand * 1 : rand * -1;
    }

    public Location genLoc(final World world) {
        var worldBorder = (int) world.getWorldBorder().getSize() / 2;
        var loc = new Location(world, getR(worldBorder), 0, getR(worldBorder));
        loc.setY(world.getHighestBlockYAt(loc));
        return loc;
    }

}
