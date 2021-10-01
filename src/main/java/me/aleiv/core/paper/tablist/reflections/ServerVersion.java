package me.aleiv.core.paper.tablist.reflections;

import org.bukkit.Bukkit;

public class ServerVersion {
    private static String VERSION = Bukkit.getServer().getClass().getPackage().getName()
            .substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf(".") + 1);

    public static String get() {
        return VERSION;
    }
}
