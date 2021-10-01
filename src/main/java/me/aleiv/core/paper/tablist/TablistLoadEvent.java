package me.aleiv.core.paper.tablist;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TablistLoadEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    private TablistManager manager = null;

    public TablistLoadEvent() {
        super(false);
    }

    public void setTablistManager(TablistManager manager) {
        this.manager = manager;
    }

    public TablistManager getTablistManager() {
        return this.manager;
    }
}
