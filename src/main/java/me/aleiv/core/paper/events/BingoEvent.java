package me.aleiv.core.paper.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.aleiv.core.paper.Game.BingoType;

public class BingoEvent extends Event {
    /*
     * Methods Required by BukkitAPI
     */
    private static final @Getter HandlerList HandlerList = new HandlerList();
    @SuppressWarnings({"java:S116", "java:S1170"})
    private final @Getter HandlerList Handlers = HandlerList;
    private final @Getter FoundItemEvent foundItemEvent;
    private final @Getter BingoType bingoType;


    public BingoEvent(FoundItemEvent foundItemEvent, BingoType bingoType, boolean async) {
        super(async);
        this.foundItemEvent = foundItemEvent;
        this.bingoType = bingoType;
    }

    public BingoEvent(FoundItemEvent foundItemEvent, BingoType bingoType) {
        this(foundItemEvent, bingoType, false);
    }

}
