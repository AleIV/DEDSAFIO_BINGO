package me.aleiv.core.paper.teams;

import java.util.Arrays;
import java.util.UUID;

import com.google.gson.Gson;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import me.aleiv.core.paper.teams.exceptions.TeamAlreadyExistsException;
import me.aleiv.core.paper.teams.objects.Team;

public class TeamManager {
    /** Static Variables */
    private static Gson gson = new Gson();
    private static String dataset = "ffa";
    /** Instance Variables */
    private RedisClient redisClient;

    public TeamManager() {
        this.redisClient = RedisClient
                .create(RedisURI.builder().withHost("redis-11764.c73.us-east-1-2.ec2.cloud.redislabs.com")
                        .withPort(11764).withPassword(new StringBuilder("Gxb1D0sbt3VoyvICOQKC8IwakpVdWegW")).build());
    }

    public static void main(String[] args) throws Exception {
        var teamManager = new TeamManager();
        var team = teamManager.createTeam("Pinche", UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

    }

    /**
     * Creates a new team and validates it
     * 
     * @param teamName The name of the team.
     * @paramn teamId The UUID of the team.
     * @param uuids The UUIDs of the players in the team.
     * @return The created team.
     * @throws TeamAlreadyExistsException If the team already exists.
     */
    public Team createTeam(String teamName, UUID teamId, UUID... uuids) throws TeamAlreadyExistsException {
        return registerTeam(new Team(teamId, Arrays.asList(uuids), teamName));
    }

    /**
     * Creates a new team and validates it. Overflow method for easier use that
     * assigns random team id.
     * 
     * @param teamName The name of the team.
     * @param uuids    The UUIDs of the players in the team.
     * @return The created team.
     * @throws TeamAlreadyExistsException If the team already exists.
     */
    public Team createTeam(String teamName, UUID[] uuids) throws TeamAlreadyExistsException {
        return createTeam(teamName, UUID.randomUUID(), uuids);
    }

    /**
     * A method that registers a new team in the database. Throws an exception if
     * the team already exists.
     * 
     * @param team The team to register.
     * @throws TeamAlreadyExistsException
     * 
     * @return The team that was registered.
     */
    public Team registerTeam(Team team) throws TeamAlreadyExistsException {
        if (!validateTeam(team)) {
            throw TeamAlreadyExistsException.of(team.getTeamName());
        }
        // TODO: Add logic to register the team once validated.
        return team;
    }

    /**
     * A method that validates if a team already exists in some part of the state.
     * 
     * @param team The team to validate.
     * @return True if the team exists, false otherwise.
     */
    private boolean validateTeam(Team team) {
        // TODO: Validate if the team is not already present elsewhere.
        return team != null;
    }

}
