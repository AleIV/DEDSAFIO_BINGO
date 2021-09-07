package me.aleiv.core.paper.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.GameStage;
import net.md_5.bungee.api.ChatColor;

public class LobbyListener implements Listener {

    Core instance;
    String notInGameMSG;

    public LobbyListener(Core instance) {
        this.instance = instance;

        notInGameMSG = ChatColor.of(instance.getGame().getColor1()) + "Game must start.";
    }


    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        var player = e.getPlayer();
        var game = instance.getGame();
        if (game.getGameStage() != GameStage.INGAME) {
            e.setCancelled(true);
            instance.sendActionBar(player, notInGameMSG);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        var player = e.getPlayer();
        var game = instance.getGame();
        if (game.getGameStage() != GameStage.INGAME) {
            e.setCancelled(true);
            instance.sendActionBar(player, notInGameMSG);
        }
    }

    @EventHandler
    public void onInventory(InventoryOpenEvent e) {
        var player = (Player) e.getPlayer();
        var game = instance.getGame();
        if (game.getGameStage() != GameStage.INGAME) {
            e.setCancelled(true);
            instance.sendActionBar(player, notInGameMSG);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        var entity = e.getEntity();
        if (entity instanceof Player) {
            var player = (Player) entity;
            var game = instance.getGame();
            if (game.getGameStage() != GameStage.INGAME) {
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
            var game = instance.getGame();
            if (game.getGameStage() != GameStage.INGAME) {
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

        if(game.getGameStage() != GameStage.INGAME){
            player.teleport(lobby.getSpawnLocation());

        }else if(player.getWorld() == lobby || table == null){
            player.setGameMode(GameMode.SPECTATOR);
        }
    }


}
