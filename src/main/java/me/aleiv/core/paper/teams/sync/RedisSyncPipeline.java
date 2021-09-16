package me.aleiv.core.paper.teams.sync;

import me.aleiv.core.paper.teams.TeamManager;

public class RedisSyncPipeline implements SyncPipelineInterface {
    private TeamManager teamManager;

    public RedisSyncPipeline(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public void communicateUpdate(String channel, String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void processUpdate(String channel, String message) {
        // TODO Auto-generated method stub

    }

}
