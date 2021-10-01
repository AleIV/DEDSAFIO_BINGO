package me.aleiv.core.paper.tablist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.teams.objects.Team;
import net.md_5.bungee.api.ChatColor;

public class DedsafioTablistGenerator extends TablistGenerator {
    private HashMap<UUID, String> cachedNames = new HashMap<>();
    private static final Gson gson = new Gson();

    private static final String STAR = Character.toString('\uEAA6');
    private static final String teamTag = ChatColor.of("#59e4fc") + "Team %s " + ChatColor.WHITE + "%d"
            + ChatColor.RESET + STAR;
    private static final String teamMemberTag = ChatColor.of("#fef1aa") + "%s";
    private static final String ffaTag = "%d" + STAR + " " + ChatColor.of("#fef1aa") + "%s";

    class SortByPoints implements Comparator<Team> {

        @Override
        public int compare(Team a, Team b) {
            return b.getPoints() - a.getPoints();
        }

    }

    public DedsafioTablistGenerator(Core plugin) {
        super(plugin);
        plugin.getTeamManager().getRedisSyncConnection().hgetall("uuids:names").entrySet().forEach(entry -> {
            var name = gson.fromJson(entry.getValue(), JsonObject.class);
            if (name != null) {
                var actualName = name.get("name");
                if (actualName != null && !actualName.isJsonNull()) {
                    cachedNames.put(UUID.fromString(entry.getKey()), actualName.getAsString());
                    return;
                }
            }
            cachedNames.put(UUID.fromString(entry.getKey()), "null");
        });
    }

    @Override
    public String[] generateHeaderFooter(Player paramPlayer) {
        return List.of("§7§lDEDSAFIO", "§7§lDEDSAFIO").toArray(new String[0]);
    }

    @Override
    public TabEntry[] generateBars(Player paramPlayer) {
        var array = new TabEntry[80];
        int i = 0;
        // Obtain all the entries
        var entries = new ArrayList<Team>(plugin.getTeamManager().getTeamsMap().values());
        // Sort them by points
        Collections.sort(entries, new SortByPoints());
        // Handle the ffa case
        if (!plugin.getTeamManager().getDataset().equalsIgnoreCase("ffa")) {
            var iter = entries.iterator();
            while (iter.hasNext() && i < 80) {
                var team = iter.next();
                var id = team.getMembers().get(0);
                var name = getNameForId(id);
                var shortName = name.substring(0, Math.min(12, name.length()));
                var entry = new TabEntry(String.format(ffaTag, team.getPoints(), shortName));
                array[i] = entry;
                i++;
            }
        } else { // Handle team case
            var iter = entries.iterator();
            while (iter.hasNext() && i < 80) {
                var next = iter.next();
                array[i] = new TabEntry(String.format(teamTag, next.getTeamName(), next.getPoints()));
                for (var member : next.getMembers()) {
                    i++;
                    if (i < 80) {
                        array[i] = new TabEntry(String.format(teamMemberTag, getNameForId(member)));
                    } else {
                        break;
                    }

                }

                i++;
            }

        }
        // Fill emptys slots with nothing.
        for (; i < 80; i++) {
            array[i] = new TabEntry(" ");

        }

        return array;
    }

    private String getNameForId(UUID name) {
        var player = Bukkit.getOfflinePlayer(name);
        if (player.getName() != null) {
            return player.getName();
        }
        var cachedName = cachedNames.get(name);
        if (cachedName != null) {
            return cachedName;
        }
        return "null";
    }

}
