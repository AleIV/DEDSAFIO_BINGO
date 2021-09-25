package me.aleiv.core.paper.game;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

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
                                case COLOR_SHEEP:
                                case EAT_FOOD: {

                                    if (challengeInfo.contains(info)) {
                                        return;

                                    } else {
                                        challengeInfo.add(info);
                                    }

                                    if (challengeInfo.size() < 10) {
                                        return;
                                    }

                                } break;

                                case ACUATIC_KILL:
                                case MINE_MINERALS:
                                case ANIMAL_KILL:
                                case HOSTILE_KILL: {

                                    if (challengeInfo.contains(info)) {
                                        return;

                                    } else {
                                        challengeInfo.add(info);
                                    }

                                    if (challengeInfo.size() < 5) {
                                        return;
                                    }

                                } break;

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

                                } break;

                                case BREAK_RULE_1: {
                                    if (challengeInfo.size() == 0) {
                                        challengeInfo.add(info);
                                        return;
                                    }
                                    int[] lastCoords = Arrays.stream(challengeInfo.get(challengeInfo.size() - 1).split(";"))
                                            .mapToInt(Integer::parseInt).toArray();
                                    int[] currentCoords = Arrays.stream(info.split(";"))
                                            .mapToInt(Integer::parseInt).toArray();
                                    if (currentCoords[0] != lastCoords[0] || currentCoords[2] != lastCoords[2] ||
                                        currentCoords[1] >= lastCoords[1]) {
                                        challengeInfo.clear();
                                        challengeInfo.add(info);
                                        return;
                                    }
                                    challengeInfo.add(info);
                                    if (challengeInfo.size() < 30) return;
                                } break;

                                case ANVIL_DAMAGE: {
                                    if (infoPlayers.contains(playerName)) return;
                                    infoPlayers.add(playerName);
                                    if (infoPlayers.size() < table.getMembers().size()) return;
                                } break;

                                case CROSSBOW_SHOT: {
                                    List<String> previewsPlayers = challengeInfo.stream()
                                            .map(s -> s.split(";")[1])
                                            .collect(Collectors.toList());
                                    String[] lastData = challengeInfo.get(challengeInfo.size() - 1).split(";");
                                    String[] newData = info.split(";");
                                    if (!lastData[0].equals(newData[0]) || previewsPlayers.contains(newData[1]) ||
                                        !lastData[2].equals(newData[2])) {
                                        challengeInfo.clear();
                                        challengeInfo.add(info);
                                        return;
                                    }
                                    challengeInfo.add(info);
                                    if (challengeInfo.size() < 3) return;
                                } break;

                                default: break;
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

        task.addWithDelay(new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    var loc = player.getLocation();
                    instance.sendActionBar(player, countDownStart);
                    player.playSound(loc, "bingo.countdown", 1, 1);
                    game.setGameStage(GameStage.STARTING);
                });

            }

        }, 50);

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

        Bukkit.getOnlinePlayers().forEach(player -> {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    // SCATTER
                    var scatter = instance.getScatterManager();
                    var safeLocations = scatter.getSafeLocations();

                    // TODO: ADD TEAM SUPPORT
                    Location loc;
                    if (safeLocations.size() == 0) {
                        loc = scatter.generateLocation();

                    } else {
                        loc = safeLocations.get(0);
                        safeLocations.remove(0);
                    }

                    scatter.Qteleport(player, loc);
                }

            }, 50);
        });

        task.addWithDelay(new BukkitRunnable() {
            @Override
            public void run() {
                // DEFINITIVE START
                game.setGameStage(GameStage.INGAME);

                Bukkit.getPluginManager().callEvent(new GameStartedEvent());

            }

        }, 4000);

        task.execute();

    }

    public void selectTables() {
        var game = instance.getGame();
        game.getTables().clear();

        // TODO: ADD TEAM SUPPORT

        Bukkit.getOnlinePlayers().forEach(p -> {
            var player = (Player) p;
            var table = new Table();
            game.getTables().add(table);
            table.selectItems(instance);
            table.getMembers().add(player.getUniqueId());

        });
    }

    public void restartGame() {
        var game = instance.getGame();

        game.setGameStage(GameStage.POSTGAME);
        instance.broadcastMessage(ChatColor.of(game.getColor3()) + "TIME IS UP!");

        var manager = instance.getScatterManager();
        Bukkit.getScheduler().runTaskLater(instance, task -> {

            game.setGameStage(GameStage.LOBBY);

            var world = Bukkit.getWorld("lobby");
            var loc = new Location(world, 0.5, 126, 0.5, 90, -0);

            Bukkit.getOnlinePlayers().forEach(player -> {
                manager.Qteleport(player, loc);
            });

        }, 20 * 5);

    }

    public Table findTable(UUID player) {
        var game = instance.getGame();
        var tables = game.getTables();

        return tables.stream().filter(table -> table.isPlaying(player)).findAny().orElse(null);

    }

    public void checkBingo(Table table, Slot slot, Player player) {

        Bukkit.getScheduler().runTaskAsynchronously(instance, task -> {

            var found = new FoundItemEvent(table, slot, player, true);
            Bukkit.getPluginManager().callEvent(found);
            table.addItemFound();

            if (!table.isFoundFull() && table.isBingoFull()) {
                Bukkit.getPluginManager().callEvent(new BingoEvent(found, BingoType.FULL, true));
                table.setFoundFull(true);
                table.playFullFoundAnimation();

            } else if (!table.isFoundLine() && table.isBingoLine()) {
                Bukkit.getPluginManager().callEvent(new BingoEvent(found, BingoType.LINE, true));
                table.setFoundLine(true);
                table.playLineFoundAnimation();

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