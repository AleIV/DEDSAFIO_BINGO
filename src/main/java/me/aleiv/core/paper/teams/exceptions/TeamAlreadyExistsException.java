package me.aleiv.core.paper.teams.exceptions;

import me.aleiv.core.paper.teams.objects.Team;

public class TeamAlreadyExistsException extends Exception {
    public TeamAlreadyExistsException(Team team) {
        super(String.format("Team %s already exists", team.getTeamID().toString()));
    }

    public static TeamAlreadyExistsException of(Team team) {
        return new TeamAlreadyExistsException(team);
    }
}
