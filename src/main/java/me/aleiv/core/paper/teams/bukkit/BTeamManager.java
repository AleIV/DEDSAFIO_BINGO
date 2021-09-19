package me.aleiv.core.paper.teams.bukkit;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import me.aleiv.core.paper.teams.TeamManager;
import me.aleiv.core.paper.teams.bukkit.events.TeamCreatedEvent;
import me.aleiv.core.paper.teams.objects.Team;

public class BTeamManager extends TeamManager {

    private Plugin plugin;

    public BTeamManager(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void updateTeam(Team team, UUID from) {
        put(team);
        Bukkit.getPluginManager().callEvent(new TeamCreatedEvent(team, from));

    }

}
