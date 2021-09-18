package me.aleiv.core.paper.teams.sync;

import com.google.gson.Gson;

import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import me.aleiv.core.paper.teams.TeamManager;

/**
 * Redis pipeline for team synchronization. Uses a Redis PubSub connection to
 * achieve the goal.
 */
public class RedisSyncPipeline implements RedisPubSubListener<String, String> {
    private static Gson gson = new Gson();
    private TeamManager teamManager;
    private StatefulRedisPubSubConnection<String, String> pubSubConnection;

    public RedisSyncPipeline(TeamManager teamManager) {
        this.teamManager = teamManager;
        this.pubSubConnection = teamManager.getRedisClient().connectPubSub();
        /** Add the listener */
        this.pubSubConnection.addListener(this);
        this.pubSubConnection.async().subscribe("dedsafio:events", "dedsafio:sync", "dedsafio:auth");
    }

    public boolean isPipelineUp() {
        return this.pubSubConnection.isOpen();
    }

    @Override
    public void message(String channel, String message) {
        System.out.println("Received message: " + message + ", from channel " + channel);

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

}
