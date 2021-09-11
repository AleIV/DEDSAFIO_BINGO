package me.aleiv.core.paper.game;

import java.util.ArrayList;
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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import lombok.Data;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.BingoDifficulty;
import me.aleiv.core.paper.Game.BingoType;
import me.aleiv.core.paper.Game.GameStage;
import me.aleiv.core.paper.events.BingoEvent;
import me.aleiv.core.paper.events.FoundItemEvent;
import me.aleiv.core.paper.events.GameStartedEvent;
import me.aleiv.core.paper.teams.objects.Team;
import me.aleiv.core.paper.utilities.FastBoard;
import net.md_5.bungee.api.ChatColor;

@Data
public class BingoManager implements Listener {

    Core instance;

    Table globalTable;
    Random random = new Random();

    boolean blankTab = true;

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

    public Table findTable(UUID player) {
        var game = instance.getGame();
        var tables = game.getTables();

        return tables.stream().filter(table -> table.isPlaying(player)).findAny().orElse(null);

    }

    public int getRand(List<?> list) {
        return random.nextInt(list.size());
    }

    public void selectItems(Table table) {

        // var options =
        // instance.getGame().getMaterials().keySet().stream().collect(Collectors.toList());
        /*
         * for (int i = 0; i < 5; i++) { var str = new StringBuilder(); for (int j = 0;
         * j < 5; j++) {
         * 
         * var material = options.get(random.nextInt(options.size()));
         * 
         * options.remove(material);
         * 
         * table.getBoard()[i][j] = new Slot(instance, material);
         * table.getSelectedItems().add(material); str.append(ChatColor.GREEN +
         * material.toString() + ChatColor.YELLOW + "-");
         * 
         * }
         * 
         * instance.broadcastMessage(str.toString()); }
         */

        var game = instance.getGame();
        var difficulty = game.getBingoDifficulty();

        List<List<Material>> diff = new ArrayList<>();

        table.getSelectedItems().clear();

        var currentDiff = difficulty;

        switch (currentDiff) {
            case EASY: diff = game.getClassEasy().stream().collect(Collectors.toList()); break;
            case MEDIUM: diff = game.getClassMedium().stream().collect(Collectors.toList()); break;
            case HARD: diff = game.getClassHard().stream().collect(Collectors.toList()); break;
            case EXPERT: diff = game.getClassExpert().stream().collect(Collectors.toList()); break;
            default: break;
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {

                var diffRand = getRand(diff);
                var options = diff.get(diffRand);
                diff.remove(diffRand);

                var material = options.get(getRand(options));

                table.getBoard()[i][j] = new Slot(material);
                table.getSelectedItems().add(material);

                if(diff.isEmpty()){
                    switch (currentDiff) {
                        case MEDIUM: {
                            currentDiff = BingoDifficulty.EASY;
                            diff = game.getClassEasy().stream().collect(Collectors.toList());
                        } break;
                        case HARD: {
                            currentDiff = BingoDifficulty.MEDIUM;
                            diff = game.getClassMedium().stream().collect(Collectors.toList());
                        } break;
                        case EXPERT: {
                            currentDiff = BingoDifficulty.HARD;
                            diff = game.getClassHard().stream().collect(Collectors.toList());
                        } break;

                        default: 
                            break;
                    }
                }

            }
        }

    }

    public void restartGame() {
        var game = instance.getGame();

        game.setGameStage(GameStage.POSTGAME);
        instance.broadcastMessage(ChatColor.of(game.getColor1()) + "Game restarting...");

        game.setGameStage(GameStage.LOBBY);
        instance.broadcastMessage(ChatColor.of(game.getColor1()) + "Game restarted.");

        game.getTables().clear();
    }

    public void checkBingo(Table table, Slot slot, Player player) {

        Bukkit.getScheduler().runTaskAsynchronously(instance, task -> {
            var multiplier = Team.mutiplier;
            var found = new FoundItemEvent(table, slot, player, true);
            Bukkit.getPluginManager().callEvent(found);
            //table.addPoints(1*multiplier);

            if (table.isBingoFull() && !table.isFoundFull()) {
                Bukkit.getPluginManager().callEvent(new BingoEvent(found, BingoType.FULL, true));
                table.setFoundFull(true);
                //table.addPoints(10*multiplier);

            } else if (table.isBingoLine() && !table.isFoundLine()) {
                Bukkit.getPluginManager().callEvent(new BingoEvent(found, BingoType.LINE, true));
                table.setFoundLine(true);
                //table.addPoints(5*multiplier);
            }

        });

    }

    public void startGame() {
        var game = instance.getGame();

        game.setGameStage(GameStage.STARTING);
        instance.broadcastMessage(ChatColor.of(game.getColor1()) + "Game starting...");

        game.setGameStage(GameStage.INGAME);

        var loc = Bukkit.getWorlds().get(0).getSpawnLocation();

        var teamManager = instance.getTeamManager();
        if (teamManager.getTeamSize() == 1) {
            // FFA CASE

            Bukkit.getOnlinePlayers().forEach(p -> {
                var player = (Player) p;
                var table = new Table();
                game.getTables().add(table);
                instance.getBingoManager().selectItems(table);
                table.getMembers().add(player.getUniqueId());

                player.teleport(loc);

            });

        } else {
            // TEAM CASE

            teamManager.getTeamMap().values().forEach(team -> {

                var table = new Table();
                game.getTables().add(table);
                instance.getBingoManager().selectItems(table);

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

    public String getTitle() {
        var neg2 = instance.getNegativeSpaces().get(instance.getGame().getNeg2());
        var neg3 = instance.getNegativeSpaces().get(instance.getGame().getNeg3());
        String t = Character.toString('\uE001') + neg2 + Character.toString('\uE000') + neg3;

        return t;
    }

    public void updateBoard(FastBoard board, Table table) {

        board.updateTitle(getTitle());
        var neg = instance.getNegativeSpaces().get(instance.getGame().getNeg());

        if (table == null) {
            board.updateLines("", "WAITING BOARD");

        } else {
            board.updateLines("",
                    "" + table.getPosDisplay(0, 0) + table.getPosDisplay(0, 1) + table.getPosDisplay(0, 2)
                            + table.getPosDisplay(0, 3) + table.getPosDisplay(0, 4) + neg,
                    "",
                    "" + table.getPosDisplay(1, 0) + table.getPosDisplay(1, 1) + table.getPosDisplay(1, 2)
                            + table.getPosDisplay(1, 3) + table.getPosDisplay(1, 4) + neg,
                    "",
                    "" + table.getPosDisplay(2, 0) + table.getPosDisplay(2, 1) + table.getPosDisplay(2, 2)
                            + table.getPosDisplay(2, 3) + table.getPosDisplay(2, 4) + neg,
                    "",
                    "" + table.getPosDisplay(3, 0) + table.getPosDisplay(3, 1) + table.getPosDisplay(3, 2)
                            + table.getPosDisplay(3, 3) + table.getPosDisplay(3, 4) + neg,
                    "", "" + table.getPosDisplay(4, 0) + table.getPosDisplay(4, 1) + table.getPosDisplay(4, 2)
                            + table.getPosDisplay(4, 3) + table.getPosDisplay(4, 4) + neg,
                    "");
        }

    }

}