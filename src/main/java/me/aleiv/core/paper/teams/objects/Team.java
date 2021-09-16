package me.aleiv.core.paper.teams.objects;

import java.util.List;
import java.util.UUID;

/**
 * Team
 */
public class Team extends BaseTeam {
    protected Integer points;

    public Team(UUID teamID, List<UUID> members, String teamName) {
        super(teamID, members, teamName);
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "Team{" + "points=" + points + ", teamID=" + teamID + ", members=" + members + ", teamName='" + teamName
                + '\'' + '}';
    }

}