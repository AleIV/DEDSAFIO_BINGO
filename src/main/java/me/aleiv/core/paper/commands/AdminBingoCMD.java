package me.aleiv.core.paper.commands;

import java.util.Random;

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
import me.aleiv.core.paper.Game.GameStage;
import me.aleiv.core.paper.game.objects.Table;
import me.aleiv.core.paper.game.objects.Timer;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("admin-bingo|ab|admin|ad")
@CommandPermission("admin.perm")
public class AdminBingoCMD extends BaseCommand {

    private @NonNull Core instance;

    Random random = new Random();

    public AdminBingoCMD(Core instance) {
        this.instance = instance;

    }

    @Subcommand("start")
    public void start(CommandSender sender){
        var game = instance.getGame();
        if(game.getGameStage() != GameStage.LOBBY){
            sender.sendMessage(ChatColor.of(game.getColor3()) + "The game only can be started in lobby stage. \n Restart or finish the game to replay.");

        }else{
        
            instance.getBingoManager().startGame();

        }

    }

    @Subcommand("restart")
    public void restart(CommandSender sender){
        var game = instance.getGame();
        if(game.getGameStage() == GameStage.INGAME){
            instance.getBingoManager().restartGame();

        }else{
            
            sender.sendMessage(ChatColor.of(game.getColor3()) + "The game only can be restarted in game stage.");

        }

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

        if(game.getTimer() != null){
            game.getTimer().delete();
        }else{
            game.setTimer(new Timer(instance, seconds));
        }

    }

    @Subcommand("add-play")
    @CommandCompletion("@players")
    public void addPlay(CommandSender sender, @Flags("other") Player target) {
        var game = instance.getGame();
        var uuid = target.getUniqueId();
        var tables = game.getTables();
        var manager = instance.getBingoManager();

        var table = manager.findTable(uuid);

        if(table != null){
            sender.sendMessage(ChatColor.of(game.getColor3()) + "Bingo player " + target.getName() + " is already playing.");

        }else{
            table = new Table();
            
            table.getMembers().add(uuid);
            
            tables.add(table);
            instance.getBingoManager().selectItems(table);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo player " + target.getName() + " is now playing.");
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

            sender.sendMessage(ChatColor.of(game.getColor3()) + "Bingo player " + target.getName() + " is already not playing.");

        }else{

            tables.remove(table);
            table.getMembers().clear();
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo player " + target.getName() + " is now not playing.");
        }

    }

    @Subcommand("stage")
    @CommandCompletion("@stages")
    public void gameStage(CommandSender sender, String stage) {

        var game = instance.getGame();

        switch (stage) {
            case "LOBBY": {
                game.setGameStage(GameStage.LOBBY);
                var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
                instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Game stage switched to LOBBY stage.");
            }
                break;

            case "STARTING": {
                game.setGameStage(GameStage.STARTING);
                var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
                instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Game stage switched to STARTING stage.");
            }
                break;

            case "INGAME": {
                game.setGameStage(GameStage.INGAME);
                var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
                instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Game stage switched to INGAME stage.");
            }
                break;

            case "POSTGAME": {
                game.setGameStage(GameStage.POSTGAME);
                var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
                instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Game stage switched to POSTGAME stage.");
            }
                break;

            default: {
                sender.sendMessage(ChatColor.of(game.getColor3()) + "Invalid stage.");

            }
                break;
        }

    }

}
