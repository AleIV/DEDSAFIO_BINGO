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
import me.aleiv.core.paper.game.objects.ItemCode;
import me.aleiv.core.paper.game.objects.Table;
import me.aleiv.core.paper.game.objects.Timer;
import me.aleiv.core.paper.utilities.FastBoard;

@Data
@EqualsAndHashCode(callSuper = false)
public class Game extends BukkitRunnable {
        Core instance;

        long gameTime = 0;
        long startTime = 0;

        long gameStartTime = 0;
        GameStage gameStage;
        BingoRound bingoRound;
        BingoFase bingoFase;

        Timer timer;

        int neg = 0;
        int neg2 = -8;
        int neg3 = -6;

        String color1 = "#f31b2d"; // INFO COLOR
        String color2 = "#a6e316"; // AWARD COLOR
        String color3 = "#ac1133"; // ERROR COLOR
        String color4 = "#16e384"; // CONFIG COLOR

        List<Table> tables = new ArrayList<>();
        HashMap<String, FastBoard> boards = new HashMap<>();
        HashMap<Material, ItemCode> materials = new HashMap<>();
        HashMap<Challenge, ItemCode> challenges = new HashMap<>();

        HashMap<BingoRound, List<List<Material>>> itemRounds = new HashMap<>();
        HashMap<BingoRound, List<Challenge>> challengeRounds = new HashMap<>();

        public Game(Core instance) {
                this.instance = instance;
                this.startTime = System.currentTimeMillis();
                this.gameTime = 0;
                this.gameStage = GameStage.LOBBY;
                this.bingoRound = BingoRound.ONE;
                this.bingoFase = BingoFase.ITEMS;

                this.timer = new Timer(instance, (int) gameTime);

                registerMaterials();
                registerChallenges();

                itemRounds.put(BingoRound.ONE, new ArrayList<>());
                itemRounds.put(BingoRound.TWO, new ArrayList<>());

                registerItems_1();
                registerItems_2();

                challengeRounds.put(BingoRound.ONE, new ArrayList<>());
                challengeRounds.put(BingoRound.TWO, new ArrayList<>());
                challengeRounds.put(BingoRound.THREE, new ArrayList<>());

                register_challenge_1();
                register_challenge_2();
                register_challenge_3();

        }

        @Override
        public void run() {

                var new_time = (int) (Math.floor((System.currentTimeMillis() - startTime) / 1000.0));

                gameTime = new_time;

                Bukkit.getPluginManager().callEvent(new GameTickEvent(new_time, true));
        }


        public enum BingoRound {
                ONE, TWO, THREE;
        }

        public enum BingoType {
                FULL, LINE;
        }

        public enum BingoFase {
                ITEMS, CHALLENGE;
        }

        public enum GameStage {
                LOBBY, STARTING, INGAME, POSTGAME;
        }

