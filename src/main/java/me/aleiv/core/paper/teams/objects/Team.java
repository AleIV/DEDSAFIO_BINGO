package me.aleiv.core.paper.teams.objects;

import java.util.List;
import java.util.UUID;

/**
 * Team
 */
public class Team extends BaseTeam {
    private Integer points;

    public Team(UUID teamID, List<UUID> members, String teamName) {
        super(teamID, members, teamName);
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

}