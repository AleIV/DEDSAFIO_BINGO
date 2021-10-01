package me.aleiv.core.paper.tablist.packets;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import me.aleiv.core.paper.tablist.gameprofile.Bar;
import me.aleiv.core.paper.tablist.reflections.ServerVersion;

public class BarPacketFactory {
    private static Field INFO_LIST_FIELD;

    private static Class ENUMPLAYERINFOACTION_CLASS;

    private static Class ENTITYPLAYER_CLASS;

    private static Constructor PACKETPLAYOUTPLAYERINFO_CONSTRUCTOR;

    private static Constructor PLAYERINFODATA_CONSTRUCTOR;

    private static Object GAMEMODE_NOT_SET;

    private static Method FROM_STRING_OR_NULL;

    public static void loadInfoField() throws Exception {
        if (INFO_LIST_FIELD == null) {
            Class<?> clazz = Class.forName("net.minecraft.server." + ServerVersion.get() + ".PacketPlayOutPlayerInfo");
            INFO_LIST_FIELD = clazz.getDeclaredField("b");
            INFO_LIST_FIELD.setAccessible(true);
            ENTITYPLAYER_CLASS = Class.forName("net.minecraft.server." + ServerVersion.get() + ".EntityPlayer");
            Class[] classes = clazz.getClasses();
            for (Class clazzz : classes) {
                Constructor[] constructors = (Constructor[]) clazzz.getConstructors();
                if (clazzz.getSimpleName().equals("PlayerInfoData")) {
                    for (Constructor constructor : constructors) {
                        if (constructor.getParameterCount() == 5) {
                            PLAYERINFODATA_CONSTRUCTOR = constructor;
                            PLAYERINFODATA_CONSTRUCTOR.setAccessible(true);
                            break;
                        }
                    }
                } else if (clazzz.getSimpleName().equals("EnumPlayerInfoAction")) {
                    ENUMPLAYERINFOACTION_CLASS = clazzz;
                }
            }
            PACKETPLAYOUTPLAYERINFO_CONSTRUCTOR = clazz.getDeclaredConstructor(
                    new Class[] { ENUMPLAYERINFOACTION_CLASS, Array.newInstance(ENTITYPLAYER_CLASS, 0).getClass() });
            PACKETPLAYOUTPLAYERINFO_CONSTRUCTOR.setAccessible(true);
            Object[] enums = Class.forName("net.minecraft.server." + ServerVersion.get() + ".EnumGamemode")
                    .getEnumConstants();
            for (Object enumm : enums) {
                if (enumm.toString().equalsIgnoreCase("NOT_SET"))
                    GAMEMODE_NOT_SET = enumm;
            }
            if (GAMEMODE_NOT_SET == null)
                throw new Exception(
                        "Cannot find enum net.minecraft.server." + ServerVersion.get() + ".EnumGamemode.NOT_SET");
            FROM_STRING_OR_NULL = Class
                    .forName("org.bukkit.craftbukkit." + ServerVersion.get() + ".util.CraftChatMessage")
                    .getDeclaredMethod("fromStringOrNull", new Class[] { String.class });
            FROM_STRING_OR_NULL.setAccessible(true);
        }
    }

    public static Object getEnumPlayerInfoAction(String action) {
        Object[] enums = ENUMPLAYERINFOACTION_CLASS.getEnumConstants();
        for (Object enumm : enums) {
            if (enumm.toString().equalsIgnoreCase(action))
                return enumm;
        }
        return null;
    }

    public static Object getPacket(String action, Bar... bars) throws Exception {
        Object actionEnum = getEnumPlayerInfoAction(action);
        Object packet = PACKETPLAYOUTPLAYERINFO_CONSTRUCTOR
                .newInstance(new Object[] { actionEnum, Array.newInstance(ENTITYPLAYER_CLASS, 0) });
        List<Object> playerInfoDataList = (List<Object>) INFO_LIST_FIELD.get(packet);
        for (Bar bar : bars) {
            playerInfoDataList.add(
                    PLAYERINFODATA_CONSTRUCTOR.newInstance(new Object[] { packet, bar, Integer.valueOf(bar.getPing()),
                            GAMEMODE_NOT_SET, FROM_STRING_OR_NULL.invoke(null, new Object[] { bar.getText() }) }));
        }
        return packet;
    }
}