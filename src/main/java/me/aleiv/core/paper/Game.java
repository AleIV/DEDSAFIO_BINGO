package me.aleiv.core.paper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aleiv.core.paper.events.GameTickEvent;
import me.aleiv.core.paper.game.BingoPlayer;
import me.aleiv.core.paper.game.ItemCode;
import me.aleiv.core.paper.game.Table;
import me.aleiv.core.paper.utilities.FastBoard;

@Data
@EqualsAndHashCode(callSuper = false)
public class Game extends BukkitRunnable {
        Core instance;

        long gameTime = 0;
        long startTime = 0;

        long gameStartTime = 0;
        GameStage gameStage;
        BingoMode bingoMode;
        BingoDifficulty bingoDifficulty;
        BingoType bingoType;

        String color1 = "#808dac"; // INFO COLOR
        String color2 = "#a6e316"; // AWARD COLOR
        String color3 = "#ac1133"; // ERROR COLOR
        String color4 = "#16e384"; // CONFIG COLOR

        HashMap<String, BingoPlayer> players = new HashMap<>();
        HashMap<String, Table> tables = new HashMap<>();
        HashMap<String, FastBoard> boards = new HashMap<>();
        HashMap<Material, ItemCode> materials = new HashMap<>();

        List<List<Material>> classEasy = new ArrayList<>();
        List<List<Material>> classMedium = new ArrayList<>();
        List<List<Material>> classHard = new ArrayList<>();
        List<List<Material>> classExpert = new ArrayList<>();

        public Game(Core instance) {
                this.instance = instance;
                this.startTime = System.currentTimeMillis();
                this.gameStage = GameStage.LOBBY;
                this.bingoMode = BingoMode.NORMAL;
                this.bingoDifficulty = BingoDifficulty.EASY;
                this.bingoType = BingoType.LINE;

                registerMaterials();

                registerClassEasy();
                registerClassMedium();
                registerClassHard();
                registerClassExpert();

        }

        @Override
        public void run() {

                var new_time = (int) (Math.floor((System.currentTimeMillis() - startTime) / 1000.0));

                gameTime = new_time;

                Bukkit.getPluginManager().callEvent(new GameTickEvent(new_time, true));
        }

        public enum BingoMode {
                NORMAL, TWITCH;
        }

        public enum BingoGamemode {
                ITEM, CHALLENGE;
        }

        public enum BingoDifficulty {
                EASY, MEDIUM, HARD, EXPERT;
        }

        public enum BingoType {
                LINE, FULL;
        }

        public enum GameStage {
                LOBBY, STARTING, INGAME, POSTGAME;
        }

        public boolean isNormalMode() {
                return bingoMode == BingoMode.NORMAL;
        }

        public boolean isBingoTypeFull() {
                return bingoType == BingoType.FULL;
        }

