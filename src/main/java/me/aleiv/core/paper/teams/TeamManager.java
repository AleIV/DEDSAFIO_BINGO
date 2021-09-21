package me.aleiv.core.paper.teams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.google.gson.Gson;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.Getter;
import lombok.Setter;
import me.aleiv.core.paper.teams.exceptions.EmptyDatasetException;
import me.aleiv.core.paper.teams.exceptions.TeamAlreadyExistsException;
import me.aleiv.core.paper.teams.objects.Team;
import me.aleiv.core.paper.teams.sync.RedisSyncPipeline;

/**
 * A concurrent, multi nodal, multi threaded team manager.
 * 
 * @author jcedeno
 */
public abstract class TeamManager {
    /** Static Variables */
    private static Gson gson = new Gson();
    private static UUID nodeId = UUID.randomUUID();
    private static final String BACKUP_SET = "historical-sets";
    /** This is the name of the hashset on redis. */
    private @Getter @Setter String dataset = "ffa";
    /** Instance Variables */
    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> redisConnection;
    private ConcurrentHashMap<UUID, Team> teams;
    /** Synchronisation pipeline */
    private RedisSyncPipeline syncPipeline;
    private Logger logger;

    public TeamManager() {
        this.teams = new ConcurrentHashMap<>();
        this.redisClient = RedisClient.create("redis://localhost");
        this.redisConnection = this.redisClient.connect();
        this.syncPipeline = new RedisSyncPipeline(this);
        this.logger = Logger.getLogger("TeamManager-" + nodeId.toString().split("-")[0]);
    }

    public abstract void updateTeam(Team team, UUID nodeId);

    /**
     * @return Returns the player team, it doesn't have one return null.
     */
    public Team getPlayerTeam(UUID uuid){

        if (teams.isEmpty()) return null;
        return teams.values().stream().filter(team -> team.isMember(uuid)).findFirst().orElse(null);
    }
    /**
     * @return The common redis sync connection used to send messages or use any
     *         other command. This object blocks the thread that executes it.
     */
    public RedisCommands<String, String> getRedisSyncConnection() {
        return redisConnection.sync();
    }

    /**
     * @return The concurrent map of teams currently in ram.
     */
    public ConcurrentHashMap<UUID, Team> getTeamsMap() { 
        return this.teams;
    }

    /**
     * Just plainly puts the team object into the map. No security checks are taken,
     * so use this with caution.
     * 
     * @param team The team to add to the map.
     * @return The previous value associated with key, or null if there was no
     *         mapping for key
     */
    public Team put(Team team) {
        return this.teams.put(team.getTeamID(), team);
    }

    /**
     * Function that must be called at least once to guarantee data accuracy.
     */
    public void initialize() {
        // Ask redis if there is an ongoing sync in the db
        var syncConn = getRedisSyncConnection();
        if (syncConn.hlen(dataset) != 0) {
            // Restore all the current data
            syncConn.hgetall(dataset).forEach((k, v) -> {
                var team = gson.fromJson(v, Team.class);
                teams.put(team.getTeamID(), team);
            });
        }
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public void printContentsOfSet() {
        logger.info(gson.toJson(teams.values()));
    }

    /**
     * Util function that returns the status of the pipeline.
     * 
     * @return the status of the pipeline.
     */
    public boolean isPipelineUp() {
        return this.syncPipeline.isPipelineUp();
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
            var field = this.dataset + ":" + System.currentTimeMillis() + ":" + nodeId;
            // Value in format json, contains all teams as an array of teams.
            var value = gson.toJson(teams.values());
            // Connect and backup the old data set.
            var syncCon = getRedisSyncConnection();
            syncCon.hset(BACKUP_SET, field, value);
        }
    }

    /**
     * It restores the old dataset. Once the update is deamed succesful, the
     * function will communicate to other nodes of the changes.
     * 
     * @throws EmptyDatasetException If the historical-sets hash is not present.
     * @param oldSet The old dataset name to be restored.
     */
    public void restoreOldDataset(String oldSet) throws EmptyDatasetException {
        // Create a synchronous connection to redis.
        var syncCon = getRedisSyncConnection();
        var keys = syncCon.hkeys(BACKUP_SET);
        // Check if the historical-sets is empty
        if (keys.isEmpty())
            throw new EmptyDatasetException(BACKUP_SET + " does not contain any hashes.");
        var matchedFields = new ArrayList<String>();
        // Iterate through to find those that match the old set.
        for (var key : keys) {
            var parsed = key.split(":");
            // If length greater than 1 means that the key is a valid key.
            if (parsed.length > 1 && parsed[0].equals(oldSet))
                matchedFields.add(key);
        }
        if (keys.isEmpty())
            throw new EmptyDatasetException(
                    "Dataset " + oldSet + " is not present in the backup set " + BACKUP_SET + ".");
        // Turn the matchedFields list into an array of strings.
        final var matchedFieldsArray = matchedFields.toArray(new String[0]);
        var matchedFieldValues = syncCon.hmget(BACKUP_SET, matchedFieldsArray);
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

        logger.info("Succesfully restored dataset: " + oldSet);
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
        var team = registerTeam(new Team(teamId, Arrays.asList(uuids), teamName));
        // Communicate the update as succesful.
        syncPipeline.communicateCreation(team);
        return team;
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
    public Team createTeam(String teamName, UUID... uuids) throws TeamAlreadyExistsException {
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
            throw TeamAlreadyExistsException.of(team);
        }
        var syncCon = getRedisSyncConnection();
        syncCon.hset(dataset, team.getTeamID().toString(), gson.toJson(team));

        teams.put(team.getTeamID(), team);

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
        if (redisConnection.isOpen())
            return !getRedisSyncConnection().hexists(dataset, team.getTeamID().toString());

        return team != null;
    }

    public RedisClient getRedisClient() {
        return this.redisClient;
    }

    /**
     * A method that performs a total disconnection from the redis server.
     */
    public void disconect() {
        this.redisConnection.close();
        this.syncPipeline.closePubSubConnection();
    }

}