        public void registerItems_1() {
                var items_1 = itemRounds.get(BingoRound.ONE);

                items_1.add(List.of(Material.DIAMOND, Material.GOLD_BLOCK, Material.LAPIS_BLOCK));

                items_1.add(List.of(Material.EMERALD, Material.GOLD_INGOT));

                items_1.add(List.of(Material.OAK_LEAVES, Material.BIRCH_LEAVES, Material.SPRUCE_LEAVES,
                                Material.OAK_SAPLING, Material.BIRCH_SAPLING, Material.SPRUCE_SAPLING));

                items_1.add(List.of(Material.WHITE_WOOL, Material.ORANGE_WOOL, Material.PINK_WOOL,
                                Material.LIGHT_BLUE_WOOL, Material.YELLOW_WOOL, Material.LIME_WOOL,
                                Material.MAGENTA_WOOL, Material.GRAY_WOOL, Material.LIGHT_GRAY_WOOL, Material.CYAN_WOOL,
                                Material.PURPLE_WOOL, Material.BLUE_WOOL, Material.GREEN_WOOL, Material.RED_WOOL,
                                Material.BLACK_WOOL));

                items_1.add(List.of(Material.CAMPFIRE, Material.LANTERN, Material.TORCH));

                items_1.add(List.of(Material.BOW, Material.FISHING_ROD, Material.SHIELD, Material.CROSSBOW,
                                Material.SHEARS, Material.FLINT_AND_STEEL));

                items_1.add(List.of(Material.PAINTING, Material.ITEM_FRAME, Material.LADDER, Material.FLOWER_POT, Material.CHAIN));

                items_1.add(List.of(Material.WHITE_STAINED_GLASS, Material.ORANGE_STAINED_GLASS,
                                Material.MAGENTA_STAINED_GLASS, Material.YELLOW_STAINED_GLASS,
                                Material.LIGHT_BLUE_STAINED_GLASS, Material.LIME_STAINED_GLASS,
                                Material.PINK_STAINED_GLASS, Material.GRAY_STAINED_GLASS,
                                Material.LIGHT_GRAY_STAINED_GLASS, Material.CYAN_STAINED_GLASS,
                                Material.PURPLE_STAINED_GLASS, Material.BLUE_STAINED_GLASS,
                                Material.GREEN_STAINED_GLASS, Material.RED_STAINED_GLASS, Material.BLACK_STAINED_GLASS,
                                Material.GLASS));

                items_1.add(List.of(Material.COMPOSTER, Material.FURNACE, Material.BARREL, Material.LOOM,
                                Material.SMOKER, Material.BLAST_FURNACE, Material.CARTOGRAPHY_TABLE,
                                Material.FLETCHING_TABLE, Material.GRINDSTONE, Material.SMITHING_TABLE,
                                Material.STONECUTTER, Material.LECTERN));

                items_1.add(List.of(Material.STONE_BRICKS, Material.CHISELED_STONE_BRICKS, Material.BRICKS,
                                Material.SMOOTH_STONE, Material.SMOOTH_SANDSTONE, Material.CRACKED_STONE_BRICKS));

                items_1.add(List.of(Material.REPEATER, Material.IRON_DOOR, Material.IRON_BARS, Material.ACTIVATOR_RAIL,
                                Material.DETECTOR_RAIL));

                items_1.add(List.of(Material.DISPENSER, Material.NOTE_BLOCK, Material.PISTON, Material.TARGET,
                                Material.HOPPER, Material.TNT));

                items_1.add(List.of(Material.CHEST_MINECART, Material.FURNACE_MINECART, Material.FIREWORK_STAR,
                                Material.FIREWORK_ROCKET));

                items_1.add(List.of(Material.INK_SAC, Material.BONE_MEAL, Material.SNOWBALL, Material.LEATHER));

                items_1.add(List.of(Material.CHARCOAL, Material.DRIED_KELP, Material.SEAGRASS, Material.GRASS,
                                Material.FERN, Material.DEAD_BUSH, Material.KELP));

                items_1.add(List.of(Material.COOKED_BEEF, Material.COOKED_MUTTON, Material.COOKED_PORKCHOP,
                                Material.COOKED_COD, Material.COOKED_SALMON, Material.COOKED_CHICKEN));

                items_1.add(List.of(Material.APPLE, Material.CARROT, Material.SPIDER_EYE, Material.ROTTEN_FLESH,
                                Material.MUSHROOM_STEW, Material.BAKED_POTATO, Material.BREAD, Material.SWEET_BERRIES));

                items_1.add(List.of(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS,
                                Material.IRON_BOOTS, Material.IRON_SWORD, Material.IRON_PICKAXE, Material.IRON_AXE,
                                Material.IRON_SHOVEL, Material.IRON_HOE));

                items_1.add(List.of(Material.WHITE_BED, Material.ORANGE_BED, Material.MAGENTA_BED, Material.YELLOW_BED,
                                Material.LIGHT_BLUE_BED, Material.LIME_BED, Material.PINK_BED, Material.GRAY_BED,
                                Material.LIGHT_GRAY_BED, Material.CYAN_BED, Material.PURPLE_BED, Material.BLUE_BED,
                                Material.GREEN_BED, Material.RED_BED, Material.BLACK_BED));

                items_1.add(List.of(Material.WHITE_BANNER, Material.ORANGE_BANNER, Material.MAGENTA_BANNER,
                                Material.YELLOW_BANNER, Material.LIGHT_BLUE_BANNER, Material.LIME_BANNER,
                                Material.PINK_BANNER, Material.GRAY_BANNER, Material.LIGHT_GRAY_BANNER,
                                Material.CYAN_BANNER, Material.PURPLE_BANNER, Material.BLUE_BANNER,
                                Material.GREEN_BANNER, Material.RED_BANNER, Material.BLACK_BANNER));

                items_1.add(List.of(Material.WHITE_TERRACOTTA, Material.ORANGE_TERRACOTTA, Material.MAGENTA_TERRACOTTA,
                                Material.YELLOW_TERRACOTTA, Material.LIGHT_BLUE_TERRACOTTA, Material.LIME_TERRACOTTA,
                                Material.PINK_TERRACOTTA, Material.GRAY_TERRACOTTA, Material.LIGHT_GRAY_TERRACOTTA,
                                Material.CYAN_TERRACOTTA, Material.PURPLE_TERRACOTTA, Material.BLUE_TERRACOTTA,
                                Material.GREEN_TERRACOTTA, Material.RED_TERRACOTTA, Material.BLACK_TERRACOTTA,
                                Material.TERRACOTTA));

                items_1.add(List.of(Material.GRAVEL, Material.CLAY, Material.SNOW_BLOCK, Material.STONE,
                                Material.COBBLESTONE, Material.SAND));

                items_1.add(List.of(Material.WHITE_CONCRETE, Material.ORANGE_CONCRETE, Material.PINK_CONCRETE,
                                Material.LIGHT_BLUE_CONCRETE, Material.YELLOW_CONCRETE, Material.LIME_CONCRETE,
                                Material.MAGENTA_CONCRETE, Material.GRAY_CONCRETE, Material.LIGHT_GRAY_CONCRETE,
                                Material.CYAN_CONCRETE, Material.PURPLE_CONCRETE, Material.BLUE_CONCRETE,
                                Material.GREEN_CONCRETE, Material.RED_CONCRETE, Material.BLACK_CONCRETE));

                items_1.add(List.of(Material.GOLDEN_HELMET, Material.GOLDEN_BOOTS, Material.GOLDEN_SWORD,
                                Material.GOLDEN_PICKAXE, Material.GOLDEN_AXE, Material.GOLDEN_SHOVEL,
                                Material.GOLDEN_HOE));

                items_1.add(List.of(Material.AZURE_BLUET, Material.ALLIUM, Material.OXEYE_DAISY, Material.CORNFLOWER,
                                Material.LILY_OF_THE_VALLEY, Material.DANDELION, Material.POPPY));
        }

