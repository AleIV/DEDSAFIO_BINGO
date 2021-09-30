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

    public Teleporter(Core instance, HashMap<Player, Location> players){
        this.players = players.entrySet().iterator();
        this.instance = instance;
        instance.getGame().setGameStage(GameStage.STARTING);
    }

    @Override
    public void run() {
        if (!players.hasNext()) {
            this.cancel();

            Bukkit.getScheduler().runTaskLater(instance, task->{
                instance.getBingoManager().totalStart();
            }, 20*5);

            return;
        }

        Map.Entry<Player, Location> playerLocationEntry = players.next();
        var player = playerLocationEntry.getKey();
        Location location = playerLocationEntry.getValue();

        instance.getScatterManager().Qteleport(player, location);

        count++;
        instance.adminMessage(ChatColor.RED + "" + count + " " + player.getName() + " scattered.");
    }
}
