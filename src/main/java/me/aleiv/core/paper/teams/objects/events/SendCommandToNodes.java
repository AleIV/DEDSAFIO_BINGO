package me.aleiv.core.paper.teams.objects.events;

import java.util.UUID;

public class SendCommandToNodes {
    private String command;
    private UUID from;

    /**
     * @return The nodeId that that generated the request.
     */
    public UUID getFrom() {
        return from;
    }

    /**
     * @return The command to send to the nodes.
     */
    public String getCommand() {
        return command;
    }

    public SendCommandToNodes(String command, UUID from) {
        this.command = command;
        this.from = from;
    }

}
