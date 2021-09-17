package me.aleiv.core.paper.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Data;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.teams.objects.Team;
import me.aleiv.core.paper.utilities.Frames;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;
import net.md_5.bungee.api.ChatColor;

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

    public void runScatter(){

        var manager = instance.getTeamManager();
        var game = instance.getGame();

        if(manager.isTeams()){

        }else{

            if(safeLocations.isEmpty()){
                instance.broadcastMessage(ChatColor.of(game.getColor3()) + "Not generated locations.");
            }

            var players = Bukkit.getOnlinePlayers();

            
            var count = 0;
            for (var player : players) {
                Location loc;

                if(count < safeLocations.size()){
                    loc = safeLocations.get(count);

                }else{
                    loc = generateLocation();
                }

                Qteleport(player, loc);
                count++;
            }
            

        }
    }

    public void Qteleport(Player player, Location loc){
        var preTeleport = Frames.getFramesCharsIntegers(261, 308);
        var postTeleport = Frames.getFramesCharsIntegers(310, 359);
        var fullTeleport = Character.toString('\uE309');
        var startTeleport = Character.toString('\uE260');

        var task = new BukkitTCT();

        task.addWithDelay(new BukkitRunnable(){
            @Override
            public void run(){
                instance.showTitle(player, startTeleport + "", "", 0, 20, 0);
                player.playSound(loc, "teleport1", 1, 1);

            }
            
        }, 50);

        preTeleport.forEach(frame->{
            task.addWithDelay(new BukkitRunnable(){
                @Override
                public void run(){
                    instance.showTitle(player, frame + "", "", 0, 20, 0);
                }
                
            }, 50);
        });

        task.addWithDelay(new BukkitRunnable(){
            @Override
            public void run(){
                instance.showTitle(player, fullTeleport + "", "", 0, 20, 0);
                player.teleport(loc);
                player.playSound(loc, "teleport2", 1, 1);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20*5, 20));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*20, 20));

            }
            
        }, 50);

        postTeleport.forEach(frame->{
            task.addWithDelay(new BukkitRunnable(){
                @Override
                public void run(){
                    instance.showTitle(player, frame + "", "", 0, 20, 0);
                }
                
            }, 50);
        });


        task.execute();

    }

    public boolean isSafe(final Location loc) {
        var biome = loc.getBlock().getBiome().toString();
        return !biome.contains("OCEAN");
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

    public Location generateLocation(){
        var world = Bukkit.getWorlds().get(0);

        var loc = genLoc(world);
        while(!isSafeLocation(loc)){
            loc = genLoc(world);
        }
        loc.setY(200f);

        return loc;
    }

    public int getR(int i){
        var neg = random.nextBoolean();
        var rand = random.nextInt(i)+1;
        return neg ? rand*1 : rand*-1;
    }

    public Location genLoc(final World world){
        var worldBorder = (int) world.getWorldBorder().getSize()/2;
        var loc = new Location(world, getR(worldBorder), 0, getR(worldBorder));
        loc.setY(world.getHighestBlockYAt(loc));
        return loc;
    }


}
