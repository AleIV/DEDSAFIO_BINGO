package me.aleiv.core.paper.commands;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.game.BingoManager;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("config|b|a|admin-bingo|ab|admin|ad")
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

        sender.sendMessage(ChatColor.of(game.getColor1()) + "RESPAWB SECONDS SET TO " + seconds);
    }



}
