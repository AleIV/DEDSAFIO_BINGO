package me.aleiv.core.paper.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.GameStage;
import me.aleiv.core.paper.events.GameStartedEvent;
import me.aleiv.core.paper.events.GameTickEvent;
import me.aleiv.core.paper.game.objects.Table;
import me.aleiv.core.paper.utilities.FastBoard;
import net.md_5.bungee.api.ChatColor;

public class GlobalListener implements Listener {

    Core instance;

    public GlobalListener(Core instance) {
        this.instance = instance;
    }

    @EventHandler
    public void customSpawns(GameTickEvent e) {
        var game = instance.getGame();
        var boards = game.getBoards();

        Bukkit.getScheduler().runTask(instance, () -> {
            for (var board : boards.values()) {
                var uuid = board.getPlayer().getUniqueId();
                var table = instance.getBingoManager().findTable(uuid);

                instance.getBingoManager().updateBoard(board, table);
            }

            var timer = game.getTimer();
            if (timer.isActive()) {
                timer.refreshTime((int) game.getGameTime());
            }

        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();
        var uuid = player.getUniqueId().toString();
        var game = instance.getGame();

        var boards = game.getBoards();
        var board = new FastBoard(player);
        var manager = instance.getBingoManager();

        var table = manager.findTable(player.getUniqueId());

        if (table == null) {
            board.updateTitle(Table.getNullTitle());

        } else {
            board.updateTitle(table.getTitle());
        }

        instance.getBingoManager().updateBoard(board, table);
        boards.put(uuid, board);

        instance.sendHeader(player, "<gradient:#5e4fa2:#f79459>Welcome to BINGOOOO!</gradient>");

        var timer = game.getTimer();
        timer.getBossbar().addPlayer(player);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        var player = e.getPlayer();
        var uuid = player.getUniqueId().toString();

        var boards = instance.getGame().getBoards();
        var board = boards.remove(uuid);

        if (board != null) {
            board.delete();
        }
    }

    @EventHandler
    public void onRespawn(PlayerDeathEvent e) {
        var player = e.getEntity();
        var lobby = Bukkit.getWorld("lobby");
        var game = instance.getGame();
        var scatter = instance.getScatterManager();

        if (player.getWorld() == lobby) {
            var loc = new Location(lobby, 71.5, 126, 0.5, 90, -0);
            scatter.Qteleport(player, loc);
            
        }else if(game.getGameStage() ==  GameStage.INGAME){
            var loc = scatter.generateLocation();
            scatter.Qteleport(player, loc);

        }
    }

    @EventHandler
    public void onStart(GameStartedEvent e) {
        var game = instance.getGame();

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20.0);
            player.setSaturation(20.0f);
        });

        var round = game.getBingoRound();
        var timer = game.getTimer();

        switch (round) {
            case ONE: {

                timer.start(1200, (int) game.getGameTime());

            }
                break;

            case TWO: {

                timer.start(1800, (int) game.getGameTime());

            }
                break;

            case THREE: {
                timer.start(3600, (int) game.getGameTime());

            }
                break;

            default:
                break;
        }

        instance.broadcastMessage(ChatColor.of(game.getColor1()) + "Game has started.");

    }

}
