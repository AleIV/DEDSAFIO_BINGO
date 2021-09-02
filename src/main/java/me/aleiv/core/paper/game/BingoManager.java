package me.aleiv.core.paper.game;

import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.GameStage;
import me.aleiv.core.paper.utilities.FastBoard;
import net.md_5.bungee.api.ChatColor;

public class BingoManager implements Listener {

    Core instance;

    Table globalTable;

    Random random = new Random();

    public BingoManager(Core instance) {
        this.instance = instance;
    }

    public void selectItems(Table table) {

        // TODO: TEST

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {

                var options = instance.getGame().getMaterials().keySet().stream().collect(Collectors.toList());

                var material = options.get(random.nextInt(options.size()));

                table.getBoard()[i][j] = new Slot(instance, material);
                table.getSelectedItems().add(material);

            }
        }

        /*

        var game = instance.getGame();
        var difficulty = game.getBingoDifficulty();

        List<List<Material>> diff = new ArrayList<>();

        table.getSelectedItems().clear();

        switch (difficulty) {

            case EASY: {
                diff = game.getClassEasy().stream().collect(Collectors.toList());
            }
                break;

            case MEDIUM: {
                diff = game.getClassMedium().stream().collect(Collectors.toList());
            }
                break;

            case HARD: {
                diff = game.getClassHard().stream().collect(Collectors.toList());
            }
                break;

            case EXPERT: {
                diff = game.getClassExpert().stream().collect(Collectors.toList());
            }
                break;

            default:
                break;
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {

                var options = diff.get(random.nextInt(diff.size()));
                diff.remove(options);

                var material = options.get(random.nextInt(options.size()));

                table.getBoard()[i][j] = new Slot(instance, material);
                table.getSelectedItems().add(material);

            }
        }*/

    }

    public void restartGame() {
        var game = instance.getGame();

        game.setGameStage(GameStage.POSTGAME);
        instance.broadcastMessage(ChatColor.of(game.getColor1()) + "Game restarting...");

        game.setGameStage(GameStage.LOBBY);
        instance.broadcastMessage(ChatColor.of(game.getColor1()) + "Game restarted.");

        game.getPlayers().values().forEach(bingoPlayer -> {
                bingoPlayer.setTable(null);
        });

        game.getTables().clear();

    }

    public void startGame() {
        var game = instance.getGame();

        game.setGameStage(GameStage.STARTING);
        instance.broadcastMessage(ChatColor.of(game.getColor1()) + "Game starting...");

        game.setGameStage(GameStage.INGAME);
        instance.broadcastMessage(ChatColor.of(game.getColor1()) + "Game has started.");

        var loc = Bukkit.getWorlds().get(0).getSpawnLocation();

        game.getPlayers().values().forEach(bingoPlayer -> {

            var uuid = bingoPlayer.getUuid();
            var table = new Table();
            instance.getBingoManager().selectItems(table);

            bingoPlayer.setTable(table);
            game.getTables().put(uuid, table);

            var player = Bukkit.getPlayer(UUID.fromString(uuid));
            if (player != null) {
                player.teleport(loc);
            }

        });

    }

    public String getTitle() {
        var game = instance.getGame();

        if (game.isNormalMode()) {

            String t = Character.toString('\uE001');
            return t;

        } else {

            String t = Character.toString('\uE000');
            return t;
        }
    }

    public void updateBoard(FastBoard board, Table table) {

        if (table == null) {
            board.updateLines("", "WAITING BOARD");

        } else {
            board.updateLines("",
                    " " + table.getPosDisplay(0, 0) + table.getPosDisplay(0, 1) + table.getPosDisplay(0, 2)
                            + table.getPosDisplay(0, 3) + table.getPosDisplay(0, 4),
                    "",
                    " " + table.getPosDisplay(1, 0) + table.getPosDisplay(1, 1) + table.getPosDisplay(1, 2)
                            + table.getPosDisplay(1, 3) + table.getPosDisplay(1, 4),
                    "",
                    " " + table.getPosDisplay(2, 0) + table.getPosDisplay(2, 1) + table.getPosDisplay(2, 2)
                            + table.getPosDisplay(2, 3) + table.getPosDisplay(2, 4),
                    "",
                    " " + table.getPosDisplay(3, 0) + table.getPosDisplay(3, 1) + table.getPosDisplay(3, 2)
                            + table.getPosDisplay(3, 3) + table.getPosDisplay(3, 4),
                    "", " " + table.getPosDisplay(4, 0) + table.getPosDisplay(4, 1) + table.getPosDisplay(4, 2)
                            + table.getPosDisplay(4, 3) + table.getPosDisplay(4, 4),
                    "");
        }

    }

}