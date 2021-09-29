package me.aleiv.core.paper.teams.demo;

import java.util.UUID;

import me.aleiv.core.paper.teams.TeamManager;
import me.aleiv.core.paper.teams.objects.Team;

public class SimpleTeamManager extends TeamManager {

    public SimpleTeamManager(String redisURI) {
        super(redisURI);
    }

    @Override
    public void updateTeam(Team team, UUID nodeId) {
        // Since this is not bukkit, we don't need to call an event or anything, so
        // let's just add the user to the ram copy.
        put(team);

    }

    @Override
    public void processCommand(String cmd, UUID nodeId) {
        System.out.println(nodeId + " sent command: " + cmd);

    }

    @Override
    public void processDestroyTeam(Team team, UUID nodeId) {
        System.out.println("removing team " + team);
        remove(team);
        
    }

}
