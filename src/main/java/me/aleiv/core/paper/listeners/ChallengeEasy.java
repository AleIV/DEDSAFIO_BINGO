package me.aleiv.core.paper.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Banner;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Turtle;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityMountEvent;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.Challenge;

public class ChallengeEasy implements Listener {

    Core instance;

    public ChallengeEasy(Core instance) {
        this.instance = instance;
    }

    private final List<Material> listMaterials = List.of(Material.GOLD_ORE, Material.IRON_ORE, Material.COAL_ORE,
            Material.NETHER_GOLD_ORE, Material.DIAMOND_ORE, Material.LAPIS_ORE, Material.EMERALD_ORE,
            Material.NETHER_QUARTZ_ORE, Material.ANCIENT_DEBRIS, Material.REDSTONE_ORE);
    private final List<Material> redstoneBlocks = List.of(Material.REDSTONE_BLOCK, Material.REDSTONE_TORCH);
    private final List<String> interactableBlock = List.of("BUTTON", "LEVER", "PRESSURE");

    private final List<Material> flowers = List.of(Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID,
            Material.ALLIUM, Material.AZURE_BLUET, Material.OXEYE_DAISY, Material.WITHER_ROSE, Material.SUNFLOWER,
            Material.PEONY, Material.CORNFLOWER, Material.LILY_OF_THE_VALLEY);// CONSIDER TULIP

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.JARDINERO) || !game.isChallengeEnabledFor(Challenge.SHIELD_BANNER))
            return;
        var block = e.getClickedBlock();
        var hand = e.getItem();
        var player = e.getPlayer();
        var manager = instance.getBingoManager();
        var table = manager.findTable(player.getUniqueId());

        if (table == null)
            return;

        if (block != null && hand != null && block.getType() == Material.FLOWER_POT
                && (flowers.contains(hand.getType()) || hand.getType().toString().contains("TULIP"))) {

            manager.attempToFind(player, Challenge.JARDINERO, hand.getType().toString());

        } else if (hand != null && hand.getType() == Material.SHIELD) {

            var meta = (BlockStateMeta) hand.getItemMeta();
            var state = meta.getBlockState();
            var banner = (Banner) state;
            if (banner.getPatterns().size() >= 3) {
                manager.attempToFind(player, Challenge.SHIELD_BANNER, "");
            }
        }
    }

    public boolean isTeamBlocking(List<Player> players, Player p) {

        for (var player : players) {
            if (player.getUniqueId().getMostSignificantBits() == p.getUniqueId().getMostSignificantBits())
                continue;

            var item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType() != Material.SHIELD || !player.isBlocking()) {
                return false;
            }
            var meta = (BlockStateMeta) item.getItemMeta();
            var state = meta.getBlockState();
            var banner = (Banner) state;

            if (banner.getBaseColor().toString().contains("WHITE")) {
                return false;
            }
        }

        if (players.isEmpty())
            return false;

        return true;
    }

    @EventHandler
    public void onCampfire(BlockCookEvent e) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.CAMPFIRE_CAMPING))
            return;

        var block = e.getBlock();
        if (block.getType() == Material.CAMPFIRE || block.getType() == Material.SOUL_CAMPFIRE) {
            var manager = instance.getBingoManager();

            var camping = block.getLocation().getNearbyPlayers(10).stream().map(p -> p.getUniqueId()).toList();
            for (var uuid : camping) {
                var table = manager.findTable(uuid);
                var players = table.getMembers();
                var player = Bukkit.getPlayer(uuid);

                if (player != null && table != null && isTeamNearby(players, camping)) {

                    manager.attempToFind(player, Challenge.CAMPFIRE_CAMPING, "");
                    return;
                }
            }

        }
    }

    public boolean isTeamNearby(List<UUID> players, List<UUID> uuidsTeam) {
        for (UUID uuid : players) {
            if (!uuidsTeam.contains(uuid)) {
                return false;
            }
        }
        return true;
    }

    @EventHandler
    public void onJump(PlayerJumpEvent e) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.JUMP_BED))
            return;

        var block = e.getPlayer().getLocation().getBlock().getType().toString();

        if (!block.contains("BED"))
            return;

        var manager = instance.getBingoManager();

        var player = e.getPlayer();
        var table = manager.findTable(player.getUniqueId());

        if (table != null) {
            manager.attempToFind(player, Challenge.JUMP_BED, "");
        }

    }

    @EventHandler
    public void onLVL(PlayerLevelChangeEvent e) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.LVL_40))
            return;

        var manager = instance.getBingoManager();

        var player = e.getPlayer();
        var table = manager.findTable(player.getUniqueId());

        if (table != null && isTeam40LVL(table.getPlayerStream().collect(Collectors.toList()))) {
            manager.attempToFind(player, Challenge.LVL_40, "");
        }

    }

    public boolean isTeam40LVL(List<Player> players) {
        var count = 0;
        for (Player player : players) {
            count += player.getLevel();
        }
        return count >= 40;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.HALF_HEART))
            return;

        var entity = e.getEntity();

        if (entity instanceof Player player) {
            var manager = instance.getBingoManager();
            var table = manager.findTable(player.getUniqueId());
            if (table != null) {
                Bukkit.getScheduler().runTaskLater(instance, task -> {
                    if (isTeamAtHalf(table.getPlayerStream().collect(Collectors.toList()))) {
                        manager.attempToFind(player, Challenge.HALF_HEART, "");
                    }
                }, 1);
            }
        }
    }

    public boolean isTeamAtHalf(List<Player> players) {
        var count = 0;
        for (Player player : players) {
            if (player.getHealth() < 1.5)
                count++;
        }
        return players.size() == count;
    }

    @EventHandler
    public void onShield(PlayerItemBreakEvent e) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.SHIELD_BREAK))
            return;

        var item = e.getBrokenItem();

        if (item.getType() == Material.SHIELD) {
            var manager = instance.getBingoManager();

            var player = e.getPlayer();
            var table = manager.findTable(player.getUniqueId());
            if (table != null) {

                manager.attempToFind(player, Challenge.SHIELD_BREAK, "");
            }

        }
    }

    @EventHandler
    public void onEntityPotionEffect(EntityPotionEffectEvent e) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.DOLPHIN_SWIM) || !game.isChallengeEnabledFor(Challenge.GET_POISON))
            return;

        if (e.getEntity() instanceof Player) {

            var manager = instance.getBingoManager();

            var player = (Player) e.getEntity();
            var table = manager.findTable(player.getUniqueId());

            var newEffect = e.getNewEffect();

            if (table == null || newEffect == null)
                return;

            var type = newEffect.getType();
            if (type.equals(PotionEffectType.DOLPHINS_GRACE)
                    && e.getCause().equals(EntityPotionEffectEvent.Cause.DOLPHIN)) {

                manager.attempToFind(player, Challenge.DOLPHIN_SWIM, "");

            } else if (type.equals(PotionEffectType.POISON)) {
                manager.attempToFind(player, Challenge.GET_POISON, "");

            }
        }
    }

    @EventHandler
    public void onSkyHigh(PlayerJumpEvent e) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.MAXIMUM_HEIGHT))
            return;

        var manager = instance.getBingoManager();
        var player = e.getPlayer();
        var loc = player.getLocation();
        var table = manager.findTable(player.getUniqueId());

        if (loc.getY() >= 255 && table != null) {
            manager.attempToFind(player, Challenge.MAXIMUM_HEIGHT, "");
        }
    }

    @EventHandler
    public void growTree(StructureGrowEvent e) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.NETHER_TREE))
            return;

        var player = e.getPlayer();
        if (player == null)
            return;

        var manager = instance.getBingoManager();
        var table = manager.findTable(player.getUniqueId());

        if (table != null && player.getWorld().getEnvironment() == Environment.NETHER) {

            manager.attempToFind(player, Challenge.NETHER_TREE, "");

        }

    }

    @EventHandler
    public void onShootTarget(ProjectileHitEvent e) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.SHOOT_TARGET))
            return;

        var block = e.getHitBlock();
        if (block != null && block.getType() == Material.TARGET) {
            var proj = e.getEntity();

            if (proj.getShooter() != null && proj.getShooter()instanceof Player player) {
                var manager = instance.getBingoManager();
                var table = manager.findTable(player.getUniqueId());

                if (table != null) {

                    manager.attempToFind(player, Challenge.SHOOT_TARGET, "");

                }
            }
        }
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent e) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.EAT_FOOD))
            return;

        var item = e.getItem();
        if (item != null && (item.getType() != Material.POTION || item.getType() != Material.MILK_BUCKET
                || item.getType() != Material.HONEY_BOTTLE)) {

            var player = e.getPlayer();
            var manager = instance.getBingoManager();
            var table = manager.findTable(player.getUniqueId());

            if (table != null) {

                manager.attempToFind(player, Challenge.EAT_FOOD, item.getType().toString());
            }
        }

    }

    @EventHandler
    public void mineMinerals(BlockBreakEvent e) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.BREAK_RULE_1) || !game.isChallengeEnabledFor(Challenge.MINE_MINERALS))
            return;

        var block = e.getBlock();
        var manager = instance.getBingoManager();
        var player = e.getPlayer();

        if (player == null)
            return;

        if (listMaterials.contains(block.getType())) {
            manager.attempToFind(player, Challenge.MINE_MINERALS, block.getType().toString());
        }

        if(block.getLocation().getY() > 70) return;

        var l = block.getLocation();
        String formattedLoc = l.getBlockX() + ";" + l.getBlockY() + ";" + l.getBlockZ();
        manager.attempToFind(player, Challenge.BREAK_RULE_1, formattedLoc);
    }

    @EventHandler
    public void entityDeath(EntityDeathEvent e) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.HOSTILE_KILL) || !game.isChallengeEnabledFor(Challenge.ANIMAL_KILL))
            return;

        var entity = e.getEntity();
        var player = entity.getKiller();

        if(player == null) return;
        var manager = instance.getBingoManager();
        var table = manager.findTable(player.getUniqueId());

        if (table == null)
            return;

        if (entity instanceof Monster monster) {

            manager.attempToFind(player, Challenge.HOSTILE_KILL, monster.getType().toString());

        } else if (entity instanceof Animals animal) {

            manager.attempToFind(player, Challenge.ANIMAL_KILL, animal.getType().toString());

        }

    }

    @EventHandler
    public void onEntDeath(EntityDeathEvent e) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.DROWN_VILLAGER))
            return;

        var entity = e.getEntity();
        var player = entity.getKiller();
        var manager = instance.getBingoManager();
        if (player == null) {

            if (entity instanceof Villager villager) {

                var cause = villager.getLastDamageCause().getCause();
                if (cause == DamageCause.DROWNING || cause == DamageCause.SUFFOCATION) {
                    var nearby = villager.getLocation().getNearbyPlayers(10).stream().toList();
                    nearby.forEach(p -> {
                        manager.attempToFind(p, Challenge.DROWN_VILLAGER, "");
                    });
                }
            }
            return;
        }

    }

    @EventHandler
    public void onEntDeath3(EntityDeathEvent e){
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.ACUATIC_KILL))
            return;

            var entity = e.getEntity();
        var player = entity.getKiller();
        var manager = instance.getBingoManager();
        
        if(player == null) return;

        if (entity instanceof Fish || entity instanceof Squid || entity instanceof Turtle || entity instanceof Drowned
                || entity instanceof Guardian || entity instanceof Dolphin) {
            manager.attempToFind(player, Challenge.ACUATIC_KILL, entity.getType().toString());

        }
    }

    @EventHandler
    public void onEntDeath2(EntityDeathEvent e) {
        var game = instance.getGame();

        if (!game.isChallengeEnabledFor(Challenge.PINK_SHEEP_BIOME))
            return;

        var killer = e.getEntity().getKiller();

        var entity = e.getEntity();
        if (killer != null && entity instanceof Sheep sheep && sheep.getColor() == DyeColor.PINK) {
            var manager = instance.getBingoManager();
            var biome = sheep.getLocation().getBlock().getBiome().toString();
            manager.attempToFind(killer, Challenge.PINK_SHEEP_BIOME, biome);

        }
    }

    @EventHandler
    public void entityInteractEvent(PlayerInteractAtEntityEvent e) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.CREEPER_IGNITE) || !game.isChallengeEnabledFor(Challenge.COLOR_SHEEP))
            return;

        var player = e.getPlayer();
        var entity = e.getRightClicked();
        var hand = player.getEquipment().getItemInMainHand();
        if (entity instanceof Creeper && hand != null && hand.getType() == Material.FLINT_AND_STEEL) {
            var manager = instance.getBingoManager();
            var table = manager.findTable(player.getUniqueId());
            if (table != null) {
                manager.attempToFind(player, Challenge.CREEPER_IGNITE, "");
            }
        }

        if (entity instanceof Sheep && hand != null && hand.getType().toString().contains("DYE")) {
            var manager = instance.getBingoManager();
            var table = manager.findTable(player.getUniqueId());
            if (table != null) {
                manager.attempToFind(player, Challenge.COLOR_SHEEP, hand.getType().toString());
            }
        }

    }

    @EventHandler
    public void armorEquipped(PlayerArmorChangeEvent event) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.ARMOR_MATERIALS))
            return;

        Player player = event.getPlayer();
        List<String> equippedItems = new ArrayList<>();
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item == null)
                break;
            Material material = item.getType();
            String baseMaterial = material.name().split("_")[0];
            if (equippedItems.contains(baseMaterial))
                break;
            equippedItems.add(baseMaterial);
        }

        if (equippedItems.size() == 4) {
            var manager = instance.getBingoManager();
            var table = manager.findTable(player.getUniqueId());
            if (table != null) {
                manager.attempToFind(player, Challenge.ARMOR_MATERIALS, "");
            }
        }
    }

    @EventHandler
    public void playerInteractBlock(PlayerInteractEvent event) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.REDSTONE_SIGNAL)
                || !game.isChallengeEnabledFor(Challenge.BONE_MEAL_COMPOSTER))
            return;

        var player = event.getPlayer();
        var block = event.getClickedBlock();

        if (block != null && block.getType() == Material.COMPOSTER) {
            var blockData = block.getBlockData();
            if (blockData.getAsString().equals("minecraft:composter[level=8]")) {
                var manager = instance.getBingoManager();
                var table = manager.findTable(player.getUniqueId());
                if (table != null) {
                    manager.attempToFind(player, Challenge.BONE_MEAL_COMPOSTER, "");
                }
            }
        } else if (block != null && interactableBlock.stream().anyMatch(b -> block.getType().toString().contains(b))) {
            var manager = instance.getBingoManager();
            var table = manager.findTable(player.getUniqueId());
            if (table != null) {
                manager.attempToFind(player, Challenge.REDSTONE_SIGNAL, block.getType().toString());
            }
        }
    }

    @EventHandler
    public void onMount(EntityMountEvent event) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.PURPLE_LLAMA))
            return;

        if (event.getEntity()instanceof Player player && event.getMount()instanceof Llama llama) {
            if (llama.getInventory().contains(new ItemStack(Material.PURPLE_CARPET))) {
                var manager = instance.getBingoManager();
                var table = manager.findTable(player.getUniqueId());
                if (table != null) {
                    manager.attempToFind(player, Challenge.PURPLE_LLAMA, "");
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.ANVIL_DAMAGE))
            return;

        if (event.getEntity()instanceof Player player) {
            if (event.getCause() == DamageCause.FALLING_BLOCK
                    && event.getDamager()instanceof FallingBlock fallingBlock) {
                if (fallingBlock.getBlockData().getMaterial() == Material.ANVIL) {
                    var manager = instance.getBingoManager();
                    var table = manager.findTable(player.getUniqueId());
                    if (table != null) {
                        manager.attempToFind(player, Challenge.ANVIL_DAMAGE, "");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        var game = instance.getGame();
        if (!game.isChallengeEnabledFor(Challenge.REDSTONE_SIGNAL))
            return;

        Player player = event.getPlayer();
        if (redstoneBlocks.contains(event.getBlockPlaced().getType())) {
            var manager = instance.getBingoManager();
            var table = manager.findTable(player.getUniqueId());
            if (table != null) {
                manager.attempToFind(player, Challenge.REDSTONE_SIGNAL, event.getBlockPlaced().getType().toString());
            }
        }
    }

}
