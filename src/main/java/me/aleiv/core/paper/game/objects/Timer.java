package me.aleiv.core.paper.game.objects;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import lombok.Getter;
import lombok.Setter;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.GameStage;
import me.aleiv.core.paper.utilities.Frames;

public class Timer {

    Core instance;

    int startTime;
    int seconds;

    String neg1;
    String neg2;

    @Getter BossBar bossbar;

    String time = "";

    @Setter @Getter boolean isActive = false;

    List<Character> clock1;
    List<Character> clock2;
    List<Character> clock3;

    int currentClock = 0;
    
    public Timer(Core instance, int currentTime){
        this.instance = instance;
        this.seconds = 0;
        this.startTime = (int) currentTime;
        this.bossbar = Bukkit.createBossBar(new NamespacedKey(instance, "TIMER"), "", BarColor.WHITE, BarStyle.SOLID);
        bossbar.setVisible(false);

        Bukkit.getOnlinePlayers().forEach(player ->{
            bossbar.addPlayer(player);
        });

        clock1 = Frames.getFramesCharsIntegers(120, 140);
        clock2 = Frames.getFramesCharsIntegers(120, 140);
        clock3 = Frames.getFramesCharsIntegers(120, 140);

        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {

            bossbar.setTitle(getClockFormat());

        }, 2L, 1L);
        
    }

    private String getClockFormat(){
        var time = (int) (startTime + seconds) - instance.getGame().getGameTime();

        if(currentClock >= clock1.size()){
            currentClock = 0;
        }

        var n = currentClock;
        currentClock++;

        var stringBuilder = new StringBuilder();
        if(time <= 600){
            var clock = clock2.get(n);
            stringBuilder.append(this.time + clock);

        }else if(time <= 60){
            var clock = clock3.get(n);
            stringBuilder.append(this.time + clock);

        }else{
            var clock = clock1.get(n);
            stringBuilder.append(this.time + clock);
        }

        return stringBuilder.toString();
    }

    private static String timeConvert(int t) {
        int hours = t / 3600;

        int minutes = (t % 3600) / 60;
        int seconds = t % 60;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
    }


    public void refreshTime(int currentTime){
        var time = (startTime + seconds) - currentTime;
        var game = instance.getGame();
        if(time < 0){
            delete();
            isActive = false;
            if(game.getGameStage() == GameStage.INGAME){
                instance.getBingoManager().restartGame();
            }

        }else{
            this.time = timeConvert((int) time);

        }
    }

    public void delete(){
        bossbar.setVisible(false);

    }

    public void start(int seconds, int startTime){
        this.seconds = seconds;
        this.startTime = (int) instance.getGame().getGameTime();
        this.isActive = true;
        bossbar.setVisible(true);
    }




}
