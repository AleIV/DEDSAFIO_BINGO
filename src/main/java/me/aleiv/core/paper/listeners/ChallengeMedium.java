package me.aleiv.core.paper.listeners;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import io.papermc.paper.event.player.PlayerTradeEvent;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game;
import me.aleiv.core.paper.Game.Challenge;
import org.spigotmc.event.entity.EntityMountEvent;

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
            piglin.getWorld().getNearbyLivingEntities(piglin.getLocation(), 10).stream()
                    .filter(livingEntity -> livingEntity instanceof Player)
                    .forEach(livingEntity -> {
                        var player = (Player) livingEntity;
                        var table = manager.findTable(player.getUniqueId());
                        if (table != null) {
                            manager.attempToFind(player, Game.Challenge.PIGLIN_BARTER, "");
                        }
                    });
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
        if (!game.isChallengeEnabledFor(Challenge.CAMPFIRE_HAY_BALE) || !game.isChallengeEnabledFor(Challenge.HOGLIN_SCARE)
                || !game.isChallengeEnabledFor(Challenge.PIGLIN_SCARE)) return;


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
        } else if (block.getType() == Material.WARPED_FUNGUS) {
            if (block.getWorld().getNearbyLivingEntities(block.getLocation(), 10).stream()
                    .anyMatch(livingEntity -> livingEntity instanceof Hoglin)) {
                var manager = instance.getBingoManager();
                var table = manager.findTable(player.getUniqueId());
                if (table != null) {
                    manager.attempToFind(player, Game.Challenge.HOGLIN_SCARE, "");
                }
            }
        } else if (block.getType() == Material.SOUL_TORCH) {
            if (block.getWorld().getNearbyLivingEntities(block.getLocation(), 10).stream()
                    .anyMatch(livingEntity -> livingEntity instanceof Piglin)) {
                var manager = instance.getBingoManager();
                var table = manager.findTable(player.getUniqueId());
                if (table != null) {
                    manager.attempToFind(player, Challenge.PIGLIN_SCARE, "");
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
        if (!game.isChallengeEnabledFor(Challenge.GIVE_PLAYER_FLOWER) || !game.isChallengeEnabledFor(Challenge.EQUIP_DONKEY_CHEST)) return;

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
        } else if (event.getRightClicked() instanceof Donkey donkey && player.getEquipment() != null) {
            ItemStack itemHand = player.getEquipment().getItemInMainHand();
            if (itemHand.getType() == Material.CHEST && donkey.isTamed() && !donkey.isCarryingChest()) {
                var manager = instance.getBingoManager();
                var table = manager.findTable(player.getUniqueId());
                if (table != null) {
                    manager.attempToFind(player, Game.Challenge.EQUIP_DONKEY_CHEST, "");
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

    @EventHandler
    public void onMobLove(EntityBreedEvent event) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.BREED_ANIMALS)) return;

        if (event.getBreeder() instanceof Player player) {
            var manager = instance.getBingoManager();
            var table = manager.findTable(player.getUniqueId());
            if (table != null) {
                manager.attempToFind(player, Game.Challenge.BREED_ANIMALS, event.getEntity().getType().toString());
            }
        }
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.POTION_EFFECTS)) return;

        if (event.getEntity() instanceof Player player) {
            if (player.getActivePotionEffects().size() >= 4) {
                var manager = instance.getBingoManager();
                var table = manager.findTable(player.getUniqueId());
                if (table != null) {
                    manager.attempToFind(player, Game.Challenge.POTION_EFFECTS, "");
                }
            }
        }
    }

    @EventHandler
    public void onEntityPickUp(EntityPickupItemEvent event) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.MILK_ZOMBIE)) return;

        if (event.getEntity() instanceof Zombie zombie && event.getItem().getItemStack().getType() == Material.MILK_BUCKET) {
            var manager = instance.getBingoManager();
            zombie.getWorld().getNearbyLivingEntities(zombie.getLocation(), 10).stream()
                    .filter(livingEntity -> livingEntity instanceof Player)
                    .forEach(livingEntity -> {
                        var player = (Player) livingEntity;
                        var table = manager.findTable(player.getUniqueId());
                        if (table != null) {
                            manager.attempToFind(player, Game.Challenge.MILK_ZOMBIE, "");
                        }
                    });
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.HUNGER_DAMAGE)) return;

        if (event.getEntity() instanceof Player player && event.getCause() == EntityDamageEvent.DamageCause.STARVATION) {
            var manager = instance.getBingoManager();
            var table = manager.findTable(player.getUniqueId());
            if (table != null) {
                manager.attempToFind(player, Game.Challenge.HUNGER_DAMAGE, "");
            }
        }
    }

    @EventHandler
    public void onPortal(EntityPortalEnterEvent event) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.SHOOT_PORTAL)) return;

        if (event.getEntity() instanceof Projectile projectile && projectile.getShooter() instanceof Player player) {
            var manager = instance.getBingoManager();
            var table = manager.findTable(player.getUniqueId());
            if (table != null) {
                manager.attempToFind(player, Game.Challenge.SHOOT_PORTAL, "");
            }
        }
    }

    @EventHandler
    public void onMount(EntityMountEvent event) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.RIDE_HORSE_MINECART)) return;

        if (event.getEntity() instanceof Player player && event.getMount() instanceof Horse horse) {
            if (horse.getVehicle() != null && horse.getVehicle() instanceof Minecart) {
                var manager = instance.getBingoManager();
                var table = manager.findTable(player.getUniqueId());
                if (table != null) {
                    manager.attempToFind(player, Game.Challenge.RIDE_HORSE_MINECART, "");
                }
            }
        }
    }

    @EventHandler
    public void onWater(PlayerBucketEmptyEvent event) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.WATER_DROP)) return;

        Player player = event.getPlayer();
        if (player.getFallDistance() >= 72 && player.getLocation().distance(event.getBlock().getLocation()) < 3) {
            final double preHealth = player.getHealth();
            Bukkit.getScheduler().runTaskLater(instance, task -> {
                if (player.getGameMode() != GameMode.SPECTATOR && player.getHealth() >= preHealth) {
                    var manager = instance.getBingoManager();
                    var table = manager.findTable(player.getUniqueId());
                    if (table != null) {
                        manager.attempToFind(player, Game.Challenge.WATER_DROP, "");
                    }
                }
            }, 10);
        }
    }

}
