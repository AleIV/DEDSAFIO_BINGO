package me.aleiv.core.paper.game.objects;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.GameStage;
import net.md_5.bungee.api.ChatColor;

public class Teleporter extends BukkitRunnable {

    private final Iterator<Map.Entry<Player, Location>> players;

    Core instance;
    int count = 0;

    public Teleporter(Core instance, HashMap<Player, Location> players) {
        this.players = players.entrySet().iterator();
        this.instance = instance;
        instance.getGame().setGameStage(GameStage.STARTING);
    }

    @Override
    public void run() {
        // Once the iterator is empty, this task should be done.
        if (!players.hasNext()) {
            // Cancel this task
            this.cancel();
            // Schedule a task to run 5 seconds later (compensate for lag) to actually
            // start.
            Bukkit.getScheduler().runTaskLater(instance, __ -> instance.getBingoManager().totalStart(), 20 * 5);
            return;
        }
        // Obtain the entry
        var playerLocationEntry = players.next();
        // Obtain the key and value
        var player = playerLocationEntry.getKey();
        var location = playerLocationEntry.getValue();
        // Teleport and hold until that task is completed.
        var qteleport = instance.getScatterManager().Qteleport(player, location);
        // Dont move forward unless is actually done.
        while (!qteleport.isDone()) {
        }
        // Now that we are guaranteed that task is done, we can actually increase the
        // count and move to the next tick
        count++;
        // Communicate the message to everyone
        instance.adminMessage(ChatColor.RED + "" + count + " " + player.getName() + " scattered.");
    }
}
