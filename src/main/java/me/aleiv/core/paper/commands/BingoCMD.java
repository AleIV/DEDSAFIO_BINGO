package me.aleiv.core.paper.commands;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.game.objects.BingoTableGUI;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("bingo")
public class BingoCMD extends BaseCommand {

    private @NonNull Core instance;

    public BingoCMD(Core instance) {
        this.instance = instance;

    }

    @Default
    public void checkPlayer(Player sender){
        var game = instance.getGame();
        var uuid = sender.getUniqueId();

        var manager = instance.getBingoManager();
        var table = manager.findTable(uuid);

        if(table == null){
            sender.sendMessage(ChatColor.of(game.getColor3()) + "" + sender.getName() + " table doesn't exist.");
        }else{
            var gui = new BingoTableGUI(table).getGui();
            gui.open(sender);
        }
    }

    @Subcommand("table")
    @CommandCompletion("@players")
    @CommandPermission("admin.perm")
    public void checkPlayer(Player sender, @Flags("other") Player target) {
        var game = instance.getGame();
        var uuid = target.getUniqueId();

        var manager = instance.getBingoManager();
        var table = manager.findTable(uuid);

        if(table == null){
            sender.sendMessage(ChatColor.of(game.getColor3()) + "" + target.getName() + " table doesn't exist.");
        }else{
            var gui = new BingoTableGUI(table).getGui();
            gui.open(sender);
        }
    }

}
