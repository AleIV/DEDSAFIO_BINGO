package me.aleiv.core.paper.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Data;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.BingoType;
import me.aleiv.core.paper.Game.Challenge;
import me.aleiv.core.paper.Game.GameStage;
import me.aleiv.core.paper.events.BingoEvent;
import me.aleiv.core.paper.events.FoundItemEvent;
import me.aleiv.core.paper.events.GameStartedEvent;
import me.aleiv.core.paper.game.objects.ChallengeSlot;
import me.aleiv.core.paper.game.objects.Slot;
import me.aleiv.core.paper.game.objects.Table;
import me.aleiv.core.paper.utilities.FastBoard;
import me.aleiv.core.paper.utilities.Frames;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;
import net.md_5.bungee.api.ChatColor;

@Data
public class BingoManager implements Listener {

    Core instance;

    public static int respawnSeconds = 10;
    Random random = new Random();

    boolean blankTab = false;

    List<UUID> topLine = new ArrayList<>();
    List<UUID> topFull = new ArrayList<>();

    public BingoManager(Core instance) {
        this.instance = instance;

        instance.getProtocolManager().addPacketListener(
                new PacketAdapter(instance, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        if (blankTab && event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
                            var packet = event.getPacket();
                            // Reference https://wiki.vg/Protocol#Player_Info
                            var action = packet.getPlayerInfoAction().read(0);
                            if (action.compareTo(PlayerInfoAction.ADD_PLAYER) == 0) {

                                event.setCancelled(true);

                            }
                        }
                    }
                });
    }

    public void addChallenge(Table table, Challenge challenge, Player player) {
        var game = instance.getGame();
        var board = table.getBoard();
        var selectedChallenges = table.getSelectedChallenge();
        var boards = game.getBoards();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                var slot = board[i][j];
                if (!slot.isFound() && slot instanceof ChallengeSlot slotC && slotC.getChallenge() == challenge) {
                    slot.setFound(true);

                    selectedChallenges.add(challenge);
                    var score = boards.get(player.getUniqueId().toString());
                    instance.getBingoManager().updateBoard(score, table);

                    instance.getBingoManager().checkBingo(table, slot, player);
                    return;
                }
            }
        }
    }

    public void removeChallenge(Table table, Challenge challenge, Player player) {
        var uuid = player.getUniqueId().toString();
        var game = instance.getGame();
        var board = table.getBoard();
        var boards = game.getBoards();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                var slot = board[i][j];
                if (slot.isFound() && slot instanceof ChallengeSlot slotC && slotC.getChallenge() == challenge) {
                    slot.setFound(false);

                    var score = boards.get(uuid);
                    instance.getBingoManager().updateBoard(score, table);

                    return;
                }
            }
        }
    }

    public void addItem(Table table, Material material, Player player) {
        var uuid = player.getUniqueId().toString();
        var game = instance.getGame();
        var board = table.getBoard();
        var selectedItems = table.getSelectedItems();
        var boards = game.getBoards();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                var slot = board[i][j];
                if (!slot.isFound() && slot.getItem().getType() == material) {
                    slot.setFound(true);

                    selectedItems.add(material);
                    var score = boards.get(uuid);
                    instance.getBingoManager().updateBoard(score, table);

                    instance.getBingoManager().checkBingo(table, slot, player);
                    return;
                }
            }
        }
    }

    public void removeItem(Table table, Material material, Player player) {
        var uuid = player.getUniqueId().toString();
        var game = instance.getGame();
        var board = table.getBoard();
        var boards = game.getBoards();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                var slot = board[i][j];
                if (!slot.isFound() && slot.getItem().getType() == material) {
                    slot.setFound(false);

                    var score = boards.get(uuid);
                    instance.getBingoManager().updateBoard(score, table);

                    return;
                }
            }
        }
    }

    public void attempToFind(Player player, ItemStack item) {
        var uuid = player.getUniqueId().toString();
        var game = instance.getGame();

        var manager = instance.getBingoManager();

        var table = manager.findTable(player.getUniqueId());

        if (table != null) {
            var board = table.getBoard();
            var selectedItems = table.getSelectedItems();

            if (item != null && game.getGameStage() == GameStage.INGAME && selectedItems.contains(item.getType())) {

                var boards = game.getBoards();

                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        var slot = board[i][j];
                        if (!slot.isFound() && slot.getItem().getType() == item.getType()) {
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

    public void attempToFind(Player player, Challenge challenge, String info) {
        var uuid = player.getUniqueId().toString();
        var game = instance.getGame();

        var manager = instance.getBingoManager();

        var table = manager.findTable(player.getUniqueId());

        if (table != null) {
            var board = table.getBoard();
            var selectedChallenges = table.getSelectedChallenge();

            if (game.getGameStage() == GameStage.INGAME && selectedChallenges.contains(challenge)) {

                var boards = game.getBoards();
                var playerName = player.getName();

                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        var slot = board[i][j];
                        if (!slot.isFound() && slot instanceof ChallengeSlot slotC
                                && slotC.getChallenge() == challenge) {

                            var challengeInfo = slotC.getChallengeInfo();
                            var infoPlayers = slotC.getInfoPlayers();

                            switch (challenge) {
                                // 5 diff player case
                                case FIREWORK_CROSSBOW:
                                case JUMP_BED: {

                                    if (infoPlayers.contains(playerName)) {
                                        return;

                                    } else {
                                        infoPlayers.add(playerName);
                                    }

                                    if (infoPlayers.size() < table.getMembers().size()) {
                                        return;
                                    }

                                }
                                    break;

                                case NOTEBLOCK_INSTRUMENTS:
                                case FARM_CROPS:
                                case EAT_SUS_STEW:
                                case JARDINERO:
                                case BREED_ANIMALS:
                                case MINE_LIGHT_SOURCE:
                                case ACUATIC_KILL:
                                case FISH_ITEMS:
                                case POTION_TYPES:
                                case FLYING_MOBS_KILL: {

                                    if (challengeInfo.contains(info)) {
                                        return;

                                    } else {
                                        challengeInfo.add(info);
                                    }

                                    if (challengeInfo.size() < 3) {
                                        return;
                                    }

                                }
                                    break;

                                case MINE_MINERALS:
                                case ANIMAL_KILL:
                                case HOSTILE_KILL:
                                case CAULDRON_WASH: {

                                    if (challengeInfo.contains(info)) {
                                        return;

                                    } else {
                                        challengeInfo.add(info);
                                    }

                                    if (challengeInfo.size() < 4) {
                                        return;
                                    }

                                }
                                    break;
                                // 5 diff info case

                                case COLOR_SHEEP:
                                case EAT_FOOD:
                                case REDSTONE_SIGNAL: {

                                    if (challengeInfo.contains(info)) {
                                        return;

                                    } else {
                                        challengeInfo.add(info);
                                    }

                                    if (challengeInfo.size() < 5) {
                                        return;
                                    }

                                }
                                    break;

                                case NETHER_MOB_KILL:
                                case PINK_SHEEP_BIOME: {

                                    if (challengeInfo.contains(info) || infoPlayers.contains(playerName)) {
                                        return;

                                    } else {
                                        challengeInfo.add(info);
                                        infoPlayers.add(playerName);
                                    }

                                    if (challengeInfo.size() < table.getMembers().size()
                                            || infoPlayers.size() < table.getMembers().size()) {
                                        return;
                                    }

                                }
                                    break;

                                case BREAK_RULE_1: {
                                    if (challengeInfo.size() == 0) {
                                        challengeInfo.add(info);
                                        return;
                                    }
                                    int[] lastCoords = Arrays
                                            .stream(challengeInfo.get(challengeInfo.size() - 1).split(";"))
                                            .mapToInt(Integer::parseInt).toArray();
                                    int[] currentCoords = Arrays.stream(info.split(";")).mapToInt(Integer::parseInt)
                                            .toArray();
                                    if (currentCoords[0] != lastCoords[0] || currentCoords[2] != lastCoords[2]
                                            || currentCoords[1] >= lastCoords[1]) {
                                        challengeInfo.clear();
                                        challengeInfo.add(info);
                                        return;
                                    }
                                    challengeInfo.add(info);
                                    if (challengeInfo.size() < 20)
                                        return;
                                }
                                    break;

                                case ANVIL_DAMAGE: {
                                    if (infoPlayers.contains(playerName))
                                        return;
                                    infoPlayers.add(playerName);
                                    if (infoPlayers.size() < table.getMembers().size())
                                        return;
                                }
                                    break;

                                case TEAM_SPAWN_ANCHOR:
                                case CAKE_EAT: {
                                    if (challengeInfo.size() == 0) {
                                        challengeInfo.add(info);
                                    }
                                    int[] lastCoords = Arrays
                                            .stream(challengeInfo.get(challengeInfo.size() - 1).split(";"))
                                            .mapToInt(Integer::parseInt).toArray();
                                    int[] currentCoords = Arrays.stream(info.split(";")).mapToInt(Integer::parseInt)
                                            .toArray();
                                    boolean validCoords = true;
                                    for (int index = 0; index < 3; index++) {
                                        if (index < lastCoords.length && (lastCoords[index] != currentCoords[index])) {
                                            validCoords = false;
                                            break;
                                        }
                                    }
                                    if (!validCoords) {
                                        challengeInfo.clear();
                                        challengeInfo.add(info);
                                        infoPlayers.clear();
                                    } else {
                                        if (infoPlayers.contains(playerName))
                                            return;
                                        challengeInfo.add(info);
                                    }
                                    infoPlayers.add(playerName);
                                    if (infoPlayers.size() < table.getMembers().size())
                                        return;
                                }
                                    break;

                                default:
                                    break;
                            }

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

    public void startGame() {
        var game = instance.getGame();

        var task = new BukkitTCT();

        var countDownStart = Character.toString('\uE360');
        var countdown = Frames.getFramesCharsIntegers(361, 489);

        Bukkit.getOnlinePlayers().forEach(player -> {
            var loc = player.getLocation();
            instance.sendActionBar(player, countDownStart);
            player.playSound(loc, "bingo.countdown", 1, 1);
            game.setGameStage(GameStage.STARTING);
        });

        countdown.forEach(frame -> {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        instance.sendActionBar(player, frame + "");
                    });
                }

            }, 100);
        });

        var scatter = instance.getScatterManager();
        task.addWithDelay(new BukkitRunnable() {
            @Override
            public void run() {
                // SCATTER

                scatter.runKernelScatter();
                
            }

        }, 50);

        task.execute();

    }

    public void totalStart(){
        var game = instance.getGame();
        var round = game.getBingoRound();

        var task = new BukkitTCT();

        var countDownStart = Character.toString('\uE360');
        var countdown = Frames.getFramesCharsIntegers(360, 489);

        Bukkit.getOnlinePlayers().forEach(player -> {
            var loc = player.getLocation();
            instance.sendActionBar(player, countDownStart);
            player.playSound(loc, "bingo.countdown", 1, 1);
        });

        countdown.forEach(frame -> {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        instance.sendActionBar(player, frame + "");
                    });
                }

            }, 100);
        });

        task.addWithDelay(new BukkitRunnable() {
            @Override
            public void run() {
        
                var timer = game.getTimer();
                var time = 0;
        
                switch (round) {
                    case ONE:{
                        time = 1200;
        
                    }break;
                    case TWO:{
                        time = 1800;
        
                    }break;
                    case THREE:{
                        time = 3600;
        
                    }break;
                
                    default:
                        break;
                }
        
                final var v = time;
                timer.setPreStart(time);
        
                Bukkit.getScheduler().runTaskLater(instance, task->{
                    timer.start(v, (int) game.getGameTime());
                }, 20*5);
        
                Bukkit.getPluginManager().callEvent(new GameStartedEvent());
                
            }

        }, 50);

        task.execute();

    }

    public void selectTables() {
        var game = instance.getGame();
        game.getTables().clear();

        var teamsOnline = instance.getTeamManager().getTeamsOnlineList();

        teamsOnline.forEach(team -> {
            var table = new Table(team.getTeamID(), team.getMembers());
            game.getTables().add(table);
            table.selectItems(instance);
        });
    }

    public void restartGame() {
        var game = instance.getGame();

        game.setGameStage(GameStage.LOBBY);

        var world = Bukkit.getWorld("lobby");
        var loc = new Location(world, 0.5, 126, 0.5, 90, -0);

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.teleport(loc);
        });

    }

    public Table findTable(UUID player) {
        var game = instance.getGame();
        var tables = game.getTables();

        return tables.stream().filter(table -> table.isPlaying(player)).findAny().orElse(null);

    }

    public void checkBingo(Table table, Slot slot, Player player) {
        var teamManager = instance.getTeamManager();
        Bukkit.getScheduler().runTaskAsynchronously(instance, task -> {

            var found = new FoundItemEvent(table, slot, player, true);
            Bukkit.getPluginManager().callEvent(found);
            table.addItemFound();
            table.addPoints(1, "point");

            if (!table.isFoundFull() && table.isBingoFull()) {

                Bukkit.getPluginManager().callEvent(new BingoEvent(found, BingoType.FULL, true));
                table.setFoundFull(true);
                table.playFullFoundAnimation();
                var team = teamManager.getPlayerTeam(player.getUniqueId());
                if (topFull.size() < 1) {
                    table.addPoints(5, "points! 1st place BINGO FULL!");
                    topFull.add(table.getUuid());

                    if (team != null)
                        instance.broadcastMessage(
                                ChatColor.of("#e04d4d") + "Team " + team.getTeamName() + " 1st place BINGO FULL!");

                } else if (topFull.size() < 2) {
                    table.addPoints(3, "points! 2nd place BINGO FULL!");
                    topFull.add(table.getUuid());

                    if (team != null)
                        instance.broadcastMessage(
                                ChatColor.of("#e04d4d") + "Team " + team.getTeamName() + " 2nd place BINGO FULL!");

                } else if (topFull.size() < 3) {
                    table.addPoints(1, "points! 3rd place BINGO FULL!");
                    topFull.add(table.getUuid());

                    if (team != null)
                        instance.broadcastMessage(
                                ChatColor.of("#e04d4d") + "Team " + team.getTeamName() + " 3rd place BINGO FULL!");

                }
                table.addPoints(5, "points");

            } else if (!table.isFoundLine() && table.isBingoLine()) {
                Bukkit.getPluginManager().callEvent(new BingoEvent(found, BingoType.LINE, true));
                table.setFoundLine(true);
                table.playLineFoundAnimation();

                var team = teamManager.getPlayerTeam(player.getUniqueId());
                if (topLine.size() < 1) {
                    table.addPoints(5, "points! 1st place BINGO LINE!");
                    topLine.add(table.getUuid());

                    if (team != null)
                        instance.broadcastMessage(
                                ChatColor.of("#e04d4d") + "Team " + team.getTeamName() + " 1st place BINGO LINE!");

                } else if (topLine.size() < 2) {
                    table.addPoints(3, "points! 2nd place BINGO LINE!");
                    topLine.add(table.getUuid());

                    if (team != null)
                        instance.broadcastMessage(
                                ChatColor.of("#e04d4d") + "Team " + team.getTeamName() + " 2nd place BINGO LINE!");

                } else if (topLine.size() < 3) {
                    table.addPoints(1, "point! 3rd place BINGO LINE!");
                    topLine.add(table.getUuid());

                    if (team != null)
                        instance.broadcastMessage(
                                ChatColor.of("#e04d4d") + "Team " + team.getTeamName() + " 3rd place BINGO LINE!");

                }
                table.addPoints(5, "points");

            } else {
                table.playItemFoundAnimation();
            }

        });

    }

    public void updateBoard(FastBoard board, Table table) {

        if (table == null) {
            var fake = Slot.getFakeDisplay();
            board.updateTitle(Table.getNullTitle());
            board.updateLines("", "" + fake + fake + fake + fake + fake, "", "" + fake + fake + fake + fake + fake, "",
                    "" + fake + fake + fake + fake + fake, "", "" + fake + fake + fake + fake + fake, "",
                    "" + fake + fake + fake + fake + fake, "");

        } else {
            board.updateTitle(table.getTitle());
            board.updateLines("",
                    "" + table.getPosDisplay(0, 0) + table.getPosDisplay(0, 1) + table.getPosDisplay(0, 2)
                            + table.getPosDisplay(0, 3) + table.getPosDisplay(0, 4),
                    "",
                    "" + table.getPosDisplay(1, 0) + table.getPosDisplay(1, 1) + table.getPosDisplay(1, 2)
                            + table.getPosDisplay(1, 3) + table.getPosDisplay(1, 4),
                    "",
                    "" + table.getPosDisplay(2, 0) + table.getPosDisplay(2, 1) + table.getPosDisplay(2, 2)
                            + table.getPosDisplay(2, 3) + table.getPosDisplay(2, 4),
                    "",
                    "" + table.getPosDisplay(3, 0) + table.getPosDisplay(3, 1) + table.getPosDisplay(3, 2)
                            + table.getPosDisplay(3, 3) + table.getPosDisplay(3, 4),
                    "", "" + table.getPosDisplay(4, 0) + table.getPosDisplay(4, 1) + table.getPosDisplay(4, 2)
                            + table.getPosDisplay(4, 3) + table.getPosDisplay(4, 4),
                    "");
        }

    }

}