        public void registerClassEasy() {
                classEasy.add(List.of(Material.COMPOSTER, Material.FURNACE, Material.BARREL, Material.LOOM,
                                Material.SMOKER, Material.BLAST_FURNACE, Material.CARTOGRAPHY_TABLE,
                                Material.FLETCHING_TABLE, Material.GRINDSTONE, Material.SMITHING_TABLE,
                                Material.STONECUTTER, Material.BELL, Material.LECTERN, Material.CRAFTING_TABLE));
                classEasy.add(List.of(Material.DRIED_KELP_BLOCK, Material.HAY_BLOCK, Material.DRIED_KELP,
                                Material.BREAD));
                classEasy.add(List.of(Material.FLOWER_POT));
                classEasy.add(List.of(Material.LADDER));
                classEasy.add(List.of(Material.PAINTING, Material.ITEM_FRAME));
                classEasy.add(List.of(Material.ANVIL));
                classEasy.add(List.of(Material.CHAIN, Material.IRON_BARS));
                classEasy.add(List.of(Material.SALMON_BUCKET, Material.COD_BUCKET));
                classEasy.add(List.of(Material.CAMPFIRE, Material.TORCH, Material.LANTERN, Material.SOUL_CAMPFIRE,
                                Material.SOUL_TORCH, Material.SOUL_LANTERN));
                classEasy.add(List.of(Material.FIREWORK_STAR, Material.FIREWORK_ROCKET, Material.FIRE_CHARGE));
                classEasy.add(List.of(Material.DISPENSER, Material.NOTE_BLOCK, Material.PISTON, Material.DROPPER,
                                Material.OBSERVER, Material.REDSTONE_BLOCK, Material.TNT, Material.TARGET));
                classEasy.add(List.of(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, Material.COMPARATOR, Material.REPEATER,
                                Material.DAYLIGHT_DETECTOR, Material.HOPPER, Material.TRIPWIRE_HOOK,
                                Material.REDSTONE_TORCH, Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
                                Material.IRON_TRAPDOOR));
                classEasy.add(List.of(Material.ACTIVATOR_RAIL, Material.RAIL, Material.DETECTOR_RAIL,
                                Material.POWERED_RAIL));
                classEasy.add(List.of(Material.CHEST_MINECART, Material.HOPPER_MINECART, Material.FURNACE_MINECART,
                                Material.TNT_MINECART, Material.MINECART));
                classEasy.add(List.of(Material.BIRCH_SAPLING, Material.ACACIA_SAPLING, Material.DARK_OAK_SAPLING,
                                Material.OAK_SAPLING, Material.SPRUCE_SAPLING));
                classEasy.add(List.of(Material.AZURE_BLUET, Material.ALLIUM, Material.RED_TULIP, Material.ORANGE_TULIP,
                                Material.WHITE_TULIP, Material.PINK_TULIP, Material.OXEYE_DAISY, Material.CORNFLOWER,
                                Material.LILY_OF_THE_VALLEY, Material.DANDELION, Material.POPPY, Material.SUGAR_CANE,
                                Material.LILAC));
                classEasy.add(List.of(Material.KELP, Material.TWISTING_VINES, Material.WEEPING_VINES,
                                Material.CRIMSON_ROOTS, Material.WARPED_ROOTS));
                classEasy.add(List.of(Material.GRASS, Material.FERN, Material.VINE, Material.SEAGRASS,
                                Material.NETHER_SPROUTS, Material.DEAD_BUSH));
                classEasy.add(List.of(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS,
                                Material.LEATHER_BOOTS, Material.DIAMOND_PICKAXE, Material.DIAMOND_SWORD,
                                Material.DIAMOND_AXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_HOE));
                classEasy.add(List.of(Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS,
                                Material.GOLDEN_BOOTS, Material.GOLDEN_SWORD, Material.GOLDEN_PICKAXE,
                                Material.GOLDEN_AXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_HOE));
                classEasy.add(List.of(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS,
                                Material.IRON_BOOTS, Material.IRON_SWORD, Material.IRON_PICKAXE, Material.IRON_AXE,
                                Material.IRON_SHOVEL, Material.IRON_HOE));
                classEasy.add(List.of(Material.SHEARS, Material.FISHING_ROD, Material.CARROT_ON_A_STICK,
                                Material.WARPED_FUNGUS_ON_A_STICK));
                classEasy.add(List.of(Material.BOW, Material.CROSSBOW, Material.SHIELD));
                classEasy.add(List.of(Material.DARK_OAK_BOAT, Material.SPRUCE_BOAT, Material.BIRCH_BOAT,
                                Material.OAK_BOAT, Material.ACACIA_BOAT));
                classEasy.add(List.of(Material.WHITE_WOOL, Material.ORANGE_WOOL, Material.PINK_WOOL,
                                Material.LIGHT_BLUE_WOOL, Material.YELLOW_WOOL, Material.LIME_WOOL,
                                Material.MAGENTA_WOOL, Material.GRAY_WOOL, Material.LIGHT_GRAY_WOOL, Material.CYAN_WOOL,
                                Material.PURPLE_WOOL, Material.BLUE_WOOL, Material.BROWN_WOOL, Material.GREEN_WOOL,
                                Material.RED_WOOL, Material.BLACK_WOOL));
                classEasy.add(List.of(Material.WHITE_CONCRETE, Material.ORANGE_CONCRETE, Material.PINK_CONCRETE,
                                Material.LIGHT_BLUE_CONCRETE, Material.YELLOW_CONCRETE, Material.LIME_CONCRETE,
                                Material.MAGENTA_CONCRETE, Material.GRAY_CONCRETE, Material.LIGHT_GRAY_CONCRETE,
                                Material.CYAN_CONCRETE, Material.PURPLE_CONCRETE, Material.BLUE_CONCRETE,
                                Material.BROWN_CONCRETE, Material.GREEN_CONCRETE, Material.RED_CONCRETE,
                                Material.BLACK_CONCRETE));
                classEasy.add(List.of(Material.DIRT, Material.GRAVEL, Material.CLAY, Material.SNOW_BLOCK,
                                Material.COARSE_DIRT));
                classEasy.add(List.of(Material.STONE, Material.GRANITE, Material.POLISHED_GRANITE, Material.DIORITE,
                                Material.POLISHED_DIORITE, Material.ANDESITE, Material.POLISHED_ANDESITE,
                                Material.COBBLESTONE, Material.MOSSY_COBBLESTONE, Material.STONE_BRICKS,
                                Material.CRACKED_STONE_BRICKS, Material.MOSSY_STONE_BRICKS,
                                Material.CHISELED_STONE_BRICKS, Material.SMOOTH_STONE, Material.BRICKS));
                classEasy.add(List.of(Material.RED_SAND, Material.RED_SANDSTONE, Material.CUT_RED_SANDSTONE,
                                Material.SMOOTH_RED_SANDSTONE, Material.CHISELED_RED_SANDSTONE, Material.SAND,
                                Material.SANDSTONE, Material.CUT_SANDSTONE, Material.SMOOTH_SANDSTONE,
                                Material.CHISELED_SANDSTONE));
                classEasy.add(List.of(Material.OBSIDIAN));
                classEasy.add(List.of(Material.OAK_LOG, Material.BIRCH_LOG, Material.SPRUCE_LOG, Material.DARK_OAK_LOG,
                                Material.ACACIA_LOG, Material.CRIMSON_STEM, Material.WARPED_STEM));
                classEasy.add(List.of(Material.OAK_LEAVES, Material.BIRCH_LEAVES, Material.SPRUCE_LEAVES,
                                Material.DARK_OAK_LEAVES, Material.ACACIA_LEAVES, Material.NETHER_WART_BLOCK,
                                Material.WARPED_WART_BLOCK));
                classEasy.add(List.of(Material.NETHERRACK, Material.NETHER_BRICKS, Material.CRACKED_NETHER_BRICKS,
                                Material.CHISELED_NETHER_BRICKS, Material.BLACKSTONE, Material.POLISHED_BLACKSTONE,
                                Material.POLISHED_BLACKSTONE_BRICKS, Material.CRACKED_POLISHED_BLACKSTONE_BRICKS,
                                Material.CHISELED_POLISHED_BLACKSTONE, Material.BASALT, Material.POLISHED_BASALT,
                                Material.SOUL_SAND, Material.SOUL_SOIL, Material.QUARTZ_BLOCK, Material.SMOOTH_QUARTZ,
                                Material.QUARTZ_BRICKS, Material.QUARTZ_PILLAR, Material.CHISELED_QUARTZ_BLOCK,
                                Material.MAGMA_BLOCK, Material.BONE_BLOCK, Material.GLOWSTONE, Material.SHROOMLIGHT));
                classEasy.add(List.of(Material.WHITE_BED, Material.ORANGE_BED, Material.MAGENTA_BED,
                                Material.YELLOW_BED, Material.LIGHT_BLUE_BED, Material.LIME_BED, Material.PINK_BED,
                                Material.GRAY_BED, Material.LIGHT_GRAY_BED, Material.CYAN_BED, Material.PURPLE_BED,
                                Material.BLUE_BED, Material.BROWN_BED, Material.GREEN_BED, Material.RED_BED,
                                Material.BLACK_BED));
                classEasy.add(List.of(Material.WHITE_BANNER, Material.ORANGE_BANNER, Material.MAGENTA_BANNER,
                                Material.YELLOW_BANNER, Material.LIGHT_BLUE_BANNER, Material.LIME_BANNER,
                                Material.PINK_BANNER, Material.GRAY_BANNER, Material.LIGHT_GRAY_BANNER,
                                Material.CYAN_BANNER, Material.PURPLE_BANNER, Material.BLUE_BANNER,
                                Material.BROWN_BANNER, Material.GREEN_BANNER, Material.RED_BANNER,
                                Material.BLACK_BANNER));
                classEasy.add(List.of(Material.WHITE_STAINED_GLASS, Material.ORANGE_STAINED_GLASS,
                                Material.MAGENTA_STAINED_GLASS, Material.YELLOW_STAINED_GLASS,
                                Material.LIGHT_BLUE_STAINED_GLASS, Material.LIME_STAINED_GLASS,
                                Material.PINK_STAINED_GLASS, Material.GRAY_STAINED_GLASS,
                                Material.LIGHT_GRAY_STAINED_GLASS, Material.CYAN_STAINED_GLASS,
                                Material.PURPLE_STAINED_GLASS, Material.BLUE_STAINED_GLASS,
                                Material.BROWN_STAINED_GLASS, Material.GREEN_STAINED_GLASS, Material.RED_STAINED_GLASS,
                                Material.BLACK_STAINED_GLASS, Material.GLASS));
                classEasy.add(List.of(Material.WHITE_TERRACOTTA, Material.ORANGE_TERRACOTTA,
                                Material.MAGENTA_TERRACOTTA, Material.YELLOW_TERRACOTTA, Material.LIGHT_BLUE_TERRACOTTA,
                                Material.LIME_TERRACOTTA, Material.PINK_TERRACOTTA, Material.GRAY_TERRACOTTA,
                                Material.LIGHT_GRAY_TERRACOTTA, Material.CYAN_TERRACOTTA, Material.PURPLE_TERRACOTTA,
                                Material.BLUE_TERRACOTTA, Material.BROWN_TERRACOTTA, Material.GREEN_TERRACOTTA,
                                Material.RED_TERRACOTTA, Material.BLACK_TERRACOTTA, Material.TERRACOTTA));
                classEasy.add(List.of(Material.BRICK, Material.INK_SAC, Material.LEATHER, Material.BONE_MEAL,
                                Material.SNOWBALL));
                classEasy.add(List.of(Material.CHARCOAL, Material.COAL, Material.IRON_INGOT, Material.GOLD_INGOT,
                                Material.EMERALD, Material.LAPIS_LAZULI, Material.DIAMOND));
                classEasy.add(List.of(Material.QUARTZ, Material.REDSTONE, Material.IRON_BLOCK, Material.GOLD_BLOCK,
                                Material.EMERALD_BLOCK, Material.LAPIS_BLOCK, Material.DIAMOND_BLOCK));
                classEasy.add(List.of(Material.APPLE, Material.CARROT, Material.COOKED_BEEF, Material.COOKED_MUTTON,
                                Material.COOKED_PORKCHOP, Material.COOKED_COD, Material.COOKED_SALMON,
                                Material.COOKED_CHICKEN, Material.SPIDER_EYE, Material.ROTTEN_FLESH,
                                Material.MUSHROOM_STEW, Material.BAKED_POTATO));

        }

