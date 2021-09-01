package me.aleiv.core.paper.events;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

public class BingoEvent extends Event {
    /*
     * Methods Required by BukkitAPI
     */
    private static final @Getter HandlerList HandlerList = new HandlerList();
    @SuppressWarnings({"java:S116", "java:S1170"})
    private final @Getter HandlerList Handlers = HandlerList;
    private final @Getter List<String> winners;


    public BingoEvent(List<String> winners, boolean async) {
        super(async);
        this.winners = winners;
    }

    public BingoEvent(List<String> winners) {
        this(winners, false);
    }

}
