package me.aleiv.core.paper.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.GameStage;

public class LobbyListener implements Listener {

    Core instance;
    
    public LobbyListener(Core instance) {
        this.instance = instance;
    }

    public boolean shouldInteract(Player player){
        var lobby = Bukkit.getWorld("lobby");
        var game = instance.getGame();

        if(player.getWorld() == lobby && !player.hasPermission("admin.perm")){
            return false;

        }else if(player.getWorld() != lobby && game.getGameStage() != GameStage.INGAME && !player.hasPermission("admin.perm")){
            return false;
        }

        return true;
    }

    @EventHandler
    public void sit(PlayerInteractAtEntityEvent e){
        var entity = e.getRightClicked();
        var player = e.getPlayer();
        
        if(entity != null && entity instanceof ArmorStand stand && !stand.hasBasePlate() && stand.getPassengers().isEmpty()){
            stand.addPassenger(player);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        var player = e.getPlayer();
        if (!shouldInteract(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e){
        var world = Bukkit.getWorld("lobby");
        var player = (Player) e.getEntity();
        if (player.getWorld() == world) {
            player.setFoodLevel(20);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        var player = e.getPlayer();
        if (!shouldInteract(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventory(InventoryOpenEvent e) {
        var player = (Player) e.getPlayer();
        if (!shouldInteract(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        var entity = e.getEntity();
        if (entity instanceof Player) {
            var player = (Player) entity;
            if (!shouldInteract(player)) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent e) {
        var damager = e.getDamager();

        if (damager instanceof Player) {
            var player = (Player) damager;
            if (!shouldInteract(player)) {
                e.setCancelled(true);
            }
        }

    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        var player = e.getPlayer();
        var game = instance.getGame();
        var manager = instance.getBingoManager();
        var table = manager.findTable(player.getUniqueId());
        var lobby = Bukkit.getWorld("lobby");
        var loc = new Location(lobby, 71.5, 126, 0.5, 90, -0);

        if(game.getGameStage() != GameStage.INGAME){
            player.teleport(loc);

        }else if(table == null){
            
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        var entity = e.getEntity();
        if (entity instanceof Player player) {
            if(!shouldInteract(player)){
                e.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onDroppedItem(PlayerDropItemEvent e) {
        var player = e.getPlayer();
        if (!shouldInteract(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickedUpItems(PlayerAttemptPickupItemEvent e) {
        var player = e.getPlayer();
        if (!shouldInteract(player)) {
            e.setCancelled(true);
        }
    }


}
