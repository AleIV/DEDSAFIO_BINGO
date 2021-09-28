package me.aleiv.core.paper.listeners;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Strider;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import io.papermc.paper.event.player.PlayerTradeEvent;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game;
import me.aleiv.core.paper.Game.Challenge;

public class ChallengeMedium implements Listener{
    
    Core instance;

    public ChallengeMedium(Core instance){
        this.instance = instance;
    }

    private final List<Material> beds = List.of(Material.BLACK_BED, Material.BLUE_BED, Material.BROWN_BED,
            Material.CYAN_BED, Material.GRAY_BED, Material.GREEN_BED, Material.LIGHT_BLUE_BED, Material.LIGHT_GRAY_BED,
            Material.LIME_BED, Material.MAGENTA_BED, Material.ORANGE_BED, Material.PINK_BED, Material.PURPLE_BED,
            Material.RED_BED, Material.WHITE_BED, Material.YELLOW_BED);
    private final List<Material> flowers = List.of(Material.DANDELION, Material.POPPY,
            Material.BLUE_ORCHID, Material.ALLIUM, Material.AZURE_BLUET, Material.OXEYE_DAISY,
            Material.WITHER_ROSE, Material.SUNFLOWER, Material.PEONY, Material.CORNFLOWER);
    private final List<Material> lightBlocks = List.of(Material.SEA_LANTERN, Material.GLOWSTONE, Material.TORCH,
            Material.SOUL_TORCH, Material.REDSTONE_TORCH, Material.SEA_PICKLE, Material.END_ROD,
            Material.ENCHANTING_TABLE, Material.ENDER_CHEST, Material.LANTERN, Material.SOUL_LANTERN,
            Material.CAMPFIRE, Material.SOUL_CAMPFIRE, Material.SHROOMLIGHT, Material.BEACON, Material.JACK_O_LANTERN);

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.INVENTORY_STACKS)) return;

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
        if (!game.isChallengeEnabledFor(Challenge.STRIDER_GAPPLE)) return;

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
        if (!game.isChallengeEnabledFor(Challenge.PIGLIN_BARTER)) return;

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
        if (!game.isChallengeEnabledFor(Challenge.VILLAGER_MAX_TRADE)) return;

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
        if (!game.isChallengeEnabledFor(Challenge.CROSSBOW_SHOT)) return;

        if (event.getEntity() instanceof Player player && event.getDamager() instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player shooter) {
                if (shooter.equals(player)) return;
                ItemStack crossbow = shooter.getInventory().getItemInMainHand();
                if (!crossbow.containsEnchantment(Enchantment.PIERCING)) return;
                var manager = instance.getBingoManager();
                var table = manager.findTable(player.getUniqueId());
                if (table != null) {
                    Bukkit.getScheduler().runTaskLater(instance, task -> {
                        if (table.getPlayerStream().collect(Collectors.toList()).contains(shooter)) {
                            manager.attempToFind(player, Game.Challenge.CROSSBOW_SHOT, "");
                        }
                    }, 1);
                }
            }
        }
    }

    @EventHandler
    public void onItemEnchant(EnchantItemEvent event) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.ENCHANT_LVL6)) return;

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
        if (!game.isChallengeEnabledFor(Challenge.CAMPFIRE_HAY_BALE)) return;


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
        if (!game.isChallengeEnabledFor(Challenge.BED_EXPLODE)) return;

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
        if (!game.isChallengeEnabledFor(Challenge.FISH_ITEMS)) return;

        Player player = event.getPlayer();
        if (event.getCaught() instanceof Item item) {
            var manager = instance.getBingoManager();
            var table = manager.findTable(player.getUniqueId());
            if (table != null && event.getCaught() != null) {
                manager.attempToFind(player, Game.Challenge.FISH_ITEMS, item.getItemStack().getType().toString());
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.GIVE_PLAYER_FLOWER)) return;

        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Player clickedPlayer && player.getEquipment() != null) {
            ItemStack itemHand = player.getEquipment().getItemInMainHand();
            if (itemHand.getType().toString().contains("TULIP") || flowers.contains(itemHand.getType())) {
                var manager = instance.getBingoManager();
                var table = manager.findTable(player.getUniqueId());
                if (table != null ) {
                    manager.attempToFind(player, Game.Challenge.GIVE_PLAYER_FLOWER, "");
                    player.sendMessage(ChatColor.GOLD + "Le diste una flor a " + clickedPlayer.getName());
                    clickedPlayer.sendMessage(ChatColor.GOLD + player.getName() + " te dió una flor");
                    clickedPlayer.getInventory().addItem(itemHand.clone());
                    player.getEquipment().setItemInMainHand(null);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.MINE_LIGHT_SOURCE)) return;

        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (lightBlocks.contains(block.getType())) {
            var manager = instance.getBingoManager();
            var table = manager.findTable(player.getUniqueId());
            if (table != null) {
                manager.attempToFind(player, Game.Challenge.MINE_LIGHT_SOURCE, block.getType().toString());
            }
        }
    }

}