        public void registerItems_2() {

                var items_2 = itemRounds.get(BingoRound.TWO);

                items_2.add(List.of(Material.SOUL_CAMPFIRE, Material.SOUL_TORCH, Material.SOUL_LANTERN));

                items_2.add(List.of(Material.BLAZE_ROD, Material.BREWING_STAND, Material.BLAZE_POWDER));

                items_2.add(List.of(Material.MAGMA_CREAM, Material.FIRE_CHARGE, Material.NETHER_WART));

                items_2.add(List.of(Material.ENDER_PEARL));

                items_2.add(List.of(Material.GLOWSTONE, Material.SHROOMLIGHT, Material.MAGMA_BLOCK));

                items_2.add(List.of(Material.CRACKED_NETHER_BRICKS,
                                Material.CHISELED_NETHER_BRICKS,
                                Material.SMOOTH_QUARTZ, Material.QUARTZ_BRICKS, Material.QUARTZ_PILLAR,
                                Material.CHISELED_QUARTZ_BLOCK));

                items_2.add(List.of(Material.NETHERRACK, Material.QUARTZ_BLOCK));

                items_2.add(List.of(Material.BLACKSTONE, Material.BASALT, Material.POLISHED_BASALT, Material.POLISHED_BLACKSTONE,
                                        Material.POLISHED_BLACKSTONE_BRICKS, Material.CHISELED_POLISHED_BLACKSTONE));

                items_2.add(List.of(Material.BONE_BLOCK, Material.SOUL_SAND, Material.SOUL_SOIL));

                items_2.add(List.of(Material.TWISTING_VINES, Material.WARPED_ROOTS, Material.WARPED_STEM,
                                Material.WARPED_WART_BLOCK));
                
                items_2.add(List.of(Material.WEEPING_VINES, Material.CRIMSON_ROOTS, Material.NETHER_WART_BLOCK,
                                Material.CRIMSON_STEM));

                items_2.add(List.of(Material.CRYING_OBSIDIAN, Material.GILDED_BLACKSTONE));

                items_2.add(List.of(Material.COMPARATOR, Material.DAYLIGHT_DETECTOR, Material.POWERED_RAIL, Material.TNT_MINECART));

                items_2.add(List.of(Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SWORD,
                                Material.DIAMOND_HOE));

                items_2.add(List.of(Material.CARROT_ON_A_STICK, Material.WARPED_FUNGUS_ON_A_STICK));

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
                materials.put(Material.NETHER_BRICKS, new ItemCode('\uEB82'));
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
                materials.put(Material.WHEAT, new ItemCode('\uEE24'));
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
                materials.put(Material.FLINT_AND_STEEL, new ItemCode('\uEE39'));


        }

