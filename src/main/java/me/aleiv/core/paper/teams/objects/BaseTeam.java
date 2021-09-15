package me.aleiv.core.paper.teams.objects;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BaseTeam {
    private UUID teamID;
    private List<UUID> members;
    private String teamName;

    /**
     * @param teamID   A provided team ID.
     * @param members  A list of ids of the members of a team.
     * @param teamName A provided name for a team.
     * 
     * @return A new team object.
     */
    public BaseTeam(UUID teamID, List<UUID> members, String teamName) {
        this.teamID = teamID;
        this.members = members;
        this.teamName = teamName;
    }

    /**
     * 
     * @param teamID   A provided team ID.
     * @param teamName A provided name for a team.
     * @param uuid     A provided id for a member of a team.
     * 
     * @return A new team object.
     */
    public BaseTeam(UUID teamID, String teamName, UUID... uuid) {
        this.teamID = teamID;
        this.members = Arrays.asList(uuid);
        this.teamName = teamName;
    }

    /**
     * 
     * @param teamName A provided name for a team.
     * @param uuid     A provided id for a member of a team.
     * @return A new team object.
     */
    public static BaseTeam createBaseTeam(String teamName, UUID... uuid) {
        return new BaseTeam(UUID.randomUUID(), teamName, uuid);
    }

    /**
     * @return The team ID.
     */
    public UUID getTeamID() {
        return teamID;
    }

    /**
     * @return The list of members of a team.
     */
    public List<UUID> getMembers() {
        return members;
    }

    /**
     * @return The name of a team.
     */
    public String getTeamName() {
        return teamName;
    }

}
