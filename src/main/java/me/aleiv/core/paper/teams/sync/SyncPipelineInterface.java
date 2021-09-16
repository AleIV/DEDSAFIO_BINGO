package me.aleiv.core.paper.teams.sync;

public interface SyncPipelineInterface {
    void communicateUpdate(String channel, String message);

    void processUpdate(String channel, String message);

}
