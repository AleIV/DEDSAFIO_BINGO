package me.aleiv.core.paper.game.objects;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.utilities.Frames;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;

public class Teleporter extends BukkitRunnable {
    private static String fullTeleport = Character.toString('\uE309');
    private static String startTeleport = Character.toString('\uE260');
    private static List<Character> preTeleport = Frames.getFramesCharsIntegers(261, 308);
    private static List<Character> postTeleport = Frames.getFramesCharsIntegers(310, 359);

    private final Iterator<Map.Entry<Player, Location>> players;

    Core instance;

    public Teleporter(Core instance, HashMap<Player, Location> players){
        this.players = players.entrySet().iterator();
        this.instance = instance;
    }

    @Override
    public void run() {
        if (!players.hasNext()) {
            this.cancel();
            return;
        }

        Map.Entry<Player, Location> playerLocationEntry = players.next();
        var player = playerLocationEntry.getKey();
        Location location = playerLocationEntry.getValue();

        Qteleport(player, location);



    }

    public void Qteleport(Player player, Location loc) {
        var task = new BukkitTCT();

        task.addWithDelay(new BukkitRunnable() {
            @Override
            public void run() {
                instance.showTitle(player, startTeleport + "", "", 0, 20, 0);
                player.playSound(loc, "bingo.tpstart", 1, 1);

            }

        }, 50);

        preTeleport.forEach(frame -> {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    instance.showTitle(player, frame + "", "", 0, 20, 0);
                }

            }, 50);
        });

        task.addWithDelay(new BukkitRunnable() {
            @Override
            public void run() {
                instance.showTitle(player, fullTeleport + "", "", 0, 20, 0);
                player.teleport(loc);
                player.setGameMode(GameMode.SURVIVAL);
                player.playSound(loc, "bingo.tpfinish", 1, 1);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 15, 20));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 20, 20));

            }

        }, 50);

        postTeleport.forEach(frame -> {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    instance.showTitle(player, frame + "", "", 0, 20, 0);
                }

            }, 50);
        });

        task.execute();

    }
}
