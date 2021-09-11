package me.aleiv.core.paper;

import java.time.Duration;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.collect.ImmutableList;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import kr.entree.spigradle.annotations.SpigotPlugin;
import lombok.Getter;
import me.aleiv.core.paper.commands.AdminBingoCMD;
import me.aleiv.core.paper.commands.BingoCMD;
import me.aleiv.core.paper.game.BingoManager;
import me.aleiv.core.paper.listeners.GlobalListener;
import me.aleiv.core.paper.listeners.InGameListener;
import me.aleiv.core.paper.listeners.LobbyListener;
import me.aleiv.core.paper.teams.TeamManager;
import me.aleiv.core.paper.utilities.NegativeSpaces;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import us.jcedeno.libs.rapidinv.RapidInvManager;

@SpigotPlugin
public class Core extends JavaPlugin {

    private static @Getter Core instance;
    private @Getter Game game;
    private @Getter PaperCommandManager commandManager;
    private @Getter static MiniMessage miniMessage = MiniMessage.get();
    private @Getter BingoManager bingoManager;
    private @Getter NegativeSpaces negativeSpaces;
    private @Getter TeamManager teamManager;
    private @Getter ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        instance = this;

        protocolManager = ProtocolLibrary.getProtocolManager();
        
        game = new Game(this);
        game.runTaskTimerAsynchronously(this, 0L, 20L);

        
        RapidInvManager.register(this);
        BukkitTCT.registerPlugin(this);

        bingoManager = new BingoManager(this);
        negativeSpaces = new NegativeSpaces();
        teamManager = new TeamManager(this);

        //LISTENERS

        Bukkit.getPluginManager().registerEvents(bingoManager, this);
        Bukkit.getPluginManager().registerEvents(new GlobalListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InGameListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LobbyListener(this), this);


        //COMMANDS
        
        commandManager = new PaperCommandManager(this);

        commandManager.getCommandCompletions().registerCompletion("stages", c -> {
            return ImmutableList.of("LOBBY", "STARTING", "INGAME", "POSTGAME");
        });

        commandManager.registerCommand(new AdminBingoCMD(this));
        commandManager.registerCommand(new BingoCMD(this));

        Bukkit.getScheduler().runTaskLater(this, task->{
            WorldCreator worldCreator = new WorldCreator("lobby");
            worldCreator.environment(Environment.NORMAL);
            worldCreator.createWorld();

            Bukkit.getWorlds().forEach(world ->{
                world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
                world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
                world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
                world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            });
        }, 20);

    }

    @Override
    public void onDisable() {

    }

    public void broadcastMessage(String text){
        Bukkit.broadcast(miniMessage.parse(text));
    }

    public void sendActionBar(Player player, String text){
        player.sendActionBar(miniMessage.parse(text));
    }

    public void showTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut){
        player.showTitle(Title.title(miniMessage.parse(title), miniMessage.parse(subtitle), Times.of(Duration.ofMillis(50*fadeIn), Duration.ofMillis(50*stay), Duration.ofMillis(50*fadeIn))));
    }

    public void sendHeader(Player player, String text){
        player.sendPlayerListHeader(miniMessage.parse(text));
    }

    public void sendFooter(Player player, String text){
        player.sendPlayerListFooter(miniMessage.parse(text));
    }

}