package me.aleiv.core.paper.tablist.handlers;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class Handler implements Listener {
    protected final Plugin plugin;

    public Handler(Plugin plugin) {
        this.plugin = plugin;
    }

    public Handler register() {
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
        return this;
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