        public enum Challenge {
                HALF_HEART, SHIELD_BREAK, ARMOR_MATERIALS, DOLPHIN_SWIM, MAXIMUM_HEIGHT, NETHER_TREE, SHOOT_TARGET, HOSTILE_KILL, ANIMAL_KILL, CAMPFIRE_CAMPING, 
                PINK_SHEEP_BIOME, BREAK_RULE_1, JUMP_BED, BONE_MEAL_COMPOSTER, DONKEY_CHEST, CREEPER_IGNITE, DROWN_VILLAGER, REDSTONE_SIGNAL, PURPLE_LLAMA, EAT_FOOD, COLOR_SHEEP, LVL_40,
                ANVIL_DAMAGE, ACUATIC_KILL, MINE_MINERALS, GET_POISON, INVENTORY_STACKS, STRIDER_GAPPLE, PIGLIN_BARTER, COMPLETE_MAP, VILLAGER_MAX_TRADE, CROSSBOW_SHOT, SHIELD_BANNER,
                ENCHANT_LVL6, CAMPFIRE_HAY_BALE, BED_EXPLODE, FISH_ITEMS, GIVE_PLAYER_FLOWER, WATER_DROP, EMPTY_CAULDRON, BREED_ANIMALS, POTION_EFFECTS, MILK_ZOMBIE, POTION_TYPES, EQUIP_DONKEY_CHEST, 
                HUNGER_DAMAGE, HOGLIN_SCARE, PIGLIN_SCARE, SHOOT_PORTAL, RIDE_HORSE_MINECART, CAKE_EAT, END_LEAVE, PLAYER_FISH, PIG_FALL, FULL_ARMOR, FIREWORK_CROSSBOW,
                CREEPER_TNT_KILL, NETHER_MOB_KILL, ENDER_EYE_THROW, FLYING_MOBS_KILL, LINGERING_WATER_POTION, ENDER_PEARL_TRAVEL, VILLAGER_EXPENSIVE_TRADE,
                EAT_SUS_STEW, MINE_LIGHT_SOURCE, GLOWING_BAT, TEAM_SPAWN_ANCHOR, NOTEBLOCK_INSTRUMENTS, DROWNED_MAP, TEAM_DANCE, SNOWBALL_BLAZE_KILL,
                OBTAIN_MULE, FARM_CROPS, END_BINGO_MAX_HEIGHT, JARDINERO, OVER_ZOGLIN

        }

