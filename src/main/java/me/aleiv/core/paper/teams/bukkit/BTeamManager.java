package me.aleiv.core.paper.teams.bukkit;

import java.util.ArrayList;
import java.util.List;
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
        // Connect to the chain and pull current data.
        this.initialize();
    }

    /**
     * Do not use this method. It is only for internal use.
     */
    @Override
    public void updateTeam(Team team, UUID from) {
        var oldTeam = put(team);
        // If old team exists, then it is an update and not a creation.
        if (oldTeam != null) {
            // TODO: IMPLEMENT LOGIC TO VALIDATE THE DIFFERENCES AND PUT THEM INTO RAM COPY.
            callEvent(new TeamUpdatedEvent(team, from, !Bukkit.isPrimaryThread()));
        } else {
            // If old team does not exist, then it is a creation.
            put(team);
            callEvent(new TeamCreatedEvent(team, from, !Bukkit.isPrimaryThread()));
        }
    }

    /**
     * Gets a list of online Teams, if at least one of the members is online is considered as team online.
     */

    public List<Team> getTeamsOnlineList(){
        List<Team> teamsOnline = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player ->{
            var uuid = player.getUniqueId();
            var team = getPlayerTeam(uuid);
            if(team != null){
                teamsOnline.add(team);
            }
        });
        return teamsOnline;
    }

    /**
     * Adds points to a team and broadcasts update to other nodes. This method is
     * blocking.
     * 
     * @param team   The team to add points to.
     * @param points The amount of points to add.
     */
    public void addPoints(Team team, int points) {
        // Update the local copy.
        team.addPoints(points);
        team.setLastObtainedPoints(System.currentTimeMillis());
        // Communicate to the backend and propagate the update
        this.writeTeamUpdate(team);
        this.communicateUpdate(team);
    }

    public void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

}
