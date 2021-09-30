package me.aleiv.core.paper.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.game.objects.Table;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("info")
public class InfoCMD extends BaseCommand {

    private @NonNull Core instance;

    public InfoCMD(Core instance) {
        this.instance = instance;

    }

    @Subcommand("global")
    public void info(CommandSender sender) {
        var game = instance.getGame();
        var str = new StringBuilder();
        str.append(ChatColor.YELLOW + "STAGE: " + ChatColor.AQUA + game.getGameStage().toString() + " ");
        str.append(ChatColor.YELLOW + "ROUND: " + ChatColor.AQUA + game.getBingoRound().toString() + " ");
        str.append(ChatColor.YELLOW + "FASE: " + ChatColor.AQUA + game.getBingoFase().toString() + " ");
        str.append(ChatColor.YELLOW + "TABLES: " + ChatColor.AQUA + game.getTables().size() + " ");

        sender.sendMessage(str.toString());
    }

    @Subcommand("team-list")
    public void infoTeams(CommandSender sender) {
        var man = instance.getTeamManager();
        var map = man.getTeamsMap();
        var str = new StringBuilder();
        for (var team : map.values()) {
            List<String> list = new ArrayList<>();
            for (var member : team.getMembers()) {
                var player = Bukkit.getPlayer(member);
                if(player != null){
                    list.add(player.getName());
                }
            }
            str.append(ChatColor.WHITE + team.getTeamName() + ": " + ChatColor.GOLD + team.getPoints() + "" + Table.getStar() + list.toString());
        }

        sender.sendMessage(str.toString());
    }

    @Subcommand("info")
    public void infoPlayer(CommandSender sender, @Flags("other") Player player) {
        var man = instance.getTeamManager();
        var uuid = player.getUniqueId();
        var team = man.getPlayerTeam(uuid);

        if(team != null){
            sender.sendMessage(ChatColor.GREEN + player.getName() + " IS " + team.getTeamName() + ": " + "POINTS: " + team.getPoints()); 
              
        }else{
            sender.sendMessage(ChatColor.DARK_RED + "Team is null");
        }
    }
}
