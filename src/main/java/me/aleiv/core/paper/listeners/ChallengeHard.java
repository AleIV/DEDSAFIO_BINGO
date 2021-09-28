package me.aleiv.core.paper.listeners;

import java.util.List;
import java.util.UUID;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Mule;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Zoglin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.Challenge;

public class ChallengeHard implements Listener{
    
    Core instance;

    public ChallengeHard(Core instance){
        this.instance = instance;
    }

    @EventHandler
    public void onKill(EntityDeathEvent e){
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.SNOWBALL_BLAZE_KILL) || !game.isChallengeEnabledFor(Challenge.OVER_ZOGLIN)) return;
        var manager = instance.getBingoManager();

        var entity = e.getEntity();
        if(entity instanceof Blaze blaze){
            var cause = blaze.getLastDamageCause();
            if(cause != null && cause instanceof EntityDamageByEntityEvent entityDamage 
                && entityDamage.getDamager() instanceof Snowball snowball && snowball.getShooter() != null 
                && snowball.getShooter() instanceof Player player){

                manager.attempToFind(player, Challenge.SNOWBALL_BLAZE_KILL, "");
                
            }

        }else if(entity instanceof Zoglin zoglin){
            var player = entity.getKiller();
            if(player != null){
                manager.attempToFind(player, Challenge.OVER_ZOGLIN, "");
            }
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent e){
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.TEAM_DANCE) || !game.isChallengeEnabledFor(Challenge.LINGERING_WATER_POTION)) return;

        var player = e.getPlayer();
        var block = e.getClickedBlock();
        var hand = player.getInventory().getItemInMainHand();
        
        if(block != null && hand != null && block.getType() == Material.JUKEBOX && hand.getType().toString().contains("MUSIC_DISC")){
            var manager = instance.getBingoManager();
            var loc = player.getLocation();
            var table = manager.findTable(player.getUniqueId());
    
            if (table != null) {
                Bukkit.getScheduler().runTaskLater(instance, task ->{
                    var nearbyTeam = loc.getNearbyPlayers(10).stream().map(p -> p.getUniqueId()).toList();
                    var uuidsTeam = table.getMembers();
                    if(isTeamNearby(uuidsTeam, nearbyTeam)){
                        manager.attempToFind(player, Challenge.TEAM_DANCE, "");
                    }
                }, 20*5);
            }
        }
    }

    @EventHandler
    public void onBreed(EntityBreedEvent e) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.OBTAIN_MULE)) return;

        var breader = e.getBreeder();

        if (breader != null && breader instanceof Player player && e.getEntity() instanceof Mule) {
            var manager = instance.getBingoManager();
            manager.attempToFind(player, Challenge.OBTAIN_MULE, "");
            
        }

    }

    @EventHandler
    public void onSkyHigh(PlayerJumpEvent e) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.END_BINGO_MAX_HEIGHT)) return;

        var manager = instance.getBingoManager();
        var player = e.getPlayer();
        var loc = player.getLocation();
        var table = manager.findTable(player.getUniqueId());

        if (loc.getY() >= 255 && table != null && table.getObjectsFound() == 24) {
            var nearbyTeam = loc.getNearbyPlayers(10).stream().map(p -> p.getUniqueId()).toList();
            var uuidsTeam = table.getMembers();
            if(isTeamNearby(uuidsTeam, nearbyTeam)){
                manager.attempToFind(player, Challenge.END_BINGO_MAX_HEIGHT, "");
            }
            
        }
    }

    public boolean isTeamNearby(List<UUID> players, List<UUID> uuidsTeam){
        for (UUID uuid : players) {
            if(!uuidsTeam.contains(uuid)){
                return false;
            }
        }
        return true;
    }

    

    
}