        public void registerChallenges(){

                challenges.put(Challenge.HALF_HEART, new ItemCode('\uEE41', 1, "Todos los miembros deben permanecer a medio corazón."));
                challenges.put(Challenge.SHIELD_BREAK, new ItemCode('\uEE42', 2, "Rompe el escudo de un compañero."));
                challenges.put(Challenge.ARMOR_MATERIALS, new ItemCode('\uEE43', 3, "Un integrante debe llevar armadura completa de distinto material cada pieza."));
                challenges.put(Challenge.DOLPHIN_SWIM, new ItemCode('\uEE44', 4, "Nada con un delfín."));
                challenges.put(Challenge.MAXIMUM_HEIGHT, new ItemCode('\uEE45', 5, "Alcanza la altura máxima."));
                challenges.put(Challenge.NETHER_TREE, new ItemCode('\uEE46', 6, "Haz crecer un árbol en el nether."));
                challenges.put(Challenge.SHOOT_TARGET, new ItemCode('\uEE47', 7, "Dispara a un target block."));
                challenges.put(Challenge.HOSTILE_KILL, new ItemCode('\uEE48', 8, "Mata a 5 mobs hostiles diferentes."));
                challenges.put(Challenge.ANIMAL_KILL, new ItemCode('\uEE49', 9, "Mata a 5 animales diferentes."));
                challenges.put(Challenge.CAMPFIRE_CAMPING, new ItemCode('\uEE50', 10, "“Acampanding” Cocina en una hoguera con todo tu equipo cerca."));
                challenges.put(Challenge.PINK_SHEEP_BIOME, new ItemCode('\uEE51', 11, "Cada miembro del equipo debe matar una oveja rosa en un bioma diferente."));
                challenges.put(Challenge.BREAK_RULE_1, new ItemCode('\uEE52', 12, "Rompe la regla #1 de Minecraft."));
                challenges.put(Challenge.JUMP_BED, new ItemCode('\uEE53', 13, "Todos los miembros del equipo deben saltar encima de la misma cama al mismo tiempo."));
                challenges.put(Challenge.BONE_MEAL_COMPOSTER, new ItemCode('\uEE54', 14, "Consigue bone meal mediante un compostador."));
                challenges.put(Challenge.CREEPER_IGNITE, new ItemCode('\uEE55', 15, "Enciende un creeper con un mechero."));
                challenges.put(Challenge.DROWN_VILLAGER, new ItemCode('\uEE56', 16, "Observa como se ahoga un aldeano."));
                challenges.put(Challenge.REDSTONE_SIGNAL, new ItemCode('\uEE57', 17, "Activa 5 objetos diferentes que emitan una señal de redstone."));
                challenges.put(Challenge.PURPLE_LLAMA, new ItemCode('\uEE58', 18, "Monta una llama con una alfombra morada."));
                challenges.put(Challenge.EAT_FOOD, new ItemCode('\uEE59', 19, "Come 5 tipos de comida diferentes."));
                challenges.put(Challenge.COLOR_SHEEP, new ItemCode('\uEE60', 20, "Cambia el color de una oveja con 10 tipos de tinte distintos."));
                challenges.put(Challenge.LVL_40, new ItemCode('\uEE61', 21, "Todos los miembros deben sumar más de 40 niveles de experiencia."));
                challenges.put(Challenge.ANVIL_DAMAGE, new ItemCode('\uEE62', 22, "Cada miembro del equipo debe tomar daño de caída de yunque."));
                challenges.put(Challenge.ACUATIC_KILL, new ItemCode('\uEE63', 23, "Mata 5 mobs acuaticos diferentes."));
                challenges.put(Challenge.MINE_MINERALS, new ItemCode('\uEE64', 24, "Mina 5 minerales diferentes."));
                challenges.put(Challenge.GET_POISON, new ItemCode('\uEE65', 25, "Consigue el efecto de veneno."));
                
                challenges.put(Challenge.INVENTORY_STACKS, new ItemCode('\uEE66', 26, "Un integrante debe de llenar todo su inventario con stacks de 64 de cualquier item."));
	        challenges.put(Challenge.STRIDER_GAPPLE, new ItemCode('\uEE67', 27, "Come una manzana dorada mientras montas un strider."));
		challenges.put(Challenge.PIGLIN_BARTER, new ItemCode('\uEE68', 28, "Tradea con un piglin."));
		challenges.put(Challenge.COMPLETE_MAP, new ItemCode('\uEE69', 29, "Completa un mapa."));
		challenges.put(Challenge.VILLAGER_MAX_TRADE, new ItemCode('\uEE70', 30, "Tradea con un aldeano de nivel máximo."));
		challenges.put(Challenge.CROSSBOW_SHOT, new ItemCode('\uEE71', 31, "Dispara a través de 3 miembros de tu equipo con un solo disparo de ballesta."));
		challenges.put(Challenge.SHIELD_BANNER, new ItemCode('\uEE72', 32, "Todos los miembros del equipo deben de tener un escudo con el mismo banner."));
		challenges.put(Challenge.ENCHANT_LVL6, new ItemCode('\uEE73', 33, "Encanta un item a nivel 6."));
		challenges.put(Challenge.CAMPFIRE_HAY_BALE, new ItemCode('\uEE74', 34, "Extiende la señal de humo de un campfire con un bloque de heno."));
		challenges.put(Challenge.BED_EXPLODE, new ItemCode('\uEE75', 35, "Almost Intentional Game Design."));
		challenges.put(Challenge.FISH_ITEMS, new ItemCode('\uEE76', 36, "Pesca 5 items diferentes."));
		challenges.put(Challenge.GIVE_PLAYER_FLOWER, new ItemCode('\uEE77', 37, "Regala una flor a un jugador de otro equipo con click derecho sobre ellos."));
		challenges.put(Challenge.WATER_DROP, new ItemCode('\uEE78', 38, "“Salto de fe” Consigue un WaterDrop perfecto de más de 75 bloques de altura."));
		challenges.put(Challenge.EMPTY_CAULDRON, new ItemCode('\uEE79', 39, "Vacía un cauldron sin usar botellas ni cubetas."));
		challenges.put(Challenge.BREED_ANIMALS, new ItemCode('\uEE80', 40, "Aparea 5 tipos de animales diferentes."));
		challenges.put(Challenge.POTION_EFFECTS, new ItemCode('\uEE81', 41, "Consigue 5 efectos de poción al mismo tiempo."));
		challenges.put(Challenge.MILK_ZOMBIE, new ItemCode('\uEE82', 42, "Dale un cubo de leche a un zombie."));
		challenges.put(Challenge.POTION_TYPES, new ItemCode('\uEE83', 43, "Fabrica 3 tipos diferentes de pociones."));
		challenges.put(Challenge.EQUIP_DONKEY_CHEST, new ItemCode('\uEE84', 44, "Equipa un cofre a un burro."));
		challenges.put(Challenge.HUNGER_DAMAGE, new ItemCode('\uEE85', 45, "Recibe daño por hambre."));
		challenges.put(Challenge.HOGLIN_SCARE, new ItemCode('\uEE86', 46, "Asusta un hoglin usando un warped fungus."));
		challenges.put(Challenge.PIGLIN_SCARE, new ItemCode('\uEE87', 47, "Asusta un piglin usando una antorcha o linterna de alma."));
		challenges.put(Challenge.SHOOT_PORTAL, new ItemCode('\uEE88', 48, "Dispara a una entidad a través de un portal del nether"));
		challenges.put(Challenge.JARDINERO, new ItemCode('\uEE89', 49, "Coloca 5 flores diferentes en una maceta."));
		challenges.put(Challenge.RIDE_HORSE_MINECART, new ItemCode('\uEE90', 50, "Monta a un caballo que este montando un minecart."));
                                
		challenges.put(Challenge.CAKE_EAT, new ItemCode('\uEE91', 51, "“Feliz Cumpleaños a ti” Todos los miembros del equipo deben comer de un mismo pastel."));
		challenges.put(Challenge.END_LEAVE, new ItemCode('\uEE92', 52, "“De vuelta a casa” Regresa al overworld a través de un portal del end."));
		challenges.put(Challenge.PLAYER_FISH, new ItemCode('\uEE93', 53, "Pesca un jugador de otro bando."));
		challenges.put(Challenge.PIG_FALL, new ItemCode('\uEE94', 54, "“Cuando los cerdos vuelen” Guía a un cerdo a morir de caída"));
		challenges.put(Challenge.FULL_ARMOR, new ItemCode('\uEE95', 55, "“Equipados para todo” Todos los miembros deben llevar una armadura completa."));
		challenges.put(Challenge.FIREWORK_CROSSBOW, new ItemCode('\uEE96', 56, "“Se fue con un BANG” Todos los miembros deben lanzar una ballesta cargada con fuegos artificiales"));
		challenges.put(Challenge.CREEPER_TNT_KILL, new ItemCode('\uEE97', 57, "“Dulce Venganza” Mata a un creeper con tnt."));
		challenges.put(Challenge.NETHER_MOB_KILL, new ItemCode('\uEE98', 58, "Cada miembro del equipo debe matar un mob en un bioma del nether diferente."));
		challenges.put(Challenge.ENDER_EYE_THROW, new ItemCode('\uEE99', 59, "“Veo veo con mi ojo feo” Todos los jugadores tienen que lanzar un ojo de ender"));
		challenges.put(Challenge.FLYING_MOBS_KILL, new ItemCode('\uEF01', 60, "Mata 3 mobs voladores."));
		challenges.put(Challenge.LINGERING_WATER_POTION, new ItemCode('\uEF02', 61, "Lanza una lingering potion de agua."));
		challenges.put(Challenge.ENDER_PEARL_TRAVEL, new ItemCode('\uEF03', 62, "Viaja más de 300 bloques con una enderpearl"));
		challenges.put(Challenge.VILLAGER_EXPENSIVE_TRADE, new ItemCode('\uEF04', 63, "“ESTAFA” Tradea algo de más de 20 esmeraldas con un aldeano "));
		challenges.put(Challenge.EAT_SUS_STEW, new ItemCode('\uEF05', 64, "Come 5 tipos diferentes de suspicious stew."));
		challenges.put(Challenge.MINE_LIGHT_SOURCE, new ItemCode('\uEF06', 65, "Pica 10 bloques diferentes que den luz."));
		challenges.put(Challenge.GLOWING_BAT, new ItemCode('\uEF07', 66, "Dispárale una flecha con glowing a un murciélago."));
		challenges.put(Challenge.TEAM_SPAWN_ANCHOR, new ItemCode('\uEF08', 67, "Todos los miembros del equipo deben asignar su reaparición a un mismo respawn anchor."));
		challenges.put(Challenge.NOTEBLOCK_INSTRUMENTS, new ItemCode('\uEF09', 68, "Toca 5 instrumentos diferentes en un note block."));
		challenges.put(Challenge.DROWNED_MAP, new ItemCode('\uEF10', 69, "“Rey de los océanos” Dale un mapa a un drowned."));
		challenges.put(Challenge.TEAM_DANCE, new ItemCode('\uEF11', 70, "Reproduce un disco en un tocadiscos y baila con todo tu equipo."));
		challenges.put(Challenge.SNOWBALL_BLAZE_KILL, new ItemCode('\uEF12', 71, "Mata a un blaze usando bolas de nieve."));
		challenges.put(Challenge.OBTAIN_MULE, new ItemCode('\uEF13', 72, "Consigue una Mula."));
		challenges.put(Challenge.FARM_CROPS, new ItemCode('\uEF14', 73, "Crece 5 cultivos diferentes al máximo."));
		challenges.put(Challenge.END_BINGO_MAX_HEIGHT, new ItemCode('\uEF15', 74, "“En la Cima” Todos los integrantes deben de estar juntos y en la capa 256 después de completar todos los otros logros."));
		challenges.put(Challenge.OVER_ZOGLIN, new ItemCode('\uEF16', 75, "Mata un Zoglin."));
                
        }

