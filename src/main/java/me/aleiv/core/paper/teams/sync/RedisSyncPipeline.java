package me.aleiv.core.paper.teams.sync;

import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import me.aleiv.core.paper.teams.TeamManager;
import me.aleiv.core.paper.teams.objects.Team;
import me.aleiv.core.paper.teams.objects.TeamCreationUpdate;

/**
 * Redis pipeline for team synchronization. Uses a Redis PubSub connection to
 * achieve the goal.
 */
public class RedisSyncPipeline implements RedisPubSubListener<String, String> {
    private static Gson gson = new Gson();
    private TeamManager teamManager;
    private StatefulRedisPubSubConnection<String, String> pubSubConnection;
    private Logger logger;

    public RedisSyncPipeline(TeamManager teamManager) {
        this.teamManager = teamManager;
        this.pubSubConnection = teamManager.getRedisClient().connectPubSub();
        /** Add the listener */
        this.pubSubConnection.addListener(this);
        this.pubSubConnection.async().subscribe("dedsafio:events", "dedsafio:sync", "dedsafio:auth");
        this.logger = Logger.getLogger("sync-" + teamManager.getNodeId().toString().split("-")[0]);
    }

    /**
     * Method that communicates to all the other nodes about the update and event
     * that should be called.
     * 
     * @param team The team that was created.
     */
    public void communicateCreation(Team team) {
        logger.info("Attempting to communicate creation for " + team);
        this.teamManager.getRedisSyncConnection().publish("dedsafio:events",
                gson.toJson(new TeamCreationUpdate(team, teamManager.getNodeId())));
    }

    public boolean isPipelineUp() {
        return this.pubSubConnection.isOpen();
    }

    @Override
    public void message(String channel, String message) {
        // logger.info(String.format("Channel %s: %s", channel, message));
        // Now process the message into whatever it should represent
        var split = channel.split(":");
        if (split.length < 1) {
            logger.warning("Wrong format?");
            return;
        }
        if (split[0].equals("dedsafio")) {
            switch (split[1].toLowerCase()) {
                // Process logic based on the channel that recieved the message.
                case "events": {
                    try {
                        var creationUpdate = gson.fromJson(message, TeamCreationUpdate.class);
                        // Check if message is not comming from this node
                        if (creationUpdate != null) {
                            if (creationUpdate.getFrom().compareTo(teamManager.getNodeId()) == 0) {
                                logger.info(String.format(
                                        "Received creation update for %s, ignorning since it comes from ourselves.",
                                        creationUpdate.getTeam()));
                            } else {
                                logger.info("Updating " + creationUpdate.getTeam() + " from node "
                                        + creationUpdate.getFrom());
                                teamManager.updateTeam(creationUpdate.getTeam(), creationUpdate.getFrom());
                            }

                        }

                    } catch (JsonSyntaxException ex) {
                        ex.printStackTrace();
                    }

                    break;
                }
                case "sync": {
                    break;
                }
                case "auth": {
                    break;
                }
                default: {
                    logger.warning("Unknown channel: " + channel);
                }
            }
        }

    }

    @Override
    public void message(String pattern, String channel, String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void subscribed(String channel, long count) {
        // TODO Auto-generated method stub

    }

    @Override
    public void psubscribed(String pattern, long count) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unsubscribed(String channel, long count) {
        // TODO Auto-generated method stub

    }

    @Override
    public void punsubscribed(String pattern, long count) {
        // TODO Auto-generated method stub

    }

    public void closePubSubConnection() {
        this.pubSubConnection.close();
    }
    // TODO: Add logic to stop repeating this.pubSubConnection all the time.

}
