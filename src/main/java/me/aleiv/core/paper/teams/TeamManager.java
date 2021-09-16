package me.aleiv.core.paper.teams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import me.aleiv.core.paper.teams.exceptions.TeamAlreadyExistsException;
import me.aleiv.core.paper.teams.objects.Team;
import me.aleiv.core.paper.teams.sync.RedisSyncPipeline;

public class TeamManager {
    /** Static Variables */
    private static Gson gson = new Gson();
    private static UUID nodeId = UUID.randomUUID();
    private static final String BACKUP_SET = "historical-sets";
    /** This is the name of the hashset on redis. */
    private String dataset = "ffa";
    /** Instance Variables */
    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> redisConnection;
    private ConcurrentHashMap<UUID, Team> teams;
    /** Synchronisation pipeline */
    private RedisSyncPipeline syncPipeline;

    public TeamManager() {
        this.teams = new ConcurrentHashMap<>();
        this.redisClient = RedisClient.create("redis://localhost");
        this.redisConnection = this.redisClient.connect();
        this.syncPipeline = new RedisSyncPipeline(this);
    }

    public static void main(String[] args) throws Exception {
        var teamManager = new TeamManager();
        // teamManager.restoreOldDataset("ffa");

    }

    /**
     * It changes the dataset that is used to store the teams. both locally and
     * remotely. This method will block until the operation is completed.
     * 
     * @param newSet The new dataset name.
     */
    public void changeDataset(String newSet) {
        // Nill all the data, dump it somewhere.
        backupDataset();
        teams.clear();
        this.dataset = newSet;
        // TODO: Communicate to other nodes the result of the change.
    }

    /**
     * It will backup the current team dataset to the historical-sets hash.
     */
    private void backupDataset() {
        if (!teams.isEmpty() && redisConnection.isOpen()) {
            // Key in format dataset:timeStamp:nodeId
            var key = this.dataset + ":" + System.currentTimeMillis() + ":" + nodeId;
            // Value in format json, contains all teams as an array of teams.
            var value = gson.toJson(teams.values());
            // Connect and backup the old data set.
            var syncCon = redisConnection.sync();
            syncCon.hset(BACKUP_SET, key, value);
        }
    }

    /**
     * It restores the old dataset. Once the update is deamed succesful, the
     * function will communicate to other nodes of the changes.
     * 
     * @param oldSet The old dataset name to be restored.
     */
    public void restoreOldDataset(String oldSet) {
        // Create a synchronous connection to redis.
        var syncCon = redisConnection.sync();
        var keys = syncCon.hkeys(BACKUP_SET);
        var matchedFields = new ArrayList<String>();
        // Iterate through to find those that match the old set.
        for (var key : keys) {
            var parsed = key.split(":");
            // If length greater than 1 means that the key is a valid key.
            if (parsed.length > 1 && parsed[0].equals(oldSet))
                matchedFields.add(key);
        }
        // Turn the matchedFields list into an array of strings.
        var matchedFieldValues = syncCon.hmget(BACKUP_SET, matchedFields.toArray(new String[0]));
        var newTeamsMap = new HashMap<UUID, Team>();
        // Iterate through the matchedFieldValues and parse them into teams.
        for (var fieldValues : matchedFieldValues) {
            var value = fieldValues.getValue();
            var teamList = gson.fromJson(value, Team[].class);
            // Parse all teams into a map.
            for (var team : teamList) {
                newTeamsMap.put(team.getTeamID(), team);
            }
        }
        // Backup current data in case of failure.
        backupDataset();
        // Perform the change of data
        teams.clear();
        teams.putAll(newTeamsMap);
        // TODO: Communicate update to other nodes.
    }

    /**
     * Creates a new team and validates it
     * 
     * @param teamName The name of the team.
     * @param teamId   The UUID of the team.
     * @param uuids    The UUIDs of the players in the team.
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
        teams.put(team.getTeamID(), team);
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
        /**
         * Should check locally, then remotely. Also, ensure a user is not present in
         * two teams at once.
         */
        return team != null;
    }

}
