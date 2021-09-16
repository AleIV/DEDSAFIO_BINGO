package me.aleiv.core.paper.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.events.BingoEvent;
import me.aleiv.core.paper.events.FoundItemEvent;
import net.md_5.bungee.api.ChatColor;

public class InGameListener implements Listener {

    Core instance;

    public InGameListener(Core instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onItem(FoundItemEvent e){
        var game = instance.getGame();
        var slot = e.getSlot();
        var player = e.getPlayer();
        instance.broadcastMessage(ChatColor.of(game.getColor2()) + player.getName() + " found " + ChatColor.RESET + slot.getDisplay());
        instance.broadcastMessage("");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPick(EntityPickupItemEvent e) {
        var entity = e.getEntity();
        if (entity instanceof Player) {
            var player = (Player) entity;
            var item = e.getItem().getItemStack();
            instance.getBingoManager().attempToFind(player, item);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void clickInv(InventoryClickEvent e) {
        var player = (Player) e.getWhoClicked();
        var item = e.getCurrentItem();
        instance.getBingoManager().attempToFind(player, item);

    }

    @EventHandler
    public void onBingo(BingoEvent e) {
        var game = instance.getGame();
        Bukkit.getScheduler().runTask(instance, () -> {
            instance.broadcastMessage(ChatColor.of(game.getColor2()) + "BINGO " + e.getBingoType() + " found " + ChatColor.RESET + e.getFoundItemEvent().getSlot().getDisplay());
            instance.broadcastMessage("");

        });

    }

    @EventHandler
    public void cancelSpawners(BlockBreakEvent e){
        var block = e.getBlock();
        if(block.getType() == Material.SPAWNER){
            e.setCancelled(true);
        }
    }
    

}