        public void registerClassMedium() {
                classMedium.add(List.of(Material.FERMENTED_SPIDER_EYE, Material.RABBIT_STEW, Material.BEETROOT_SOUP,
                                Material.COOKIE, Material.SWEET_BERRIES, Material.COOKED_RABBIT,
                                Material.POISONOUS_POTATO, Material.SEA_PICKLE, Material.HONEY_BOTTLE, Material.EGG,
                                Material.PUMPKIN_PIE));
                classMedium.add(List.of(Material.TROPICAL_FISH, Material.TROPICAL_FISH_BUCKET, Material.MELON_SLICE,
                                Material.ENDER_PEARL));
                classMedium.add(List.of(Material.SADDLE, Material.MAGMA_CREAM, Material.GLISTERING_MELON_SLICE,
                                Material.CRYING_OBSIDIAN, Material.GILDED_BLACKSTONE, Material.ENCHANTING_TABLE,
                                Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.CAKE, Material.SUNFLOWER));
                classMedium.add(List.of(Material.COCOA_BEANS, Material.MELON_SEEDS, Material.PUMPKIN_SEEDS,
                                Material.NETHER_WART));
                classMedium.add(List.of(Material.BLAZE_ROD, Material.BREWING_STAND, Material.BLAZE_POWDER,
                                Material.PUFFERFISH_BUCKET, Material.NAME_TAG, Material.HEART_OF_THE_SEA));

        }

        public void registerClassHard() {
                classHard.add(List.of(Material.GRASS_BLOCK, Material.NETHER_GOLD_ORE, Material.COAL_ORE,
                                Material.REDSTONE_ORE, Material.DIAMOND_ORE, Material.LAPIS_ORE, Material.EMERALD_ORE,
                                Material.BEE_NEST, Material.MUSHROOM_STEM, Material.RED_MUSHROOM_BLOCK,
                                Material.BROWN_MUSHROOM_BLOCK, Material.PODZOL, Material.NETHER_QUARTZ_ORE,
                                Material.NETHERITE_SCRAP, Material.JUKEBOX));
                classHard.add(List.of(Material.DEAD_HORN_CORAL_BLOCK, Material.DEAD_FIRE_CORAL_BLOCK,
                                Material.DEAD_TUBE_CORAL_BLOCK, Material.DEAD_BRAIN_CORAL_BLOCK,
                                Material.DEAD_BUBBLE_CORAL_BLOCK));
                classHard.add(List.of(Material.HORN_CORAL_BLOCK, Material.FIRE_CORAL_BLOCK, Material.TUBE_CORAL_BLOCK,
                                Material.BRAIN_CORAL_BLOCK, Material.BUBBLE_CORAL_BLOCK));
                classHard.add(List.of(Material.SCUTE, Material.PHANTOM_MEMBRANE, Material.GHAST_TEAR,
                                Material.END_CRYSTAL, Material.MUSIC_DISC_PIGSTEP, Material.RESPAWN_ANCHOR,
                                Material.BAMBOO, Material.SCAFFOLDING, Material.TURTLE_EGG));
                classHard.add(List.of(Material.SLIME_BLOCK, Material.HONEY_BLOCK, Material.STICKY_PISTON));
                classHard.add(List.of(Material.NETHERITE_INGOT, Material.LODESTONE, Material.NETHERITE_HOE,
                                Material.NETHERITE_AXE, Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL));
                classHard.add(List.of(Material.SEA_LANTERN, Material.DARK_PRISMARINE, Material.PRISMARINE_BRICKS,
                                Material.PRISMARINE, Material.SPONGE, Material.MYCELIUM, Material.ICE,
                                Material.PACKED_ICE, Material.BLUE_ICE, Material.PRISMARINE_SHARD,
                                Material.PRISMARINE_CRYSTALS, Material.BEEHIVE));

        }

        public void registerClassExpert() {
                classExpert.add(List.of(Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE,
                                Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS, Material.TURTLE_HELMET,
                                Material.WITHER_SKELETON_SKULL, Material.ZOMBIE_HEAD, Material.SKELETON_SKULL,
                                Material.CREEPER_HEAD, Material.TRIDENT));
                classExpert.add(List.of(Material.CHORUS_FRUIT, Material.POPPED_CHORUS_FRUIT, Material.SHULKER_BOX,
                                Material.SHULKER_SHELL, Material.ELYTRA, Material.END_ROD, Material.CHORUS_FLOWER,
                                Material.PURPUR_PILLAR, Material.PURPUR_BLOCK, Material.RABBIT_FOOT));
                classExpert.add(List.of(Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE,
                                Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, Material.CONDUIT,
                                Material.BEACON, Material.ENCHANTED_GOLDEN_APPLE));

        }

