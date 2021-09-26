package me.aleiv.core.paper.listeners;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import io.papermc.paper.event.player.PlayerTradeEvent;

import me.aleiv.core.paper.Game;
import me.aleiv.core.paper.Core;

public class ChallengeMedium implements Listener{
    
    Core instance;

    public ChallengeMedium(Core instance){
        this.instance = instance;
    }

    private final List<Material> beds = List.of(Material.BLACK_BED, Material.BLUE_BED, Material.BROWN_BED,
            Material.CYAN_BED, Material.GRAY_BED, Material.GREEN_BED, Material.LIGHT_BLUE_BED, Material.LIGHT_GRAY_BED,
            Material.LIME_BED, Material.MAGENTA_BED, Material.ORANGE_BED, Material.PINK_BED, Material.PURPLE_BED,
            Material.RED_BED, Material.WHITE_BED, Material.YELLOW_BED);

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
                            String data = player.getUniqueId() + ";" + projectile.getUniqueId();
                            manager.attempToFind(player, Game.Challenge.CROSSBOW_SHOT, data);
                        }
                    }, 1);
                }
            }
        }
    }

    @EventHandler
    public void onItemEnchant(EnchantItemEvent event) {
        var game = instance.getGame();
        if (game.getBingoFase() != Game.BingoFase.CHALLENGE && game.getBingoRound() != Game.BingoRound.TWO)
            return;

        Player player = event.getEnchanter();
        if (event.getExpLevelCost() == 6) {
            var manager = instance.getBingoManager();
            var table = manager.findTable(player.getUniqueId());
            if (table != null) {
                manager.attempToFind(player, Game.Challenge.ENCHANT_LVL6, "");
            }
        }
    }

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event) {
        var game = instance.getGame();
        if (game.getBingoFase() != Game.BingoFase.CHALLENGE && game.getBingoRound() != Game.BingoRound.TWO)
            return;

        Block block = event.getBlockPlaced();
        Player player = event.getPlayer();
        if (block.getType() == Material.HAY_BLOCK) {
            Block campfire = block.getWorld().getBlockAt(block.getLocation().add(0, 1, 0));
            if (campfire.getType() == Material.CAMPFIRE) {
                var manager = instance.getBingoManager();
                var table = manager.findTable(player.getUniqueId());
                if (table != null) {
                    manager.attempToFind(player, Game.Challenge.CAMPFIRE_HAY_BALE, "");
                }
            }
        } else if (block.getType() == Material.CAMPFIRE) {
            Block hayBlock = block.getWorld().getBlockAt(block.getLocation().add(0, -1, 0));
            if (hayBlock.getType() == Material.HAY_BLOCK) {
                var manager = instance.getBingoManager();
                var table = manager.findTable(player.getUniqueId());
                if (table != null) {
                    manager.attempToFind(player, Game.Challenge.CAMPFIRE_HAY_BALE, "");
                }
            }
        }
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        var game = instance.getGame();
        if (game.getBingoFase() != Game.BingoFase.CHALLENGE && game.getBingoRound() != Game.BingoRound.TWO)
            return;

        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block != null && beds.contains(block.getType()) && (player.getWorld().getEnvironment() == World.Environment.NETHER ||
                    player.getWorld().getEnvironment() == World.Environment.THE_END)) {
                var manager = instance.getBingoManager();
                var table = manager.findTable(player.getUniqueId());
                if (table != null) {
                    manager.attempToFind(player, Game.Challenge.BED_EXPLODE, "");
                }
            }
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        var game = instance.getGame();
        if (game.getBingoFase() != Game.BingoFase.CHALLENGE && game.getBingoRound() != Game.BingoRound.TWO)
            return;

        Player player = event.getPlayer();
        if (event.getCaught() instanceof Item item) {
            var manager = instance.getBingoManager();
            var table = manager.findTable(player.getUniqueId());
            if (table != null && event.getCaught() != null) {
                manager.attempToFind(player, Game.Challenge.FISH_ITEMS, item.getItemStack().getType().toString());
            }
        }
    }

}
