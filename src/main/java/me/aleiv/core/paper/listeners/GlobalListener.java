package me.aleiv.core.paper.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.events.GameTickEvent;
import me.aleiv.core.paper.game.BingoPlayer;
import me.aleiv.core.paper.utilities.FastBoard;

public class GlobalListener implements Listener{
    
    Core instance;

    public GlobalListener(Core instance){
        this.instance = instance;
    }


    @EventHandler
    public void customSpawns(GameTickEvent e) {
        var game = instance.getGame();
        var boards = game.getBoards();
    
        Bukkit.getScheduler().runTask(instance, () -> {
            for (var board : boards.values()) {
                var uuid = board.getPlayer().getUniqueId().toString();
                var table = game.getTables().get(uuid);
                instance.getBingoManager().updateBoard(board, table);

            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        var player = e.getPlayer();
        var uuid = player.getUniqueId().toString();
        var game = instance.getGame();

        var players = game.getPlayers();
        var tables = game.getTables();

        var table = tables.containsKey(uuid) ? tables.get(uuid) : null;
        var bingoPlayer = new BingoPlayer(uuid);

        if(table != null){
            bingoPlayer.setTable(table);
        }

        if(players.containsKey(uuid)){
            players.remove(uuid);
            players.put(uuid, bingoPlayer);

        }else{
            players.put(uuid, bingoPlayer);

        }

        var boards = game.getBoards();
        var board = new FastBoard(player);
        var title = instance.getBingoManager().getTitle();

        board.updateTitle(" " + title);
        instance.getBingoManager().updateBoard(board, table);
        boards.put(uuid, board);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        var player = e.getPlayer();
        var uuid = player.getUniqueId().toString();
        var players = instance.getGame().getPlayers();

        if(players.containsKey(uuid)){
            players.remove(uuid);
        }

        var boards = instance.getGame().getBoards();
        var board = boards.remove(uuid);

        if (board != null) {
            board.delete();
        }
    }
}
