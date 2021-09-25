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

}
