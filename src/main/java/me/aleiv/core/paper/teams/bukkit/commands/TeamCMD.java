package me.aleiv.core.paper.teams.bukkit.commands;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.aleiv.core.paper.Core;

@CommandAlias("team|teams")
@CommandPermission("admin.perm")
public class TeamCMD extends BaseCommand {
    private Core plugin;

    public TeamCMD(Core plugin) {
        this.plugin = plugin;
    }

    @Subcommand("list|ls")
    public String listTeams(CommandSender sender) {
        System.out.println(plugin.getTeamManager().getTeamsMap());
        plugin.getTeamManager().getTeamsMap().forEach((k, v) -> {
            sender.sendMessage(v.getTeamName() + "" + ": " + v.getMembers().size() + " members");
        });
        return "";
    }


}
