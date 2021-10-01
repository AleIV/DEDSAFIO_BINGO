package me.aleiv.core.paper.tablist.reflections;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PacketSender {
    private static volatile PacketSender INSTANCE;

    private static String VERSION = ServerVersion.get();

    private static final Object lock = new Object();

    private Method getHandle;

    private Field playerConnection;

    private Method sendPacket;

    public PacketSender() {
        try {
            this.getHandle = Class.forName("org.bukkit.craftbukkit." + VERSION + ".entity.CraftPlayer")
                    .getDeclaredMethod("getHandle", new Class[0]);
            this.getHandle.setAccessible(true);
            this.playerConnection = Class.forName("net.minecraft.server." + VERSION + ".EntityPlayer")
                    .getDeclaredField("playerConnection");
            this.playerConnection.setAccessible(true);
            this.sendPacket = Class.forName("net.minecraft.server." + VERSION + ".PlayerConnection").getDeclaredMethod(
                    "sendPacket", new Class[] { Class.forName("net.minecraft.server." + VERSION + ".Packet") });
            this.sendPacket.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException e) {
            Bukkit.getLogger().log(Level.SEVERE,
                    "Is it unsupported version? Send this error to me on spigotmc.org (private message - mnekos).", e);
        }
    }

    public static PacketSender getInstance() {
        if (INSTANCE != null)
            return INSTANCE;
        synchronized (lock) {
            if (INSTANCE == null)
                INSTANCE = new PacketSender();
            return INSTANCE;
        }
    }

    public void send(Player player, Object packet) {
        try {
            Object entityPlayer = this.getHandle.invoke(player, new Object[0]);
            Object playerConnectionObject = this.playerConnection.get(entityPlayer);
            this.sendPacket.invoke(playerConnectionObject, new Object[] { packet });
        } catch (IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            Bukkit.getLogger().log(Level.SEVERE,
                    "Is it unsupported version? Send this error to me on spigotmc.org (private message - mnekos).", e);
        }
    }
}
