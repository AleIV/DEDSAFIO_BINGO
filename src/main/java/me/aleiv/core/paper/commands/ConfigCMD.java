package me.aleiv.core.paper.commands;

import java.util.Random;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.BingoDifficulty;
import me.aleiv.core.paper.Game.BingoType;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("config")
@CommandPermission("admin.perm")
public class ConfigCMD extends BaseCommand {

    private @NonNull Core instance;

    Random random = new Random();

    public ConfigCMD(Core instance) {
        this.instance = instance;

    }

    @Subcommand("difficulty")
    public void difficulty(CommandSender sender){
        var game = instance.getGame();
        var difficulty = game.getBingoDifficulty();

        if(difficulty == BingoDifficulty.EASY){

            game.setBingoDifficulty(BingoDifficulty.MEDIUM);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo difficulty switched to MEDIUM.");

        }else if(difficulty == BingoDifficulty.MEDIUM){

            game.setBingoDifficulty(BingoDifficulty.HARD);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo difficulty switched to HARD.");
    
        }else if(difficulty == BingoDifficulty.HARD){

            game.setBingoDifficulty(BingoDifficulty.EXPERT);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo difficulty switched to EXPERT.");
    
        }else if(difficulty == BingoDifficulty.EXPERT){

            game.setBingoDifficulty(BingoDifficulty.EASY);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo difficulty switched to EASY.");
    
        }

    }

    @Subcommand("type")
    public void type(CommandSender sender){
        var game = instance.getGame();
        var isFullBingo = game.isBingoTypeFull();

        if(isFullBingo){
            game.setBingoType(BingoType.LINE);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo type switched to LINE.");

        }else{
            game.setBingoType(BingoType.FULL);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo type switched to FULL.");
    
        }

    }

    /*@Subcommand("mode")
    public void mode(CommandSender sender) {
        var game = instance.getGame();
        var normalMode = game.isNormalMode();

        if (normalMode) {
            game.setBingoMode(BingoMode.TWITCH);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.DARK_PURPLE + "Twitch Rivals mode Enabled.");

        } else {
            game.setBingoMode(BingoMode.NORMAL);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.of(game.getColor1()) + "Normal mode Enabled.");

        }

        var boards = game.getBoards();

        for (var board : boards.values()) {
            var uuid = board.getPlayer().getUniqueId();
            var table = instance.getBingoManager().findTable(uuid);

            board.updateTitle(table.getTitle());
            instance.getBingoManager().updateBoard(board, table);
        }

    }*/

}
