package me.aleiv.core.paper.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.aleiv.core.paper.game.Slot;
import me.aleiv.core.paper.game.Table;

public class FoundItemEvent extends Event {
    /*
     * Methods Required by BukkitAPI
     */
    private static final @Getter HandlerList HandlerList = new HandlerList();
    @SuppressWarnings({"java:S116", "java:S1170"})
    private final @Getter HandlerList Handlers = HandlerList;
    private final @Getter Table table;
    private final @Getter Slot slot;
    private final @Getter Player player;
    


    public FoundItemEvent(Table table, Slot slot, Player player, boolean async) {
        super(async);
        this.table = table;
        this.slot = slot;
        this.player = player;
    }

    public FoundItemEvent(Table table, Slot slot, Player player) {
        this(table, slot, player, false);
    }

}

