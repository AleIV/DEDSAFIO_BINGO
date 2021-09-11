package me.aleiv.core.paper.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import lombok.Data;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.teams.objects.Team;

@Data
public class ScatterManager {
    Core instance;

    Random random = new Random();

    List<Location> safeLocations = new ArrayList<>();

    int minSafeLocation = 500;

    public ScatterManager(Core instance){
        this.instance = instance;

        
    }

    public List<Team> getTeamsForScatter(){
        return null;
    }

    public void runScatter(boolean teamScatter){

        var manager = instance.getTeamManager();

        if(manager.isTeams()){

        }else{
            var players = Bukkit.getOnlinePlayers();
            var size = players.size();
            generateLocations(size*2);
            instance.broadcastMessage("GENERATED " + players + " LOCATIONS");

            var count = 0;
            for (var loc : safeLocations) {
                var playerList = players.stream().collect(Collectors.toList());
                playerList.get(count).teleport(loc);
                count++;
            }
            

        }
    }

    public boolean isSafe(final Location loc) {
        return !(loc.getBlock().isLiquid() || loc.getBlock().getRelative(BlockFace.DOWN).isLiquid()
                || loc.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).isLiquid());
    }

    public boolean isSafeDistance(final Location loc){

        for (Location location : safeLocations) {
            if(getDistance(loc, location) <= minSafeLocation)
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

    public boolean isSafeLocation(Location loc){
        return isSafe(loc) && isSafeDistance(loc);
    }

    public void generateLocations(int i){

        var world = Bukkit.getWorlds().get(0);
        for (int j = 0; j < i; j++) {
            var loc = genLoc(world);
            while(!isSafeLocation(loc)){
                loc = genLoc(world);
            }
            safeLocations.add(loc);
        }
    }

    public int getR(int i){
        var neg = random.nextBoolean();
        var rand = random.nextInt(i)+1;
        return neg ? rand*1 : rand*-1;
    }

    public Location genLoc(final World world){
        var worldBorder = (int) world.getWorldBorder().getSize();
        var loc = new Location(world, getR(worldBorder), 0, getR(worldBorder));
        loc.setY(world.getHighestBlockYAt(loc));
        return loc;
    }


}
