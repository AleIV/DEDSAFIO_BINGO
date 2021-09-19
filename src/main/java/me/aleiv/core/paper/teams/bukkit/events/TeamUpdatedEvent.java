package me.aleiv.core.paper.teams.bukkit.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.aleiv.core.paper.teams.objects.Team;

public class TeamUpdatedEvent extends Event {
    /*
     * Methods Required by BukkitAPI
     */
    private static final @Getter HandlerList HandlerList = new HandlerList();
    private final @Getter HandlerList Handlers = HandlerList;
    /** Fields */
    private Team team;
    private UUID node;

    /**
     * Constructor
     * 
     * @param team
     * @param node
     */
    public TeamUpdatedEvent(Team team, UUID node) {
        this.team = team;
        this.node = node;
    }

    /**
     * Get the team
     * 
     * @return
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Get the node where the update was orginated
     * 
     * @return
     */
    public UUID getNode() {
        return node;
    }

}
