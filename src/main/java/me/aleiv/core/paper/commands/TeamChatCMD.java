package me.aleiv.core.paper.commands;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("info")
public class TeamChatCMD extends BaseCommand {

    private @NonNull Core instance;

    public TeamChatCMD(Core instance) {
        this.instance = instance;

    }

    @Subcommand("teamchat|tc")
    @CommandAlias("tc|teamchat")
    public void info(Player player, String text) {
        var manager = instance.getTeamManager();
        var team = manager.getPlayerTeam(player.getUniqueId());

        if(team != null){
            var str = ChatColor.of("#029283") + "[Team" + team.getTeamName() + "] " + player.getName() + ": " + ChatColor.WHITE + text;
            team.getPlayerStream().forEach(p -> {
                p.sendMessage(str);
            });

        }
        
    }

}
