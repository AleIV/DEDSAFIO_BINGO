package me.aleiv.core.paper.listeners;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import io.papermc.paper.event.player.PlayerTradeEvent;

import me.aleiv.core.paper.Game;
import me.aleiv.core.paper.Core;

public class ChallengeMedium implements Listener{
    
    Core instance;

    public ChallengeMedium(Core instance){
        this.instance = instance;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        var game = instance.getGame();
        if (game.getBingoFase() != Game.BingoFase.CHALLENGE && game.getBingoRound() != Game.BingoRound.TWO)
            return;

        if (event.getPlayer() instanceof Player player) {
            long stacks = Arrays.stream(player.getInventory().getContents())
                    .filter(i -> i != null && i.getAmount() == 64).count();
            if (stacks >= 36) {
                var manager = instance.getBingoManager();
                var table = manager.findTable(player.getUniqueId());
                if (table != null) {
                    manager.attempToFind(player, Game.Challenge.INVENTORY_STACKS, "");
                }
            }
        }
    }

    @EventHandler
    public void onItemConsumed(PlayerItemConsumeEvent event) {
        var game = instance.getGame();
        if (game.getBingoFase() != Game.BingoFase.CHALLENGE && game.getBingoRound() != Game.BingoRound.TWO)
            return;

        Player player = event.getPlayer();
        if (event.getItem().getType() == Material.GOLDEN_APPLE) {
            if (player.getVehicle() != null && player.getVehicle() instanceof Strider) {
                var manager = instance.getBingoManager();
                var table = manager.findTable(player.getUniqueId());
                if (table != null) {
                    manager.attempToFind(player, Game.Challenge.STRIDER_GAPPLE, "");
                }
            }
        }
    }

    @EventHandler
    public void onEntityDropItem(EntityDropItemEvent event) {
        var game = instance.getGame();
        if (game.getBingoFase() != Game.BingoFase.CHALLENGE && game.getBingoRound() != Game.BingoRound.TWO)
            return;

        if (event.getEntity() instanceof Piglin piglin) {
            var manager = instance.getBingoManager();
            Bukkit.getScheduler().runTaskLater(instance, task -> piglin.getNearbyEntities(10, 10, 10).stream()
                    .filter(e -> e instanceof Player)
                    .forEach(e -> {
                        var player = (Player) e;
                        var table = manager.findTable(player.getUniqueId());
                        if (table != null) {
                            manager.attempToFind(player, Game.Challenge.PIGLIN_BARTER, "");
                        }
                    }), 1);
        }
    }

    @EventHandler
    public void onTrading(PlayerTradeEvent event) {
        var game = instance.getGame();
        if (game.getBingoFase() != Game.BingoFase.CHALLENGE && game.getBingoRound() != Game.BingoRound.TWO)
            return;

        Player player = event.getPlayer();
        if (event.getVillager() instanceof Villager villager && villager.getVillagerLevel() >= 5) {
            var manager = instance.getBingoManager();
            var table = manager.findTable(player.getUniqueId());
            if (table != null) {
                manager.attempToFind(player, Game.Challenge.VILLAGER_MAX_TRADE, "");
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        var game = instance.getGame();
        if (game.getBingoFase() != Game.BingoFase.CHALLENGE && game.getBingoRound() != Game.BingoRound.TWO)
            return;

        if (event.getEntity() instanceof Player player && event.getDamager() instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player shooter) {
                if (shooter.equals(player)) return;
                var manager = instance.getBingoManager();
                var table = manager.findTable(player.getUniqueId());
                if (table != null) {
                    Bukkit.getScheduler().runTaskLater(instance, task -> {
                        if (table.getPlayerStream().collect(Collectors.toList()).contains(shooter)) {
                            String data = shooter.getUniqueId() + ";" + player.getUniqueId() + ";" + projectile.getUniqueId();
                            manager.attempToFind(player, Game.Challenge.CROSSBOW_SHOT, data);
                        }
                    }, 1);
                }
            }
        }
    }

}
