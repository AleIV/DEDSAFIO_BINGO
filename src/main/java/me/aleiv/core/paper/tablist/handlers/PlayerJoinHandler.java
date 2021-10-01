package me.aleiv.core.paper.tablist.handlers;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import me.aleiv.core.paper.Core;

public class PlayerJoinHandler extends Handler {
    public PlayerJoinHandler(Plugin plugin) {
        super(plugin);
        register();
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            var manager = ((Core) this.plugin).getTablist().getManager();
            Bukkit.getOnlinePlayers().forEach(player -> {
                manager.createTablist(player);
            });
        }, 20L, 100L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ((Core) this.plugin).getTablist().getManager().createTablist(event.getPlayer());

    }
}
