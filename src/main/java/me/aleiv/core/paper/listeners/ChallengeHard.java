package me.aleiv.core.paper.listeners;

import java.util.List;
import java.util.UUID;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;

import me.aleiv.core.paper.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mule;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.Zoglin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.Challenge;

public class ChallengeHard implements Listener{
    
    Core instance;

    private final List<EntityType> flyingList = List.of(EntityType.BEE, EntityType.BAT, EntityType.PARROT,
        EntityType.GHAST, EntityType.BLAZE, EntityType.PHANTOM, EntityType.WITHER);
    private final List<String> invalidPotions = List.of("WATER", "AWKWARD", "MUNDANE", "THICK");

    public ChallengeHard(Core instance){
        this.instance = instance;
    }

    @EventHandler
    public void onKill(EntityDeathEvent e){
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.SNOWBALL_BLAZE_KILL) || !game.isChallengeEnabledFor(Challenge.OVER_ZOGLIN)
            || !game.isChallengeEnabledFor(Challenge.FLYING_MOBS_KILL) || !game.isChallengeEnabledFor(Challenge.NETHER_MOB_KILL)
            || !game.isChallengeEnabledFor(Challenge.PIG_FALL)) return;
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
        if(flyingList.contains(entity.getType())){
            var player = entity.getKiller();
            if(player != null){
                manager.attempToFind(player, Challenge.FLYING_MOBS_KILL, entity.getType().toString());
            }

        }
        if(entity.getWorld().getEnvironment() == Environment.NETHER){
            var player = entity.getKiller();
            
            if(player != null){
                var biome = entity.getLocation().getBlock().getBiome().toString();
                manager.attempToFind(player, Challenge.NETHER_MOB_KILL, biome);
            }

        }
        if(entity instanceof Pig pig && !pig.getPassengers().isEmpty() && pig.getPassengers().get(0) instanceof Player player){

            if(entity.getLastDamageCause().getCause() == DamageCause.FALL){
                manager.attempToFind(player, Challenge.PIG_FALL, "");
            }
            
        }
    }

    @EventHandler
    public void damageEvent(EntityDamageByEntityEvent e){
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.GLOWING_BAT)) return;

        var entity = e.getEntity();
        var damager = e.getDamager();
        
        if(entity instanceof Bat bat && damager instanceof SpectralArrow arrow 
            && arrow.getShooter() != null && arrow.getShooter() instanceof Player player){
                
                var manager = instance.getBingoManager();
                
                manager.attempToFind(player, Challenge.GLOWING_BAT, "");
        }
    }

    @EventHandler
    public void onCatch(PlayerItemConsumeEvent e){
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.EAT_SUS_STEW) || !game.isChallengeEnabledFor(Challenge.POTION_TYPES)) return;

        var item = e.getItem();
        if(item.getType() == Material.SUSPICIOUS_STEW){
            var meta = (SuspiciousStewMeta) item.getItemMeta();
            var list = meta.getCustomEffects();
            if(!list.isEmpty()){
                var effect = list.get(0).toString().split(" ");
                var str = new StringBuilder();
                for (var string : effect) {
                    str.append(string);
                }
                var nStr = str.toString().split(":");

                var player = e.getPlayer();
                var manager = instance.getBingoManager();
                        
                manager.attempToFind(player, Challenge.EAT_SUS_STEW, nStr[0]);
            }
        } else if (e.getItem().getType() == Material.POTION) {
            var player = e.getPlayer();
            PotionMeta potionMeta = (PotionMeta) e.getItem().getItemMeta();
            String potionType = potionMeta.getBasePotionData().getType().toString();
            if (!invalidPotions.contains(potionType)) {
                var manager = instance.getBingoManager();
                var table = manager.findTable(player.getUniqueId());
                if (table != null) {
                    manager.attempToFind(player, Game.Challenge.POTION_TYPES, potionType);
                }
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e){
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.ENDER_PEARL_TRAVEL)) return;

        var cause = e.getCause();
        if(cause == TeleportCause.ENDER_PEARL && getDistance(e.getFrom(), e.getTo()) >= 300){
            var manager = instance.getBingoManager();
            var player = e.getPlayer();
            manager.attempToFind(player, Challenge.ENDER_PEARL_TRAVEL, "");
        }
    }

    private double getDistance(Location loc1, Location loc2) {
        final int x1 = (int) loc1.getX();
        final int z1 = (int) loc1.getZ();

        final int x2 = (int) loc2.getX();
        final int z2 = (int) loc2.getZ();

        return Math.abs(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(z2 - z1, 2)));
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

        }else if(hand != null && hand.getType() == Material.LINGERING_POTION){
            var manager = instance.getBingoManager();
            var table = manager.findTable(player.getUniqueId());

            if (table != null) {
                
                manager.attempToFind(player, Challenge.LINGERING_WATER_POTION, "");
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