        public void register_challenge_1(){
                var challenge_1 = challengeRounds.get(BingoRound.ONE);

                challenge_1.add(Challenge.HALF_HEART);
                challenge_1.add(Challenge.SHIELD_BREAK);
                challenge_1.add(Challenge.ARMOR_MATERIALS);
                challenge_1.add(Challenge.DOLPHIN_SWIM);
                challenge_1.add(Challenge.MAXIMUM_HEIGHT);
                challenge_1.add(Challenge.NETHER_TREE);
                challenge_1.add(Challenge.SHOOT_TARGET);
                challenge_1.add(Challenge.HOSTILE_KILL);
                challenge_1.add(Challenge.ANIMAL_KILL);
                challenge_1.add(Challenge.CAMPFIRE_CAMPING);
                challenge_1.add(Challenge.PINK_SHEEP_BIOME);
                challenge_1.add(Challenge.BREAK_RULE_1);
                challenge_1.add(Challenge.JUMP_BED);
                challenge_1.add(Challenge.BONE_MEAL_COMPOSTER);
                challenge_1.add(Challenge.CREEPER_IGNITE);
                challenge_1.add(Challenge.DROWN_VILLAGER);
                challenge_1.add(Challenge.REDSTONE_SIGNAL);
                challenge_1.add(Challenge.PURPLE_LLAMA);
                challenge_1.add(Challenge.EAT_FOOD);
                challenge_1.add(Challenge.COLOR_SHEEP);
                challenge_1.add(Challenge.LVL_40);
                challenge_1.add(Challenge.ANVIL_DAMAGE);
                challenge_1.add(Challenge.ACUATIC_KILL);
                challenge_1.add(Challenge.MINE_MINERALS);
                challenge_1.add(Challenge.GET_POISON);

        }

