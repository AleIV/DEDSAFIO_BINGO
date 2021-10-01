package me.aleiv.core.paper.commands;

import java.util.Random;

import org.bukkit.Material;
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
import me.aleiv.core.paper.Game.Challenge;
import me.aleiv.core.paper.Game.GameStage;
import me.aleiv.core.paper.game.objects.Table;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("config")
@CommandPermission("admin.perm")
public class ConfigCMD extends BaseCommand {

    private @NonNull Core instance;

    Random random = new Random();

    public ConfigCMD(Core instance) {
        this.instance = instance;

    }

    @Subcommand("set-table")
    @CommandCompletion("@players")
    public void setTable(CommandSender sender, @Flags("other") Player player1, @Flags("other") Player player2) {
        var game = instance.getGame();
        var uuid1 = player1.getUniqueId();
        var uuid2 = player2.getUniqueId();
        var manager = instance.getBingoManager();

        var color1 = ChatColor.of(game.getColor1());

        var table1 = manager.findTable(uuid1);
        var table2 = manager.findTable(uuid2);

        if(table1 == null){
            sender.sendMessage(color1 + "Bingo player " + player1.getName() + " is not playing.");

        }else{
            if(table2 != null){
                table2.getMembers().remove(uuid2);
            }

            table1.getMembers().add(uuid2);
            
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            sender.sendMessage(senderName + ChatColor.of(game.getColor4()) + "Added " + player2.getName() + " to " + player1.getName() + " table.");
        }

    }

    @Subcommand("add-challenge")
    public void addChallenge(CommandSender sender, Challenge challenge, @Flags("other") Player player) {
        var manager = instance.getBingoManager();
        var game = instance.getGame();
        var color1 = ChatColor.of(game.getColor1());
        var uuid = player.getUniqueId();
        var table = manager.findTable(uuid);

        if(table == null){
            sender.sendMessage(color1 + "Bingo player " + player.getName() + " is not playing.");

        }else{
            manager.addChallenge(table, challenge, player);
            sender.sendMessage(color1 + "Tried to add " + player.getName() + " " + challenge.toString());
        }
        
    }

    @Subcommand("remove-challenge")
    public void removeChallenge(CommandSender sender, Challenge challenge, @Flags("other") Player player) {
        var manager = instance.getBingoManager();
        var game = instance.getGame();
        var color1 = ChatColor.of(game.getColor1());
        var uuid = player.getUniqueId();
        var table = manager.findTable(uuid);

        if(table == null){
            sender.sendMessage(color1 + "Bingo player " + player.getName() + " is not playing.");

        }else{
            manager.removeChallenge(table, challenge, player);
            sender.sendMessage(color1 + "Tried to remove " + player.getName() + " " + challenge.toString());
        }

    }

    @Subcommand("add-item")
    public void addItem(CommandSender sender, Material material, @Flags("other") Player player) {
        var manager = instance.getBingoManager();
        var game = instance.getGame();
        var color1 = ChatColor.of(game.getColor1());
        var uuid = player.getUniqueId();
        var table = manager.findTable(uuid);

        if(table == null){
            sender.sendMessage(color1 + "Bingo player " + player.getName() + " is not playing.");

        }else{
            manager.addItem(table, material, player);
            sender.sendMessage(color1 + "Tried to add " + player.getName() + " " + material.toString());
        }
        
    }

    @Subcommand("remove-item")
    public void removeItem(CommandSender sender, Material material, @Flags("other") Player player) {
        var manager = instance.getBingoManager();
        var game = instance.getGame();
        var color1 = ChatColor.of(game.getColor1());
        var uuid = player.getUniqueId();
        var table = manager.findTable(uuid);

        if(table == null){
            sender.sendMessage(color1 + "Bingo player " + player.getName() + " is not playing.");

        }else{
            manager.removeItem(table, material, player);
            sender.sendMessage(color1 + "Tried to remove " + player.getName() + " " + material.toString());
        }

    }

    @Subcommand("add-play")
    @CommandCompletion("@players")
    public void addPlay(CommandSender sender, @Flags("other") Player target) {
        var game = instance.getGame();
        var uuid = target.getUniqueId();
        var tables = game.getTables();
        var manager = instance.getBingoManager();

        var color1 = ChatColor.of(game.getColor1());

        var table = manager.findTable(uuid);

        if(table != null){
            sender.sendMessage(color1 + "Bingo player " + target.getName() + " is already playing.");

        }else{
            table = new Table();
            
            table.getMembers().add(uuid);
            
            tables.add(table);
            table.selectItems(instance);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            sender.sendMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo player " + target.getName() + " is now playing.");
        }

    }

    @Subcommand("remove-play")
    @CommandCompletion("@players")
    public void removePlay(CommandSender sender, @Flags("other") Player target) {
        var game = instance.getGame();
        var uuid = target.getUniqueId();

        var tables = game.getTables();
        var manager = instance.getBingoManager();
        var table = manager.findTable(uuid);
        
        if(table == null){

            sender.sendMessage(ChatColor.of(game.getColor3()) + "Bingo player " + target.getName() + " is not playing.");

        }else{

            tables.remove(table);
            table.getMembers().clear();
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            sender.sendMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo player " + target.getName() + " is now not playing.");
        }

    }

    @Subcommand("stage")
    public void gameStage(CommandSender sender, GameStage stage) {

        var game = instance.getGame();

        switch (stage) {
            case LOBBY: {
                game.setGameStage(GameStage.LOBBY);
                var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
                sender.sendMessage(senderName + ChatColor.of(game.getColor4()) + "Game stage switched to LOBBY stage.");
            }
                break;

            case STARTING: {
                game.setGameStage(GameStage.STARTING);
                var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
                sender.sendMessage(senderName + ChatColor.of(game.getColor4()) + "Game stage switched to STARTING stage.");
            }
                break;

            case INGAME: {
                game.setGameStage(GameStage.INGAME);
                var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
                sender.sendMessage(senderName + ChatColor.of(game.getColor4()) + "Game stage switched to INGAME stage.");
            }
                break;

            case POSTGAME: {
                game.setGameStage(GameStage.POSTGAME);
                var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
                sender.sendMessage(senderName + ChatColor.of(game.getColor4()) + "Game stage switched to POSTGAME stage.");
            }
                break;

            default: {
                sender.sendMessage(ChatColor.of(game.getColor3()) + "Invalid stage.");

            }
                break;
        }

    }

    

}