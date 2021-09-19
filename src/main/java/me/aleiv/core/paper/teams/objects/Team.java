package me.aleiv.core.paper.teams.objects;

import java.util.List;
import java.util.UUID;

/**
 * Team intended to have more information then just the basics of a team, but
 * also scores and many more variables that are required in the different
 * applications of teams in different gamemodes.
 * 
 * @author jcedeno
 */
public class Team extends BaseTeam {
    protected Integer points;

    public Team(UUID teamID, List<UUID> members, String teamName) {
        super(teamID, members, teamName);
    }

    /*
     * @return Team's current points.
     */
    public Integer getPoints() {
        return points;
    }

    /**
     * Setter for the points of the team.
     * 
     * <b>Note</b>: This won't update the database. Intended to be used in pair with
     * other functions that actually do update the database.
     * 
     * @param points
     */
    public void setPoints(Integer points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "Team{" + "points=" + points + ", teamID=" + teamID + ", members=" + members + ", teamName='" + teamName
                + '\'' + '}';
    }

}