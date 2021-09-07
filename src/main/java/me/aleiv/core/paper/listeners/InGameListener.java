package me.aleiv.core.paper.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.GameStage;
import me.aleiv.core.paper.events.BingoEvent;
import me.aleiv.core.paper.events.FoundItemEvent;
import net.md_5.bungee.api.ChatColor;

public class InGameListener implements Listener {

    Core instance;

    public InGameListener(Core instance) {
        this.instance = instance;
    }

    public void attempToFind(Player player, ItemStack item) {
        var uuid = player.getUniqueId().toString();
        var game = instance.getGame();

        var manager = instance.getBingoManager();

        var table = manager.findTable(player.getUniqueId());

        if (table != null) {
            var board = table.getBoard();
            var selectedItems = table.getSelectedItems();

            if (game.getGameStage() == GameStage.INGAME && selectedItems.contains(item.getType())) {

                var boards = game.getBoards();

                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        var slot = board[i][j];
                        if (slot.getMaterial() == item.getType() && !slot.isFound()) {
                            slot.setFound(true);

                            var score = boards.get(uuid);
                            instance.getBingoManager().updateBoard(score, table);

                            instance.getBingoManager().checkBingo(table, slot, player);
                            return;
                        }
                    }
                }
            }
        }

    }

    @EventHandler
    public void onItem(FoundItemEvent e){
        var game = instance.getGame();
        var slot = e.getSlot();
        var player = e.getPlayer();
        instance.broadcastMessage(ChatColor.of(game.getColor2()) + player.getName() + " found " + ChatColor.RESET + slot.getDisplay());
        instance.broadcastMessage("");
    }

    @EventHandler
    public void onPick(EntityPickupItemEvent e) {
        var entity = e.getEntity();
        if (entity instanceof Player) {
            var player = (Player) entity;
            var item = e.getItem().getItemStack();
            attempToFind(player, item);
        }
    }

    @EventHandler
    public void clickInv(InventoryClickEvent e) {
        var player = (Player) e.getWhoClicked();
        var item = e.getCurrentItem();
        attempToFind(player, item);

    }

    @EventHandler
    public void onBingo(BingoEvent e) {
        Bukkit.getScheduler().runTask(instance, () -> {
            Bukkit.getOnlinePlayers().forEach(p ->{
                var player = (Player) p;
                e.getFoundItemEvent().getTable().sendTableDisplay(player);
            });

        });

    }

}
