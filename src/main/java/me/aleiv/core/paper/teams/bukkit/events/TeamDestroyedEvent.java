package me.aleiv.core.paper.teams.bukkit.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.aleiv.core.paper.teams.objects.Team;

public class TeamDestroyedEvent extends Event {
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
    public TeamDestroyedEvent(Team team, UUID node, boolean async) {
        super(async);
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
     * Get the node where the team destroy request was orginated
     * 
     * @return
     */
    public UUID getNode() {
        return node;
    }
}