        public void registerMaterials() {
                materials.put(Material.COMPOSTER, new ItemCode('\uEA00'));
                materials.put(Material.BARREL, new ItemCode('\uEA01'));
                materials.put(Material.LOOM, new ItemCode('\uEA02'));
                materials.put(Material.SMOKER, new ItemCode('\uEA03'));
                materials.put(Material.BLAST_FURNACE, new ItemCode('\uEA04'));
                materials.put(Material.CARTOGRAPHY_TABLE, new ItemCode('\uEA05'));
                materials.put(Material.FLETCHING_TABLE, new ItemCode('\uEA06'));
                materials.put(Material.GRINDSTONE, new ItemCode('\uEA07'));
                materials.put(Material.SMITHING_TABLE, new ItemCode('\uEA08'));
                materials.put(Material.STONECUTTER, new ItemCode('\uEA09'));
                materials.put(Material.BELL, new ItemCode('\uEA10'));
                materials.put(Material.LECTERN, new ItemCode('\uEA11'));
                materials.put(Material.CRAFTING_TABLE, new ItemCode('\uEA12'));
                materials.put(Material.FURNACE, new ItemCode('\uEA13'));
                materials.put(Material.DISPENSER, new ItemCode('\uEA14'));
                materials.put(Material.NOTE_BLOCK, new ItemCode('\uEA15'));
                materials.put(Material.PISTON, new ItemCode('\uEA16'));
                materials.put(Material.REDSTONE_LAMP, new ItemCode('\uEA17'));
                materials.put(Material.DROPPER, new ItemCode('\uEA18'));
                materials.put(Material.OBSERVER, new ItemCode('\uEA19'));
                materials.put(Material.REDSTONE_BLOCK, new ItemCode('\uEA20'));
                materials.put(Material.TNT, new ItemCode('\uEA21'));
                materials.put(Material.TARGET, new ItemCode('\uEA22'));
                materials.put(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, new ItemCode('\uEA23'));
                materials.put(Material.COMPARATOR, new ItemCode('\uEA24'));
                materials.put(Material.REPEATER, new ItemCode('\uEA25'));
                materials.put(Material.DAYLIGHT_DETECTOR, new ItemCode('\uEA26'));
                materials.put(Material.HOPPER, new ItemCode('\uEA27'));
                materials.put(Material.TRIPWIRE, new ItemCode('\uEA28'));
                materials.put(Material.REDSTONE_TORCH, new ItemCode('\uEA29'));
                materials.put(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, new ItemCode('\uEA30'));
                materials.put(Material.IRON_TRAPDOOR, new ItemCode('\uEA31'));
                materials.put(Material.ACTIVATOR_RAIL, new ItemCode('\uEA32'));
                materials.put(Material.RAIL, new ItemCode('\uEA33'));
                materials.put(Material.DETECTOR_RAIL, new ItemCode('\uEA34'));
                materials.put(Material.POWERED_RAIL, new ItemCode('\uEA35'));
                materials.put(Material.CHEST_MINECART, new ItemCode('\uEA36'));
                materials.put(Material.HOPPER_MINECART, new ItemCode('\uEA37'));
                materials.put(Material.FURNACE_MINECART, new ItemCode('\uEA38'));
                materials.put(Material.TNT_MINECART, new ItemCode('\uEA39'));
                materials.put(Material.MINECART, new ItemCode('\uEA40'));
                materials.put(Material.ACACIA_SAPLING, new ItemCode('\uEA41'));
                materials.put(Material.BIRCH_SAPLING, new ItemCode('\uEA42'));
                materials.put(Material.DARK_OAK_SAPLING, new ItemCode('\uEA43'));
                materials.put(Material.JUNGLE_SAPLING, new ItemCode('\uEA44'));
                materials.put(Material.OAK_SAPLING, new ItemCode('\uEA45'));
                materials.put(Material.SPRUCE_SAPLING, new ItemCode('\uEA46'));
                materials.put(Material.ALLIUM, new ItemCode('\uEA47'));
                materials.put(Material.AZURE_BLUET, new ItemCode('\uEA48'));
                materials.put(Material.CORNFLOWER, new ItemCode('\uEA49'));
                materials.put(Material.DANDELION, new ItemCode('\uEA50'));
                materials.put(Material.LILY_OF_THE_VALLEY, new ItemCode('\uEA51'));
                materials.put(Material.OXEYE_DAISY, new ItemCode('\uEA52'));
                materials.put(Material.POPPY, new ItemCode('\uEA53'));
                materials.put(Material.ORANGE_TULIP, new ItemCode('\uEA54'));
                materials.put(Material.PINK_TULIP, new ItemCode('\uEA55'));
                materials.put(Material.RED_TULIP, new ItemCode('\uEA56'));
                materials.put(Material.WHITE_TULIP, new ItemCode('\uEA57'));
                materials.put(Material.KELP, new ItemCode('\uEA58'));
                materials.put(Material.TWISTING_VINES, new ItemCode('\uEA59'));
                materials.put(Material.WEEPING_VINES, new ItemCode('\uEA60'));
                materials.put(Material.CRIMSON_ROOTS, new ItemCode('\uEA61'));
                materials.put(Material.WARPED_ROOTS, new ItemCode('\uEA62'));
                materials.put(Material.GRASS, new ItemCode('\uEA63'));
                materials.put(Material.FERN, new ItemCode('\uEA64'));
                materials.put(Material.VINE, new ItemCode('\uEA65'));
                materials.put(Material.SEAGRASS, new ItemCode('\uEA66'));
                materials.put(Material.NETHER_SPROUTS, new ItemCode('\uEA67'));
                materials.put(Material.DEAD_BUSH, new ItemCode('\uEA68'));
                materials.put(Material.LEATHER_HELMET, new ItemCode('\uEA69'));
                materials.put(Material.LEATHER_CHESTPLATE, new ItemCode('\uEA70'));
                materials.put(Material.LEATHER_LEGGINGS, new ItemCode('\uEA71'));
                materials.put(Material.LEATHER_BOOTS, new ItemCode('\uEA72'));
                materials.put(Material.GOLDEN_HELMET, new ItemCode('\uEA73'));
                materials.put(Material.GOLDEN_CHESTPLATE, new ItemCode('\uEA74'));
                materials.put(Material.GOLDEN_LEGGINGS, new ItemCode('\uEA75'));
                materials.put(Material.GOLDEN_BOOTS, new ItemCode('\uEA76'));
                materials.put(Material.IRON_HELMET, new ItemCode('\uEA77'));
                materials.put(Material.IRON_CHESTPLATE, new ItemCode('\uEA78'));
                materials.put(Material.IRON_LEGGINGS, new ItemCode('\uEA79'));
                materials.put(Material.IRON_BOOTS, new ItemCode('\uEA80'));
                materials.put(Material.GOLDEN_SWORD, new ItemCode('\uEA81'));
                materials.put(Material.GOLDEN_PICKAXE, new ItemCode('\uEA82'));
                materials.put(Material.GOLDEN_AXE, new ItemCode('\uEA83'));
                materials.put(Material.GOLDEN_SHOVEL, new ItemCode('\uEA84'));
                materials.put(Material.GOLDEN_HOE, new ItemCode('\uEA85'));
                materials.put(Material.IRON_SWORD, new ItemCode('\uEA86'));
                materials.put(Material.IRON_PICKAXE, new ItemCode('\uEA87'));
                materials.put(Material.IRON_AXE, new ItemCode('\uEA88'));
                materials.put(Material.IRON_SHOVEL, new ItemCode('\uEA89'));
                materials.put(Material.IRON_HOE, new ItemCode('\uEA90'));
                materials.put(Material.DIAMOND_SWORD, new ItemCode('\uEA91'));
                materials.put(Material.DIAMOND_PICKAXE, new ItemCode('\uEA92'));
                materials.put(Material.DIAMOND_AXE, new ItemCode('\uEA93'));
                materials.put(Material.DIAMOND_SHOVEL, new ItemCode('\uEA94'));
                materials.put(Material.DIAMOND_HOE, new ItemCode('\uEA95'));
                materials.put(Material.SHIELD, new ItemCode('\uEA96'));
                materials.put(Material.BOW, new ItemCode('\uEA97'));
                materials.put(Material.CROSSBOW, new ItemCode('\uEA98'));
                materials.put(Material.SHEARS, new ItemCode('\uEA99'));
                materials.put(Material.FISHING_ROD, new ItemCode('\uEB00'));
                materials.put(Material.CARROT_ON_A_STICK, new ItemCode('\uEB01'));
                materials.put(Material.WARPED_FUNGUS_ON_A_STICK, new ItemCode('\uEB02'));
                materials.put(Material.TURTLE_HELMET, new ItemCode('\uEB03'));
                materials.put(Material.DARK_OAK_BOAT, new ItemCode('\uEB04'));
                materials.put(Material.SPRUCE_BOAT, new ItemCode('\uEB05'));
                materials.put(Material.BIRCH_BOAT, new ItemCode('\uEB06'));
                materials.put(Material.JUNGLE_BOAT, new ItemCode('\uEB07'));
                materials.put(Material.OAK_BOAT, new ItemCode('\uEB08'));
                materials.put(Material.ACACIA_BOAT, new ItemCode('\uEB09'));
                materials.put(Material.WHITE_WOOL, new ItemCode('\uEB10'));
                materials.put(Material.ORANGE_WOOL, new ItemCode('\uEB11'));
                materials.put(Material.PINK_WOOL, new ItemCode('\uEB12'));
                materials.put(Material.LIGHT_BLUE_WOOL, new ItemCode('\uEB13'));
                materials.put(Material.YELLOW_WOOL, new ItemCode('\uEB14'));
                materials.put(Material.LIME_WOOL, new ItemCode('\uEB15'));
                materials.put(Material.MAGENTA_WOOL, new ItemCode('\uEB16'));
                materials.put(Material.GRAY_WOOL, new ItemCode('\uEB17'));
                materials.put(Material.LIGHT_GRAY_WOOL, new ItemCode('\uEB18'));
                materials.put(Material.CYAN_WOOL, new ItemCode('\uEB19'));
                materials.put(Material.PURPLE_WOOL, new ItemCode('\uEB20'));
                materials.put(Material.BLUE_WOOL, new ItemCode('\uEB21'));
                materials.put(Material.BROWN_WOOL, new ItemCode('\uEB22'));
                materials.put(Material.GREEN_WOOL, new ItemCode('\uEB23'));
                materials.put(Material.RED_WOOL, new ItemCode('\uEB24'));
                materials.put(Material.BLACK_WOOL, new ItemCode('\uEB25'));
                materials.put(Material.WHITE_CONCRETE, new ItemCode('\uEB26'));
                materials.put(Material.ORANGE_CONCRETE, new ItemCode('\uEB27'));
                materials.put(Material.PINK_CONCRETE, new ItemCode('\uEB28'));
                materials.put(Material.LIGHT_BLUE_CONCRETE, new ItemCode('\uEB29'));
                materials.put(Material.YELLOW_CONCRETE, new ItemCode('\uEB30'));
                materials.put(Material.LIME_CONCRETE, new ItemCode('\uEB31'));
                materials.put(Material.MAGENTA_CONCRETE, new ItemCode('\uEB32'));
                materials.put(Material.GRAY_CONCRETE, new ItemCode('\uEB33'));
                materials.put(Material.LIGHT_GRAY_CONCRETE, new ItemCode('\uEB34'));
                materials.put(Material.CYAN_CONCRETE, new ItemCode('\uEB35'));
                materials.put(Material.PURPLE_CONCRETE, new ItemCode('\uEB36'));
                materials.put(Material.BLUE_CONCRETE, new ItemCode('\uEB37'));
                materials.put(Material.BROWN_CONCRETE, new ItemCode('\uEB38'));
                materials.put(Material.GREEN_CONCRETE, new ItemCode('\uEB39'));
                materials.put(Material.RED_CONCRETE, new ItemCode('\uEB40'));
                materials.put(Material.BLACK_CONCRETE, new ItemCode('\uEB41'));
                materials.put(Material.STONE, new ItemCode('\uEB42'));
                materials.put(Material.RED_SAND, new ItemCode('\uEB43'));
                materials.put(Material.RED_SANDSTONE, new ItemCode('\uEB44'));
                materials.put(Material.CUT_RED_SANDSTONE, new ItemCode('\uEB45'));
                materials.put(Material.SMOOTH_RED_SANDSTONE, new ItemCode('\uEB46'));
                materials.put(Material.CHISELED_RED_SANDSTONE, new ItemCode('\uEB47'));
                materials.put(Material.SAND, new ItemCode('\uEB48'));
                materials.put(Material.SANDSTONE, new ItemCode('\uEB49'));
                materials.put(Material.CUT_SANDSTONE, new ItemCode('\uEB50'));
                materials.put(Material.SMOOTH_SANDSTONE, new ItemCode('\uEB51'));
                materials.put(Material.CHISELED_SANDSTONE, new ItemCode('\uEB52'));
                materials.put(Material.COBBLESTONE, new ItemCode('\uEB53'));
                materials.put(Material.MOSSY_COBBLESTONE, new ItemCode('\uEB54'));
                materials.put(Material.STONE_BRICKS, new ItemCode('\uEB55'));
                materials.put(Material.CRACKED_STONE_BRICKS, new ItemCode('\uEB56'));
                materials.put(Material.MOSSY_STONE_BRICKS, new ItemCode('\uEB57'));
                materials.put(Material.CHISELED_STONE_BRICKS, new ItemCode('\uEB58'));
                materials.put(Material.SMOOTH_STONE, new ItemCode('\uEB59'));
                materials.put(Material.BRICKS, new ItemCode('\uEB60'));
                materials.put(Material.OBSIDIAN, new ItemCode('\uEB61'));
                materials.put(Material.CLAY, new ItemCode('\uEB62'));
                materials.put(Material.GRAVEL, new ItemCode('\uEB63'));
                materials.put(Material.SNOW_BLOCK, new ItemCode('\uEB64'));
                materials.put(Material.OAK_LOG, new ItemCode('\uEB65'));
                materials.put(Material.BIRCH_LOG, new ItemCode('\uEB66'));
                materials.put(Material.SPRUCE_LOG, new ItemCode('\uEB67'));
                materials.put(Material.DARK_OAK_LOG, new ItemCode('\uEB68'));
                materials.put(Material.ACACIA_LOG, new ItemCode('\uEB69'));
                materials.put(Material.JUNGLE_LOG, new ItemCode('\uEB70'));
                materials.put(Material.CRIMSON_STEM, new ItemCode('\uEB71'));
                materials.put(Material.WARPED_STEM, new ItemCode('\uEB72'));
                materials.put(Material.OAK_LEAVES, new ItemCode('\uEB73'));
                materials.put(Material.BIRCH_LEAVES, new ItemCode('\uEB74'));
                materials.put(Material.SPRUCE_LEAVES, new ItemCode('\uEB75'));
                materials.put(Material.DARK_OAK_LEAVES, new ItemCode('\uEB76'));
                materials.put(Material.ACACIA_LEAVES, new ItemCode('\uEB77'));
                materials.put(Material.JUNGLE_LEAVES, new ItemCode('\uEB78'));
                materials.put(Material.NETHER_WART_BLOCK, new ItemCode('\uEB79'));
                materials.put(Material.WARPED_WART_BLOCK, new ItemCode('\uEB80'));
                materials.put(Material.NETHERRACK, new ItemCode('\uEB81'));
                materials.put(Material.NETHER_BRICK, new ItemCode('\uEB82'));
                materials.put(Material.CRACKED_NETHER_BRICKS, new ItemCode('\uEB83'));
                materials.put(Material.CHISELED_NETHER_BRICKS, new ItemCode('\uEB84'));
                materials.put(Material.BLACKSTONE, new ItemCode('\uEB85'));
                materials.put(Material.POLISHED_BLACKSTONE, new ItemCode('\uEB86'));
                materials.put(Material.POLISHED_BLACKSTONE_BRICKS, new ItemCode('\uEB87'));
                materials.put(Material.CRACKED_POLISHED_BLACKSTONE_BRICKS, new ItemCode('\uEB88'));
                materials.put(Material.CHISELED_POLISHED_BLACKSTONE, new ItemCode('\uEB89'));
                materials.put(Material.BASALT, new ItemCode('\uEB90'));
                materials.put(Material.POLISHED_BASALT, new ItemCode('\uEB91'));
                materials.put(Material.SOUL_SAND, new ItemCode('\uEB92'));
                materials.put(Material.SOUL_SOIL, new ItemCode('\uEB93'));
                materials.put(Material.QUARTZ_BLOCK, new ItemCode('\uEB94'));
                materials.put(Material.SMOOTH_QUARTZ, new ItemCode('\uEB95'));
                materials.put(Material.QUARTZ_BRICKS, new ItemCode('\uEB96'));
                materials.put(Material.QUARTZ_PILLAR, new ItemCode('\uEB97'));
                materials.put(Material.CHISELED_QUARTZ_BLOCK, new ItemCode('\uEB98'));
                materials.put(Material.MAGMA_BLOCK, new ItemCode('\uEB99'));
                materials.put(Material.BONE_BLOCK, new ItemCode('\uEC00'));
                materials.put(Material.GLOWSTONE, new ItemCode('\uEC01'));
                materials.put(Material.SHROOMLIGHT, new ItemCode('\uEC02'));
                materials.put(Material.WHITE_BED, new ItemCode('\uEC03'));
                materials.put(Material.ORANGE_BED, new ItemCode('\uEC04'));
                materials.put(Material.MAGENTA_BED, new ItemCode('\uEC05'));
                materials.put(Material.YELLOW_BED, new ItemCode('\uEC06'));
                materials.put(Material.LIGHT_BLUE_BED, new ItemCode('\uEC07'));
                materials.put(Material.LIME_BED, new ItemCode('\uEC08'));
                materials.put(Material.PINK_BED, new ItemCode('\uEC09'));
                materials.put(Material.GRAY_BED, new ItemCode('\uEC10'));
                materials.put(Material.LIGHT_GRAY_BED, new ItemCode('\uEC11'));
                materials.put(Material.CYAN_BED, new ItemCode('\uEC12'));
                materials.put(Material.PURPLE_BED, new ItemCode('\uEC13'));
                materials.put(Material.BLUE_BED, new ItemCode('\uEC14'));
                materials.put(Material.BROWN_BED, new ItemCode('\uEC15'));
                materials.put(Material.GREEN_BED, new ItemCode('\uEC16'));
                materials.put(Material.RED_BED, new ItemCode('\uEC17'));
                materials.put(Material.BLACK_BED, new ItemCode('\uEC18'));
                materials.put(Material.WHITE_BANNER, new ItemCode('\uEC19'));
                materials.put(Material.ORANGE_BANNER, new ItemCode('\uEC20'));
                materials.put(Material.MAGENTA_BANNER, new ItemCode('\uEC21'));
                materials.put(Material.YELLOW_BANNER, new ItemCode('\uEC22'));
                materials.put(Material.LIGHT_BLUE_BANNER, new ItemCode('\uEC23'));
                materials.put(Material.LIME_BANNER, new ItemCode('\uEC24'));
                materials.put(Material.PINK_BANNER, new ItemCode('\uEC25'));
                materials.put(Material.GRAY_BANNER, new ItemCode('\uEC26'));
                materials.put(Material.LIGHT_GRAY_BANNER, new ItemCode('\uEC27'));
                materials.put(Material.CYAN_BANNER, new ItemCode('\uEC28'));
                materials.put(Material.PURPLE_BANNER, new ItemCode('\uEC29'));
                materials.put(Material.BLUE_BANNER, new ItemCode('\uEC30'));
                materials.put(Material.BROWN_BANNER, new ItemCode('\uEC31'));
                materials.put(Material.GREEN_BANNER, new ItemCode('\uEC32'));
                materials.put(Material.RED_BANNER, new ItemCode('\uEC33'));
                materials.put(Material.BLACK_BANNER, new ItemCode('\uEC34'));
                materials.put(Material.WHITE_STAINED_GLASS, new ItemCode('\uEC35'));
                materials.put(Material.ORANGE_STAINED_GLASS, new ItemCode('\uEC36'));
                materials.put(Material.MAGENTA_STAINED_GLASS, new ItemCode('\uEC37'));
                materials.put(Material.YELLOW_STAINED_GLASS, new ItemCode('\uEC38'));
                materials.put(Material.LIGHT_BLUE_STAINED_GLASS, new ItemCode('\uEC39'));
                materials.put(Material.LIME_STAINED_GLASS, new ItemCode('\uEC40'));
                materials.put(Material.PINK_STAINED_GLASS, new ItemCode('\uEC41'));
                materials.put(Material.GRAY_STAINED_GLASS, new ItemCode('\uEC42'));
                materials.put(Material.LIGHT_GRAY_STAINED_GLASS, new ItemCode('\uEC43'));
                materials.put(Material.CYAN_STAINED_GLASS, new ItemCode('\uEC44'));
                materials.put(Material.PURPLE_STAINED_GLASS, new ItemCode('\uEC45'));
                materials.put(Material.BLUE_STAINED_GLASS, new ItemCode('\uEC46'));
                materials.put(Material.BROWN_STAINED_GLASS, new ItemCode('\uEC47'));
                materials.put(Material.GREEN_STAINED_GLASS, new ItemCode('\uEC48'));
                materials.put(Material.RED_STAINED_GLASS, new ItemCode('\uEC49'));
                materials.put(Material.BLACK_STAINED_GLASS, new ItemCode('\uEC50'));
                materials.put(Material.GLASS, new ItemCode('\uEC51'));
                materials.put(Material.WHITE_TERRACOTTA, new ItemCode('\uEC52'));
                materials.put(Material.ORANGE_TERRACOTTA, new ItemCode('\uEC53'));
                materials.put(Material.MAGENTA_TERRACOTTA, new ItemCode('\uEC54'));
                materials.put(Material.YELLOW_TERRACOTTA, new ItemCode('\uEC55'));
                materials.put(Material.LIGHT_BLUE_TERRACOTTA, new ItemCode('\uEC56'));
                materials.put(Material.LIME_TERRACOTTA, new ItemCode('\uEC57'));
                materials.put(Material.PINK_TERRACOTTA, new ItemCode('\uEC58'));
                materials.put(Material.GRAY_TERRACOTTA, new ItemCode('\uEC59'));
                materials.put(Material.LIGHT_GRAY_TERRACOTTA, new ItemCode('\uEC60'));
                materials.put(Material.CYAN_TERRACOTTA, new ItemCode('\uEC61'));
                materials.put(Material.PURPLE_TERRACOTTA, new ItemCode('\uEC62'));
                materials.put(Material.BLUE_TERRACOTTA, new ItemCode('\uEC63'));
                materials.put(Material.BROWN_TERRACOTTA, new ItemCode('\uEC64'));
                materials.put(Material.GREEN_TERRACOTTA, new ItemCode('\uEC65'));
                materials.put(Material.RED_TERRACOTTA, new ItemCode('\uEC66'));
                materials.put(Material.BLACK_TERRACOTTA, new ItemCode('\uEC67'));
                materials.put(Material.TERRACOTTA, new ItemCode('\uEC68'));
                materials.put(Material.CHARCOAL, new ItemCode('\uEC69'));
                materials.put(Material.COAL, new ItemCode('\uEC70'));
                materials.put(Material.IRON_INGOT, new ItemCode('\uEC71'));
                materials.put(Material.GOLD_INGOT, new ItemCode('\uEC72'));
                materials.put(Material.EMERALD, new ItemCode('\uEC73'));
                materials.put(Material.LAPIS_LAZULI, new ItemCode('\uEC74'));
                materials.put(Material.DIAMOND, new ItemCode('\uEC75'));
                materials.put(Material.QUARTZ, new ItemCode('\uEC76'));
                materials.put(Material.REDSTONE, new ItemCode('\uEC77'));
                materials.put(Material.IRON_BLOCK, new ItemCode('\uEC78'));
                materials.put(Material.GOLD_BLOCK, new ItemCode('\uEC79'));
                materials.put(Material.EMERALD_BLOCK, new ItemCode('\uEC80'));
                materials.put(Material.LAPIS_BLOCK, new ItemCode('\uEC81'));
                materials.put(Material.DIAMOND_BLOCK, new ItemCode('\uEC82'));
                materials.put(Material.BRICK, new ItemCode('\uEC83'));
                materials.put(Material.INK_SAC, new ItemCode('\uEC84'));
                materials.put(Material.LEATHER, new ItemCode('\uEC85'));
                materials.put(Material.BONE_MEAL, new ItemCode('\uEC86'));
                materials.put(Material.SNOWBALL, new ItemCode('\uEC87'));
                materials.put(Material.COCOA_BEANS, new ItemCode('\uEC88'));
                materials.put(Material.MELON_SEEDS, new ItemCode('\uEC89'));
                materials.put(Material.PUMPKIN_SEEDS, new ItemCode('\uEC90'));
                materials.put(Material.NETHER_WART, new ItemCode('\uEC91'));
                materials.put(Material.APPLE, new ItemCode('\uEC92'));
                materials.put(Material.CARROT, new ItemCode('\uEC93'));
                materials.put(Material.BREAD, new ItemCode('\uEC94'));
                materials.put(Material.COOKED_BEEF, new ItemCode('\uEC95'));
                materials.put(Material.COOKED_MUTTON, new ItemCode('\uEC96'));
                materials.put(Material.COOKED_PORKCHOP, new ItemCode('\uEC97'));
                materials.put(Material.COOKED_COD, new ItemCode('\uEC98'));
                materials.put(Material.COOKED_SALMON, new ItemCode('\uEC99'));
                materials.put(Material.COOKED_CHICKEN, new ItemCode('\uED00'));
                materials.put(Material.SPIDER_EYE, new ItemCode('\uED01'));
                materials.put(Material.ROTTEN_FLESH, new ItemCode('\uED02'));
                materials.put(Material.MUSHROOM_STEW, new ItemCode('\uED03'));
                materials.put(Material.BAKED_POTATO, new ItemCode('\uED04'));
                materials.put(Material.DRIED_KELP, new ItemCode('\uED05'));
                materials.put(Material.FERMENTED_SPIDER_EYE, new ItemCode('\uED06'));
                materials.put(Material.RABBIT_STEW, new ItemCode('\uED07'));
                materials.put(Material.BEETROOT_SOUP, new ItemCode('\uED08'));
                materials.put(Material.COOKIE, new ItemCode('\uED09'));
                materials.put(Material.SWEET_BERRIES, new ItemCode('\uED10'));
                materials.put(Material.SEA_PICKLE, new ItemCode('\uED11'));
                materials.put(Material.HONEY_BOTTLE, new ItemCode('\uED12'));
                materials.put(Material.COOKED_RABBIT, new ItemCode('\uED13'));
                materials.put(Material.EGG, new ItemCode('\uED14'));
                materials.put(Material.PUMPKIN_PIE, new ItemCode('\uED15'));
                materials.put(Material.POISONOUS_POTATO, new ItemCode('\uED16'));
                materials.put(Material.TROPICAL_FISH, new ItemCode('\uED17'));
                materials.put(Material.TROPICAL_FISH_BUCKET, new ItemCode('\uED18'));
                materials.put(Material.MELON_SLICE, new ItemCode('\uED19'));
                materials.put(Material.ENDER_PEARL, new ItemCode('\uED20'));
                materials.put(Material.SADDLE, new ItemCode('\uED21'));
                materials.put(Material.MAGMA_CREAM, new ItemCode('\uED22'));
                materials.put(Material.GLISTERING_MELON_SLICE, new ItemCode('\uED23'));
                materials.put(Material.CRYING_OBSIDIAN, new ItemCode('\uED24'));
                materials.put(Material.GILDED_BLACKSTONE, new ItemCode('\uED25'));
                materials.put(Material.ENCHANTING_TABLE, new ItemCode('\uED26'));
                materials.put(Material.BROWN_MUSHROOM, new ItemCode('\uED27'));
                materials.put(Material.RED_MUSHROOM, new ItemCode('\uED28'));
                materials.put(Material.CAKE, new ItemCode('\uED29'));
                materials.put(Material.SUNFLOWER, new ItemCode('\uED30'));
                materials.put(Material.BLAZE_ROD, new ItemCode('\uED31'));
                materials.put(Material.BREWING_STAND, new ItemCode('\uED32'));
                materials.put(Material.BLAZE_POWDER, new ItemCode('\uED33'));
                materials.put(Material.PUFFERFISH_BUCKET, new ItemCode('\uED34'));
                materials.put(Material.NAME_TAG, new ItemCode('\uED35'));
                materials.put(Material.HEART_OF_THE_SEA, new ItemCode('\uED36'));
                materials.put(Material.GRASS_BLOCK, new ItemCode('\uED37'));
                materials.put(Material.NETHER_GOLD_ORE, new ItemCode('\uED38'));
                materials.put(Material.COAL_ORE, new ItemCode('\uED39'));
                materials.put(Material.REDSTONE_ORE, new ItemCode('\uED40'));
                materials.put(Material.DIAMOND_ORE, new ItemCode('\uED41'));
                materials.put(Material.LAPIS_ORE, new ItemCode('\uED42'));
                materials.put(Material.EMERALD_ORE, new ItemCode('\uED43'));
                materials.put(Material.BEE_NEST, new ItemCode('\uED44'));
                materials.put(Material.MUSHROOM_STEM, new ItemCode('\uED45'));
                materials.put(Material.RED_MUSHROOM_BLOCK, new ItemCode('\uED46'));
                materials.put(Material.BROWN_MUSHROOM_BLOCK, new ItemCode('\uED47'));
                materials.put(Material.PODZOL, new ItemCode('\uED48'));
                materials.put(Material.NETHER_QUARTZ_ORE, new ItemCode('\uED49'));
                materials.put(Material.NETHERITE_SCRAP, new ItemCode('\uED50'));
                materials.put(Material.JUKEBOX, new ItemCode('\uED51'));
                materials.put(Material.DEAD_HORN_CORAL_BLOCK, new ItemCode('\uED52'));
                materials.put(Material.DEAD_FIRE_CORAL_BLOCK, new ItemCode('\uED53'));
                materials.put(Material.DEAD_TUBE_CORAL_BLOCK, new ItemCode('\uED54'));
                materials.put(Material.DEAD_BRAIN_CORAL_BLOCK, new ItemCode('\uED55'));
                materials.put(Material.DEAD_BUBBLE_CORAL_BLOCK, new ItemCode('\uED56'));
                materials.put(Material.HORN_CORAL_BLOCK, new ItemCode('\uED52'));
                materials.put(Material.FIRE_CORAL_BLOCK, new ItemCode('\uED53'));
                materials.put(Material.TUBE_CORAL_BLOCK, new ItemCode('\uED54'));
                materials.put(Material.BRAIN_CORAL_BLOCK, new ItemCode('\uED55'));
                materials.put(Material.BUBBLE_CORAL_BLOCK, new ItemCode('\uED56'));
                materials.put(Material.PUFFERFISH, new ItemCode('\uED57'));
                materials.put(Material.HONEYCOMB, new ItemCode('\uED58'));
                materials.put(Material.HONEYCOMB_BLOCK, new ItemCode('\uED59'));
                materials.put(Material.SCUTE, new ItemCode('\uED60'));
                materials.put(Material.PHANTOM_MEMBRANE, new ItemCode('\uED61'));
                materials.put(Material.GHAST_TEAR, new ItemCode('\uED62'));
                materials.put(Material.END_CRYSTAL, new ItemCode('\uED63'));
                materials.put(Material.MUSIC_DISC_PIGSTEP, new ItemCode('\uED64'));
                materials.put(Material.RESPAWN_ANCHOR, new ItemCode('\uED65'));
                materials.put(Material.BAMBOO, new ItemCode('\uED66'));
                materials.put(Material.SCAFFOLDING, new ItemCode('\uED67'));
                materials.put(Material.TURTLE_EGG, new ItemCode('\uED68'));
                materials.put(Material.SLIME_BLOCK, new ItemCode('\uED69'));
                materials.put(Material.HONEY_BLOCK, new ItemCode('\uED70'));
                materials.put(Material.STICKY_PISTON, new ItemCode('\uED71'));
                materials.put(Material.NETHERITE_INGOT, new ItemCode('\uED72'));
                materials.put(Material.LODESTONE, new ItemCode('\uED73'));
                materials.put(Material.NETHERITE_HOE, new ItemCode('\uED74'));
                materials.put(Material.NETHERITE_AXE, new ItemCode('\uED75'));
                materials.put(Material.NETHERITE_PICKAXE, new ItemCode('\uED76'));
                materials.put(Material.NETHERITE_SHOVEL, new ItemCode('\uED77'));
                materials.put(Material.SEA_LANTERN, new ItemCode('\uED78'));
                materials.put(Material.DARK_PRISMARINE, new ItemCode('\uED79'));
                materials.put(Material.PRISMARINE_BRICKS, new ItemCode('\uED80'));
                materials.put(Material.PRISMARINE, new ItemCode('\uED81'));
                materials.put(Material.SPONGE, new ItemCode('\uED82'));
                materials.put(Material.MYCELIUM, new ItemCode('\uED83'));
                materials.put(Material.ICE, new ItemCode('\uED84'));
                materials.put(Material.PACKED_ICE, new ItemCode('\uED85'));
                materials.put(Material.BLUE_ICE, new ItemCode('\uED86'));
                materials.put(Material.PRISMARINE_SHARD, new ItemCode('\uED87'));
                materials.put(Material.PRISMARINE_CRYSTALS, new ItemCode('\uED88'));
                materials.put(Material.BEEHIVE, new ItemCode('\uED89'));
                materials.put(Material.NETHERITE_HELMET, new ItemCode('\uED90'));
                materials.put(Material.NETHERITE_CHESTPLATE, new ItemCode('\uED91'));
                materials.put(Material.NETHERITE_LEGGINGS, new ItemCode('\uED92'));
                materials.put(Material.NETHERITE_BOOTS, new ItemCode('\uED93'));
                materials.put(Material.WITHER_SKELETON_SKULL, new ItemCode('\uED94'));
                materials.put(Material.RABBIT_FOOT, new ItemCode('\uED95'));
                materials.put(Material.CHORUS_FRUIT, new ItemCode('\uED96'));
                materials.put(Material.POPPED_CHORUS_FRUIT, new ItemCode('\uED97'));
                materials.put(Material.ENCHANTED_GOLDEN_APPLE, new ItemCode('\uED98'));
                materials.put(Material.TRIDENT, new ItemCode('\uED99'));

                materials.put(Material.SHULKER_BOX, new ItemCode('\uEE00'));
                materials.put(Material.SHULKER_SHELL, new ItemCode('\uEE01'));
                materials.put(Material.ELYTRA, new ItemCode('\uEE02'));
                materials.put(Material.CHORUS_FLOWER, new ItemCode('\uEE03'));
                materials.put(Material.PURPUR_BLOCK, new ItemCode('\uEE04'));
                materials.put(Material.PURPUR_PILLAR, new ItemCode('\uEE05'));
                materials.put(Material.BEACON, new ItemCode('\uEE06'));
                materials.put(Material.ZOMBIE_HEAD, new ItemCode('\uEE07'));
                materials.put(Material.SKELETON_SKULL, new ItemCode('\uEE08'));
                materials.put(Material.CREEPER_HEAD, new ItemCode('\uEE09'));
                materials.put(Material.CONDUIT, new ItemCode('\uEE10'));
                materials.put(Material.CHAINMAIL_HELMET, new ItemCode('\uEE11'));
                materials.put(Material.CHAINMAIL_CHESTPLATE, new ItemCode('\uEE12'));
                materials.put(Material.CHAINMAIL_LEGGINGS, new ItemCode('\uEE13'));
                materials.put(Material.CHAINMAIL_BOOTS, new ItemCode('\uEE14'));

                materials.put(Material.ANVIL, new ItemCode('\uEE15'));
                materials.put(Material.DRIED_KELP_BLOCK, new ItemCode('\uEE16'));
                materials.put(Material.LADDER, new ItemCode('\uEE17'));
                materials.put(Material.FLOWER_POT, new ItemCode('\uEE18'));
                materials.put(Material.COBWEB, new ItemCode('\uEE19'));
                materials.put(Material.ITEM_FRAME, new ItemCode('\uEE20'));
                materials.put(Material.PAINTING, new ItemCode('\uEE21'));
                materials.put(Material.ARMOR_STAND, new ItemCode('\uEE22'));
                materials.put(Material.HAY_BLOCK, new ItemCode('\uEE23'));
                materials.put(Material.BREAD, new ItemCode('\uEE24'));
                materials.put(Material.CHAIN, new ItemCode('\uEE25'));
                materials.put(Material.IRON_BARS, new ItemCode('\uEE26'));
                materials.put(Material.IRON_DOOR, new ItemCode('\uEE27'));
                materials.put(Material.CAMPFIRE, new ItemCode('\uEE28'));
                materials.put(Material.TORCH, new ItemCode('\uEE29'));
                materials.put(Material.LANTERN, new ItemCode('\uEE30'));
                materials.put(Material.SOUL_CAMPFIRE, new ItemCode('\uEE31'));
                materials.put(Material.SOUL_TORCH, new ItemCode('\uEE32'));
                materials.put(Material.SOUL_LANTERN, new ItemCode('\uEE33'));
                materials.put(Material.FIREWORK_STAR, new ItemCode('\uEE34'));
                materials.put(Material.FIREWORK_ROCKET, new ItemCode('\uEE35'));
                materials.put(Material.FIRE_CHARGE, new ItemCode('\uEE36'));
                materials.put(Material.SALMON_BUCKET, new ItemCode('\uEE37'));
                materials.put(Material.COD_BUCKET, new ItemCode('\uEE38'));

        }
}