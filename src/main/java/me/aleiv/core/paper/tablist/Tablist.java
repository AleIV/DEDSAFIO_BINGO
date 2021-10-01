package me.aleiv.core.paper.tablist;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.tablist.handlers.Handler;
import me.aleiv.core.paper.tablist.handlers.PlayerJoinHandler;
import me.aleiv.core.paper.tablist.reflections.ServerVersion;

public class Tablist {
    public static ExecutorService executorService = Executors
            .newCachedThreadPool((new ThreadFactoryBuilder()).setNameFormat("Tablist Thread").build());

    private TablistManager manager;
    private Core instance;

    public Tablist(Core instance) {
        this.instance = instance;
    }

    private Set<Handler> handlerSet = new HashSet<>();

    public void onEnable() {
        (new BukkitRunnable() {
            public void run() {
                TablistLoadEvent event = new TablistLoadEvent();
                Bukkit.getPluginManager().callEvent(event);
                Tablist.this.manager = event.getTablistManager();
                if (Tablist.this.manager == null) {
                    instance.getLogger().log(Level.SEVERE,
                            "Cannot find TablistManager! Please use TablistLoadEvent or download plugin which uses this library to change TablistManager.");
                    return;
                }
                instance.getLogger().log(Level.INFO, "Found tablist manager! Server version: " + ServerVersion.get());
                instance.getLogger().log(Level.INFO, "Creating tablist for online players...");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    try {
                        Tablist.this.manager.createTablist(player);
                    } catch (Exception e) {
                        instance.getLogger().log(Level.SEVERE, "Cannot create tablist for player.", e);
                    }
                }
                instance.getLogger().log(Level.INFO, "Successfully created tablist for online players!");
                Tablist.this.handlerSet.add(new PlayerJoinHandler(instance));
            }
        }).runTaskLater(instance, 1L);
    }

    public void onDisable() {
        for (Handler handler : this.handlerSet)
            handler.unregister();
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                this.manager.removeTablist(player);
            } catch (Exception e) {
                instance.getLogger().log(Level.SEVERE, "Cannot remove tablist for player.", e);
            }
        }
        executorService.shutdown();
    }

    public TablistManager getManager() {
        return this.manager;
    }
}