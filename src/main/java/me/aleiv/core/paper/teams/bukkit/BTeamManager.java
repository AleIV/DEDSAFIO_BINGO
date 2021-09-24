package me.aleiv.core.paper.teams.bukkit;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import me.aleiv.core.paper.teams.TeamManager;
import me.aleiv.core.paper.teams.bukkit.events.TeamCreatedEvent;
import me.aleiv.core.paper.teams.bukkit.events.TeamUpdatedEvent;
import me.aleiv.core.paper.teams.objects.Team;

/**
 * Simple class to make a TeamManager for Bukkit.
 * 
 * @author jcedeno
 */
public class BTeamManager extends TeamManager {

    Plugin plugin;

    /**
     * Constructor for the BTeamManager class.
     * 
     * @param plugin   A Bukkit plugin instance.
     * @param redisURI The URI of the Redis server. Include password and port if
     *                 needed.
     */
    public BTeamManager(Plugin plugin, String redisURI) {
        super(redisURI);
        this.plugin = plugin;
    }

    @Override
    public void updateTeam(Team team, UUID from) {
        var oldTeam = put(team);
        // If old team exists, then it is an update and not a creation.
        if (oldTeam != null) {
            callEvent(new TeamUpdatedEvent(team, from));
        } else {
            // If old team does not exist, then it is a creation.
            put(team);
            callEvent(new TeamCreatedEvent(team, from));
        }

    }

    public void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

}
