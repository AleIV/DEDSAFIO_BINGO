package me.aleiv.core.paper.tablist;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.aleiv.core.paper.tablist.packets.BarPacketFactory;

public class TablistManager {
    public static final UUID[] BARS = new UUID[80];

    private final Set<PlayerTablist> tablists = new HashSet<>();

    private TablistGenerator generator;

    public TablistManager(TablistGenerator generator) throws Exception {
        this.generator = generator;
        BarPacketFactory.loadInfoField();
    }

    public PlayerTablist createTablist(Player player) {
        PlayerTablist tablist = new PlayerTablist(player);
        this.tablists.add(tablist);
        String[] headerfooter = this.generator.generateHeaderFooter(player);
        tablist.sendTablist(headerfooter[0], headerfooter[1], this.generator.generateBars(player));
        return tablist;
    }

    public void removeTablist(Player player) {
        PlayerTablist tablist = getTablist(player);
        if (tablist != null)
            tablist.removeTablist();
    }

    public PlayerTablist getTablist(Player player) {
        PlayerTablist tablist;
        Iterator<PlayerTablist> var2 = this.tablists.iterator();
        do {
            if (!var2.hasNext())
                return null;
            tablist = var2.next();
        } while (!tablist.getPlayer().equals(player));
        return tablist;
    }

    static {
        for (int i = 0; i < 80; i++) {
            BARS[i] = UUID.fromString(String.format("00000000-0000-00%s-0000-000000000000",
                    new Object[] { (i < 10) ? ("0" + i) : Integer.toString(i) }));
        }
    }
}