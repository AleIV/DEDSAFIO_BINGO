package me.aleiv.core.paper.listeners;

import me.aleiv.core.paper.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.aleiv.core.paper.Core;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

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

}
