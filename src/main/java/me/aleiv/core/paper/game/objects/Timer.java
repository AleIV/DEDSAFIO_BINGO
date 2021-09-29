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
import me.aleiv.core.paper.utilities.NegativeSpaces;
import net.md_5.bungee.api.ChatColor;

public class Timer {

    Core instance;

    int startTime;
    int seconds;

    static String neg1 = NegativeSpaces.get(-39);
    static String neg2 = NegativeSpaces.get(8);
    static String neg3 = NegativeSpaces.get(-48);
    static String neg4 = NegativeSpaces.get(24);
    

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

        clock1 = Frames.getFramesCharsIntegers(200, 219);
        clock2 = Frames.getFramesCharsIntegers(220, 239);
        clock3 = Frames.getFramesCharsIntegers(240, 259);

        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {

            bossbar.setTitle(getClockFormat());

        }, 1L, 1L);
        
    }

    private String getClockFormat(){
        var time = (int) (startTime + seconds) - instance.getGame().getGameTime();

        if(currentClock >= clock1.size()){
            currentClock = 0;
        }

        var n = currentClock;
        currentClock++;

        var stringBuilder = new StringBuilder();

        if(time <= 30){
            var clock = clock3.get(n);
            stringBuilder.append(clock + this.time);

        }else if(time <= 600){
            var clock = clock2.get(n);
            stringBuilder.append(clock + this.time);

        }else{
            var clock = clock1.get(n);
            stringBuilder.append(clock + this.time);
        }

        return stringBuilder.toString();
    }

    private static String timeConvert(int t) {
        int hours = t / 3600;

        int minutes = (t % 3600) / 60;
        int seconds = t % 60;

        return (hours > 0 ? neg1 + neg2 + String.format("%02d:%02d:%02d", hours, minutes, seconds)
        : neg3 + neg4 + String.format("%02d:%02d", minutes, seconds));
    }


    public int getTime(int currentTime){
        return (startTime + seconds) - currentTime;
    }

    public void setPreStart(int time){
        this.time = timeConvert(time);
        this.getBossbar().setVisible(true);

    }

    public void refreshTime(int currentTime){
        var time = (startTime + seconds) - currentTime;
        var game = instance.getGame();

        if(time == 30 && game.getGameStage() == GameStage.INGAME){
            Bukkit.getOnlinePlayers().forEach(player ->{
                var loc = player.getLocation();
                player.playSound(loc, "bingo.alarm", 1, 1);
            });
        }

        if(time < 0){
            this.time = neg3 + neg4 + "00:00";
            if(game.getGameStage() == GameStage.INGAME){
                game.setGameStage(GameStage.POSTGAME);
                instance.broadcastMessage(ChatColor.of(game.getColor3()) + "TIME IS UP!");

                //instance.getBingoManager().restartGame();
            }

        }else{
            this.time = timeConvert((int) time);

        }

        if(time < -10){
            delete();
            setActive(false);
        }

        
    }

    public void delete(){
        bossbar.setVisible(false);

    }

    public void start(int seconds, int startTime){
        this.time = timeConvert(seconds);
        this.seconds = seconds;
        this.startTime = (int) instance.getGame().getGameTime();
        this.isActive = true;
        bossbar.setVisible(true);
    }




}
