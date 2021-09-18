package me.aleiv.core.paper.teams.objects;

public class TeamCreationUpdate {
    private Team team;
    private String from;

    public Team getTeam() {
        return team;
    }

    public String getFrom() {
        return from;
    }

    public TeamCreationUpdate(Team team, String from) {
        this.team = team;
        this.from = from;
    }

}
