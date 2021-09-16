package me.aleiv.core.paper.game;

import java.util.Random;
import java.util.UUID;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

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
import net.md_5.bungee.api.ChatColor;

@Data
public class BingoManager implements Listener {

    Core instance;

    Table globalTable;
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

    public void attempToFind(Player player, Challenge challenge) {
        var uuid = player.getUniqueId().toString();
        var game = instance.getGame();

        var manager = instance.getBingoManager();

        var table = manager.findTable(player.getUniqueId());

        if (table != null) {
            var board = table.getBoard();
            var selectedChallenges = table.getSelectedChallenge();

            if (game.getGameStage() == GameStage.INGAME && selectedChallenges.contains(challenge)) {

                var boards = game.getBoards();

                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        var slot = board[i][j];
                        if (!slot.isFound() && slot instanceof ChallengeSlot slotC && slotC.getChallenge() == challenge) {
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

        game.setGameStage(GameStage.STARTING);
        instance.broadcastMessage(ChatColor.of(game.getColor1()) + "Game starting...");

        game.setGameStage(GameStage.INGAME);

        var loc = Bukkit.getWorlds().get(0).getSpawnLocation();

        game.getTables().clear();

        var teamManager = instance.getTeamManager();
        var scatterManager = instance.getScatterManager();

        if (!teamManager.isTeams()) {
            // FFA CASE

            Bukkit.getOnlinePlayers().forEach(p -> {
                var player = (Player) p;
                var table = new Table();
                game.getTables().add(table);
                table.selectItems(instance);
                table.getMembers().add(player.getUniqueId());

            });

            scatterManager.runScatter();

        } else {
            // TEAM CASE

            teamManager.getTeamMap().values().forEach(team -> {

                var table = new Table();
                game.getTables().add(table);
                table.selectItems(instance);

                team.getMembers().forEach(member -> {
                    table.getMembers().add(member);
                    var player = Bukkit.getPlayer(member);
                    if (player != null) {
                        player.teleport(loc);
                    }
                });

            });
        }

        Bukkit.getPluginManager().callEvent(new GameStartedEvent());

    }

    public void restartGame() {
        var game = instance.getGame();

        game.setGameStage(GameStage.POSTGAME);
        instance.broadcastMessage(ChatColor.of(game.getColor1()) + "Game restarting...");

        game.setGameStage(GameStage.LOBBY);
        instance.broadcastMessage(ChatColor.of(game.getColor1()) + "Game restarted.");

        var world = Bukkit.getWorld("lobby");
        var loc = new Location(world, 71.5, 126, 0.5, 90, -0);

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.teleport(loc);
            var table = findTable(player.getUniqueId());
            if (table != null) {
                instance.broadcastMessage(ChatColor.DARK_RED + player.getName() + ChatColor.GOLD + " POINTS: "
                        + (table.getObjectsFound()));
            }

        });

    }

    public Table findTable(UUID player) {
        var game = instance.getGame();
        var tables = game.getTables();

        return tables.stream().filter(table -> table.isPlaying(player)).findAny().orElse(null);

    }

    public void checkBingo(Table table, Slot slot, Player player) {

        Bukkit.getScheduler().runTaskAsynchronously(instance, task -> {
            // var multiplier = Team.mutiplier;
            var found = new FoundItemEvent(table, slot, player, true);
            Bukkit.getPluginManager().callEvent(found);
            table.addItemFound();
            // table.addPoints(1*multiplier);

            if (!table.isFoundFull() && table.isBingoFull()) {
                Bukkit.getPluginManager().callEvent(new BingoEvent(found, BingoType.FULL, true));
                table.setFoundFull(true);
                // table.addPoints(10*multiplier);

            } else if (!table.isFoundLine() && table.isBingoLine()) {
                Bukkit.getPluginManager().callEvent(new BingoEvent(found, BingoType.LINE, true));
                table.setFoundLine(true);
                // table.addPoints(5*multiplier);
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