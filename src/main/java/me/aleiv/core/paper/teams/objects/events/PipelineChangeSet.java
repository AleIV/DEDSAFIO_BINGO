package me.aleiv.core.paper.teams.objects.events;

import java.util.UUID;

public class PipelineChangeSet {
    private String newDataset;
    private UUID from;

    public PipelineChangeSet(String newDataset, UUID from) {
        this.newDataset = newDataset;
        this.from = from;
    }

    /**
     * @return The name of the new dataset to be used
     */
    public String getNewDataset() {
        return newDataset;
    }

    /**
     * @return The nodeId that that generated the request.
     */
    public UUID getFrom() {
        return from;
    }

}
