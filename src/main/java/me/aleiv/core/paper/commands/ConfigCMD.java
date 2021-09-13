package me.aleiv.core.paper.commands;

import java.util.Random;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.BingoRound;
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
    public void difficulty(CommandSender sender, BingoRound round){
        var game = instance.getGame();

        game.setBingoRound(round);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo round switched to " + round.toString());

    }

}