        public void register_challenge_2(){
                var challenge_2 = challengeRounds.get(BingoRound.TWO);

                challenge_2.add(Challenge.INVENTORY_STACKS);
                challenge_2.add(Challenge.STRIDER_GAPPLE);
                challenge_2.add(Challenge.PIGLIN_BARTER);
                challenge_2.add(Challenge.COMPLETE_MAP);
                challenge_2.add(Challenge.VILLAGER_MAX_TRADE);
                challenge_2.add(Challenge.CROSSBOW_SHOT);
                challenge_2.add(Challenge.SHIELD_BANNER);
                challenge_2.add(Challenge.ENCHANT_LVL6);
                challenge_2.add(Challenge.CAMPFIRE_HAY_BALE);
                challenge_2.add(Challenge.BED_EXPLODE);
                challenge_2.add(Challenge.FISH_ITEMS);
                challenge_2.add(Challenge.GIVE_PLAYER_FLOWER);
                challenge_2.add(Challenge.WATER_DROP);
                challenge_2.add(Challenge.EMPTY_CAULDRON);
                challenge_2.add(Challenge.BREED_ANIMALS);
                challenge_2.add(Challenge.POTION_EFFECTS);
                challenge_2.add(Challenge.MILK_ZOMBIE);
                challenge_2.add(Challenge.POTION_TYPES);
                challenge_2.add(Challenge.EQUIP_DONKEY_CHEST);
                challenge_2.add(Challenge.HUNGER_DAMAGE);
                challenge_2.add(Challenge.HOGLIN_SCARE);
                challenge_2.add(Challenge.PIGLIN_SCARE);
                challenge_2.add(Challenge.SHOOT_PORTAL);
                challenge_2.add(Challenge.RIDE_HORSE_MINECART);
                challenge_2.add(Challenge.JARDINERO);

        }

