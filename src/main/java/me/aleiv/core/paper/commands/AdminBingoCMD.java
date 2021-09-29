package me.aleiv.core.paper.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.game.BingoManager;
import me.aleiv.core.paper.teams.exceptions.TeamAlreadyExistsException;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("admin|ad|a")
@CommandPermission("admin.perm")
public class AdminBingoCMD extends BaseCommand {

    private @NonNull Core instance;

    Random random = new Random();

    public AdminBingoCMD(Core instance) {
        this.instance = instance;

    }

    @CommandPermission("admin.perm")
    @Subcommand("tpworld")
    @CommandAlias("tpworld")
    @CommandCompletion("@worlds")
    public void tpWorld(Player player, World world) {
        player.teleport(world.getSpawnLocation());
        player.sendMessage(ChatColor.GRAY + "Teleported to world " + world.getName());
    }

    @Subcommand("locations")
    public void generateLocations(CommandSender sender, Integer i) {
        var game = instance.getGame();
        var manager = instance.getScatterManager();

        manager.generateLocations(i);
        sender.sendMessage(ChatColor.of(game.getColor1()) + "LOCATIONS GENERATED " + i);

    }

    @Subcommand("timer")
    public void setTimer(CommandSender sender, Integer seconds) {
        var game = instance.getGame();

        game.getTimer().start(seconds, (int) game.getGameTime());

    }

    @Subcommand("respawn-seconds")
    public void setRespawnSeconds(CommandSender sender, Integer seconds) {
        var game = instance.getGame();
        BingoManager.respawnSeconds = seconds;

        sender.sendMessage(ChatColor.of(game.getColor1()) + "RESPAWN SECONDS SET TO " + seconds);
    }

    @Subcommand("create-team")
    @CommandCompletion("@players")
    public void createTeam(CommandSender sender, String... str) {
        var manager = instance.getTeamManager();
        var game = instance.getGame();

        CompletableFuture.supplyAsync(() -> {
            List<UUID> playerUuids = new ArrayList<>();

            for (var s : str) {
                var player = Bukkit.getPlayer(s);
                if (player != null) {
                    playerUuids.add(player.getUniqueId());
                }
            }

            var arr = playerUuids.toArray(new UUID[] {});
            var map = manager.getTeamsMap();
            var name = "#" + (map.values().size() + 1);

            try {
                manager.createTeam(name, arr);

            } catch (TeamAlreadyExistsException e) {

                e.printStackTrace();
            }

            return name;
        }).thenAccept(name -> {
            sender.sendMessage(ChatColor.of(game.getColor1()) + "Team " + name + " created.");

        });

    }

    @Subcommand("destroy-team")
    @CommandCompletion("@players")
    public void destroyTeam(CommandSender sender, @Flags("other") Player player) {
        var manager = instance.getTeamManager();
        var game = instance.getGame();

        var team = manager.getPlayerTeam(player.getUniqueId());
        if (team != null) {
            CompletableFuture.supplyAsync(() -> {

                manager.destroyTeam(team);

                return true;
            }).thenAccept(bool -> {
                sender.sendMessage(ChatColor.of(game.getColor1()) + "Team destroyed.");

            });
        }

    }

    @Subcommand("destroy-all-teams")
    public void destroyTeamAll(CommandSender sender) {
        var manager = instance.getTeamManager();
        var game = instance.getGame();
        var map = manager.getTeamsMap();

        CompletableFuture.supplyAsync(() -> {

            map.values().forEach(team ->{
                manager.destroyTeam(team);
            });

            return true;
        }).thenAccept(bool -> {
            sender.sendMessage(ChatColor.of(game.getColor1()) + "Teams destroyed.");

        });

    }

    @Subcommand("create-global-ffa-teams")
    public void createAllTeams(CommandSender sender) {
        var manager = instance.getTeamManager();
        var map = manager.getTeamsMap();
        var game = instance.getGame();

        CompletableFuture.supplyAsync(() -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                var uuid = player.getUniqueId();
                if (manager.getPlayerTeam(uuid) == null) {
                    try {

                        var name = "#" + (map.values().size() + 1);
                        var list = List.of(uuid);
                        var arr = list.toArray(new UUID[] {});
                        manager.createTeam(name, arr);

                    } catch (TeamAlreadyExistsException e) {

                        e.printStackTrace();
                    }
                }
            });

            return true;
        }).thenAccept(added -> {
            sender.sendMessage(ChatColor.of(game.getColor1()) + "Created a team for all players.");

        });
    }

    @Subcommand("dataset")
    public void changeSet(CommandSender sender, String string, boolean bool) {
        var manager = instance.getTeamManager();
        var game = instance.getGame();

        CompletableFuture.supplyAsync(() -> {
            manager.changeDataset(string, bool);

            return true;
        }).thenAccept(added -> {
            sender.sendMessage(ChatColor.of(game.getColor1()) + "Changed dataset to " + string);

        });

    }

    @Subcommand("points")
    @CommandCompletion("@players")
    public void changeSet(CommandSender sender, @Flags("other") Player player, Integer i) {
        var manager = instance.getBingoManager();
        var game = instance.getGame();

        CompletableFuture.supplyAsync(() -> {
            var table = manager.findTable(player.getUniqueId());
            if (table != null) {
                table.addPoints(i, "points");
            }

            return true;
        }).thenAccept(added -> {
            sender.sendMessage(ChatColor.of(game.getColor1()) + "Points " + player.getName() + " to " + i);

        });

    }

}
