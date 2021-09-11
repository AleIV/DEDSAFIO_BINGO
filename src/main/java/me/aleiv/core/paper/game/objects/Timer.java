package me.aleiv.core.paper.game.objects;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import lombok.Data;
import me.aleiv.core.paper.Core;

@Data
public class Timer {

    int startTime;
    int seconds;

    BossBar bossbar;

    public Timer(Core instance, int seconds){
        this.seconds = seconds;
        this.startTime = (int) instance.getGame().getGameTime();
        this.bossbar = Bukkit.createBossBar(new NamespacedKey(instance, "TIMER"), timeConvert(seconds), BarColor.WHITE, BarStyle.SOLID);
        bossbar.setVisible(true);

        Bukkit.getOnlinePlayers().forEach(player ->{
            bossbar.addPlayer(player);
        });
        
    }

    public static String timeConvert(int t) {
        int hours = t / 3600;

        int minutes = (t % 3600) / 60;
        int seconds = t % 60;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
    }


    public void refresh(long currentTime){
        var time = (startTime + seconds) - currentTime;

        if(time < 0){
            bossbar.setVisible(false);
            Core.getInstance().getGame().setTimer(null);

        }else{
            bossbar.setTitle(timeConvert((int) time));

        }
    }




}
