package me.aleiv.core.paper.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.GameStage;
import me.aleiv.core.paper.events.GameStartedEvent;
import me.aleiv.core.paper.events.GameTickEvent;
import me.aleiv.core.paper.game.objects.Table;
import me.aleiv.core.paper.game.objects.Timer;
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
            if (timer != null && game.getGameStage() == GameStage.INGAME) {
                timer.refresh(game.getGameTime());
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
        if (timer != null)
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
    public void onRespawn(PlayerRespawnEvent e) {
        var player = e.getPlayer();
        var world = Bukkit.getWorld("lobby");
        if (player.getWorld() == world) {
            player.teleport(world.getSpawnLocation());
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

                if (timer != null) {
                    timer.delete();
                    game.setTimer(new Timer(instance, 1200));

                } else {
                    game.setTimer(new Timer(instance, 1200));
                }

            }
                break;

            case TWO: {

                if (timer != null) {
                    timer.delete();
                    game.setTimer(new Timer(instance, 1800));

                } else {
                    game.setTimer(new Timer(instance, 1800));
                }

            }
                break;

            case THREE: {
                if (timer != null) {
                    timer.delete();
                    game.setTimer(new Timer(instance, 3600));

                } else {
                    game.setTimer(new Timer(instance, 3600));
                }
            }
                break;

            default:
                break;
        }

        instance.broadcastMessage(ChatColor.of(game.getColor1()) + "Game has started.");

    }

    @EventHandler
    public void onJoinHide(PlayerJoinEvent e) {
        var player = e.getPlayer();
        // If gamemode is Spectator, then hide him from all other non spectators
        if (player.getGameMode() != GameMode.SURVIVAL) {
            Bukkit.getOnlinePlayers().stream().filter(all -> all.getGameMode() == GameMode.SURVIVAL)
                    .forEach(all -> all.hidePlayer(instance, player));
        } else {
            // If gamemode isn't Spectator, then hide all spectators for him.
            Bukkit.getOnlinePlayers().stream().filter(it -> it.getGameMode() != GameMode.SURVIVAL)
                    .forEach(all -> player.hidePlayer(instance, all.getPlayer()));
        }
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent e) {
        var player = e.getPlayer();
        // If gamemode to change is spectator
        if (e.getNewGameMode() != GameMode.SURVIVAL) {

            Bukkit.getOnlinePlayers().stream().forEach(all -> {
                // If players are not specs, hide them the player
                if (all.getGameMode() == GameMode.SURVIVAL) {
                    all.hidePlayer(instance, player);
                } else {
                    // If players are specs, then show them to the player
                    player.showPlayer(instance, all);
                }
            });
        } else {
            Bukkit.getOnlinePlayers().stream().forEach(all -> {
                // When switching to other gamemodes, show them if not visible to player
                if (!all.canSee(player)) {
                    all.showPlayer(instance, player);
                }
                // If one of the players is a spec, hide them from the player
                if (all.getGameMode() != GameMode.SURVIVAL) {
                    player.hidePlayer(instance, all);
                }
            });
        }
    }

}
