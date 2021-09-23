package me.aleiv.core.paper.listeners;

import me.aleiv.core.paper.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Strider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.aleiv.core.paper.Core;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.Arrays;

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

}
