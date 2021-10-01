package me.aleiv.core.paper.tablist;

import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.aleiv.core.paper.tablist.gameprofile.Bar;
import me.aleiv.core.paper.tablist.packets.BarPacketFactory;
import me.aleiv.core.paper.tablist.reflections.PacketSender;

public class PlayerTablist {
    private Bar[] bars = new Bar[80];

    private Player player;

    public PlayerTablist(Player player) {
        this.player = player;
    }

    public void updateBar(int index, @Nonnull TabEntry entry) {
        Validate.isTrue((index <= 79), "Bar index cannot be higher than 79!");
        Validate.isTrue((index >= 0), "Bar index cannot be smaller than 0!");
        Tablist.executorService.submit(() -> {
            try {
                Bar bar = this.bars[index];
                if (bar.setHead(entry.head)) {
                    Object packet = BarPacketFactory.getPacket("ADD_PLAYER", new Bar[] { bar });
                    PacketSender.getInstance().send(this.player, packet);
                } else {
                    if (bar.setText(entry.text)) {
                        Object packet = BarPacketFactory.getPacket("UPDATE_DISPLAY_NAME", new Bar[] { bar });
                        PacketSender.getInstance().send(this.player, packet);
                    }
                    if (bar.setPing(entry.ping)) {
                        Object packet = BarPacketFactory.getPacket("UPDATE_LATENCY", new Bar[] { bar });
                        PacketSender.getInstance().send(this.player, packet);
                    }
                }
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE,
                        "An error occurred while updating bar " + index + " for player " + this.player.getName() + ".",
                        e);
            }
        });
    }

    public void sendTablist(@Nullable String header, @Nullable String footer, @Nonnull TabEntry[] entries) {
        Tablist.executorService.submit(() -> {
            try {
                Validate.isTrue((entries.length == 80), "entries table must have 80 objects!");
                for (int i = 0; i < 80; i++) {
                    TabEntry entry = entries[i];
                    Bar bar = new Bar(TablistManager.BARS[i], i, entry.text, entry.ping, entry.head);
                    this.bars[i] = bar;
                }
                Object packet = BarPacketFactory.getPacket("ADD_PLAYER", this.bars);
                PacketSender.getInstance().send(this.player, packet);
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE,
                        "An error occurred while sending tablist for player " + this.player.getName() + ".", e);
            }
        });
    }

    @Nonnull
    public Player getPlayer() {
        return this.player;
    }

    @Nonnull
    public Bar[] getBars() {
        return this.bars;
    }

    public void removeTablist() {
        Tablist.executorService.submit(() -> {
            try {
                Object packet = BarPacketFactory.getPacket("REMOVE_PLAYER", this.bars);
                PacketSender.getInstance().send(this.player, packet);
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE,
                        "An error occurred while removing tablist for player " + this.player.getName() + ".", e);
            }
        });
    }
}