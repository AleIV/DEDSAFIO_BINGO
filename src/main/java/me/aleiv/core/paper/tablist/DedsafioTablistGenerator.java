package me.aleiv.core.paper.tablist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.teams.objects.Team;

public class DedsafioTablistGenerator extends TablistGenerator {

    class SortByPoints implements Comparator<Team> {

        @Override
        public int compare(Team a, Team b) {
            return b.getPoints() - a.getPoints();
        }

    }

    public DedsafioTablistGenerator(Core plugin) {
        super(plugin);
    }

    @Override
    public String[] generateHeaderFooter(Player paramPlayer) {
        return List.of("§7§lDEDSAFIO", "§7§lDEDSAFIO").toArray(new String[0]);
    }

    private static final String STAR = Character.toString('\uEAA6');

    @Override
    public TabEntry[] generateBars(Player paramPlayer) {
        var array = new TabEntry[80];
        int i = 0;
        if (plugin.getTeamManager().getDataset().equalsIgnoreCase("ffa")) {
            var entries = new ArrayList<Team>(plugin.getTeamManager().getTeamsMap().values());
            Collections.sort(entries, new SortByPoints());
            var iter = entries.iterator();
            var random = new Random();
            while (iter.hasNext()) {
                var team = iter.next();
                var id = team.getMembers().get(0);
                var player = Bukkit.getOfflinePlayer(id);
                if (player.getName() != null) {
                    var name = player.getName().substring(0, Math.min(12, player.getName().length()));
                    var entry = new TabEntry(team.getPoints() + STAR + " " + name);
                    array[i] = entry;
                } else {
                    var falseName = "NoName" + random.nextInt(999999);
                    var name = falseName.substring(0, Math.min(12, falseName.length()));
                    var entry = new TabEntry(team.getPoints() + STAR + " " + name);
                    array[i] = entry;

                }
                i++;
            }

            for (; i < 79; i++) {
                array[i] = new TabEntry(" ");

            }
            array[79] = new TabEntry("§7§lDEDSAFIO");
        } else {
            var entries = new ArrayList<Team>(plugin.getTeamManager().getTeamsMap().values());
            Collections.sort(entries, new SortByPoints());
            var iter = entries.iterator();
            while (iter.hasNext()) {
                var next = iter.next();
                
                

                i++;
            }

            for (; i < 79; i++) {
                array[i] = new TabEntry(" ");

            }
            array[79] = new TabEntry("§7§lDEDSAFIO");

        }

        return array;
    }

}