        public void register_challenge_3(){
                var challenge_3 = challengeRounds.get(BingoRound.THREE);

                challenge_3.add(Challenge.CAKE_EAT);
                challenge_3.add(Challenge.END_LEAVE);
                challenge_3.add(Challenge.PLAYER_FISH);
                challenge_3.add(Challenge.PIG_FALL);
                challenge_3.add(Challenge.FULL_ARMOR);
                challenge_3.add(Challenge.FIREWORK_CROSSBOW);
                challenge_3.add(Challenge.CREEPER_TNT_KILL);
                challenge_3.add(Challenge.NETHER_MOB_KILL);
                challenge_3.add(Challenge.ENDER_EYE_THROW);
                challenge_3.add(Challenge.FLYING_MOBS_KILL);
                challenge_3.add(Challenge.LINGERING_WATER_POTION);
                challenge_3.add(Challenge.ENDER_PEARL_TRAVEL);
                challenge_3.add(Challenge.VILLAGER_EXPENSIVE_TRADE);
                challenge_3.add(Challenge.EAT_SUS_STEW);
                challenge_3.add(Challenge.MINE_LIGHT_SOURCE);
                challenge_3.add(Challenge.GLOWING_BAT);
                challenge_3.add(Challenge.TEAM_SPAWN_ANCHOR);
                challenge_3.add(Challenge.NOTEBLOCK_INSTRUMENTS);
                challenge_3.add(Challenge.DROWNED_MAP);
                challenge_3.add(Challenge.TEAM_DANCE);
                challenge_3.add(Challenge.SNOWBALL_BLAZE_KILL);
                challenge_3.add(Challenge.OBTAIN_MULE);
                challenge_3.add(Challenge.FARM_CROPS);
                challenge_3.add(Challenge.END_BINGO_MAX_HEIGHT);
                challenge_3.add(Challenge.OVER_ZOGLIN);

        }


}