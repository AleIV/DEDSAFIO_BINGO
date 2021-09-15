package me.aleiv.core.paper.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.GameStage;
import net.md_5.bungee.api.ChatColor;

public class LobbyListener implements Listener {

    Core instance;
    String notInGameMSG;

    World world = Bukkit.getWorld("lobby");
    
    public LobbyListener(Core instance) {
        this.instance = instance;

        notInGameMSG = ChatColor.of(instance.getGame().getColor1()) + "El juego no ha iniciado.";
    }

    public boolean shouldInteract(Player player){
        var game = instance.getGame();
        return (player.getWorld() == world && player.hasPermission("admin.perm")) || (player.getWorld() != world && game.getGameStage() != GameStage.INGAME && !player.hasPermission("admin.perm"));
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        var player = e.getPlayer();
        if (shouldInteract(player)) {
            e.setCancelled(true);
            instance.sendActionBar(player, notInGameMSG);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e){
        var player = (Player) e.getEntity();
        if (player.getWorld() == world) {
            e.setCancelled(true);
            instance.sendActionBar(player, notInGameMSG);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        var player = e.getPlayer();
        if (shouldInteract(player)) {
            e.setCancelled(true);
            instance.sendActionBar(player, notInGameMSG);
        }
    }

    @EventHandler
    public void onInventory(InventoryOpenEvent e) {
        var player = (Player) e.getPlayer();
        if (shouldInteract(player)) {
            e.setCancelled(true);
            instance.sendActionBar(player, notInGameMSG);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        var entity = e.getEntity();
        if (entity instanceof Player) {
            var player = (Player) entity;
            if (shouldInteract(player)) {
                e.setCancelled(true);
                instance.sendActionBar(player, notInGameMSG);
            }
        }

    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent e) {
        var damager = e.getDamager();

        if (damager instanceof Player) {
            var player = (Player) damager;
            if (shouldInteract(player)) {
                e.setCancelled(true);
                instance.sendActionBar(player, notInGameMSG);
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
        var loc = new Location(lobby, 71, 126, 0, 90, -0);

        if(game.getGameStage() != GameStage.INGAME){
            player.teleport(loc);

        }else if(player.getWorld() == lobby || table == null){
            
            player.setGameMode(GameMode.SPECTATOR);
        }
    }


}
