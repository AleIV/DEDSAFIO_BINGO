package me.aleiv.core.paper;

import java.time.Duration;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

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
import me.aleiv.core.paper.commands.ConfigCMD;
import me.aleiv.core.paper.commands.InfoCMD;
import me.aleiv.core.paper.game.BingoManager;
import me.aleiv.core.paper.game.ScatterManager;
import me.aleiv.core.paper.listeners.ChallengeEasy;
import me.aleiv.core.paper.listeners.ChallengeHard;
import me.aleiv.core.paper.listeners.ChallengeMedium;
import me.aleiv.core.paper.listeners.GlobalListener;
import me.aleiv.core.paper.listeners.InGameListener;
import me.aleiv.core.paper.listeners.LobbyListener;
import me.aleiv.core.paper.teams.bukkit.BTeamManager;
import me.aleiv.core.paper.teams.bukkit.commands.TeamCMD;
import me.aleiv.core.paper.utilities.JsonConfig;
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
    private @Getter BTeamManager teamManager;
    private @Getter ProtocolManager protocolManager;
    private @Getter ScatterManager scatterManager;
    private JsonConfig redisJsonConfig;

    @Override
    public void onEnable() {
        instance = this;

        protocolManager = ProtocolLibrary.getProtocolManager();
        RapidInvManager.register(this);
        BukkitTCT.registerPlugin(this);
        NegativeSpaces.registerCodes();

        // Obtain the secret connection string
        try {
            this.redisJsonConfig = new JsonConfig("secret.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
        var redisUri = redisJsonConfig != null ? redisJsonConfig.getRedisUri() : null;
        // Hook the team , ensure no nulls
        teamManager = new BTeamManager(this, redisUri != null ? redisUri : "redis://147.182.135.68");

        game = new Game(this);
        game.runTaskTimerAsynchronously(this, 0L, 20L);

        bingoManager = new BingoManager(this);
        scatterManager = new ScatterManager(this);

        // LISTENERS

        Bukkit.getPluginManager().registerEvents(bingoManager, this);
        Bukkit.getPluginManager().registerEvents(new GlobalListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InGameListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LobbyListener(this), this);

        Bukkit.getPluginManager().registerEvents(new ChallengeEasy(this), this);
        Bukkit.getPluginManager().registerEvents(new ChallengeMedium(this), this);
        Bukkit.getPluginManager().registerEvents(new ChallengeHard(this), this);

        commandManager = new PaperCommandManager(this);

        commandManager.registerCommand(new AdminBingoCMD(this));
        commandManager.registerCommand(new ConfigCMD(this));
        commandManager.registerCommand(new BingoCMD(this));
        commandManager.registerCommand(new InfoCMD(this));
        commandManager.registerCommand(new TeamCMD(this));


        Bukkit.getScheduler().runTaskLater(this, task -> {
            WorldCreator worldCreator = new WorldCreator("lobby");
            worldCreator.environment(Environment.THE_END);
            worldCreator.createWorld();

            Bukkit.getWorlds().forEach(world -> {
                world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
                world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
                world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
                world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
                world.getWorldBorder().setCenter(0, 0);
                world.getWorldBorder().setSize(6000);
            });
        }, 20);

    }

    @Override
    public void onDisable() {
        teamManager.disconect();

    }

    public void broadcastMessage(String text) {
        Bukkit.broadcast(miniMessage.parse(text));
    }

    public void sendActionBar(Player player, String text) {
        player.sendActionBar(miniMessage.parse(text));
    }

    public void showTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.showTitle(Title.title(miniMessage.parse(title), miniMessage.parse(subtitle), Times
                .of(Duration.ofMillis(50 * fadeIn), Duration.ofMillis(50 * stay), Duration.ofMillis(50 * fadeIn))));
    }

    public void sendHeader(Player player, String text) {
        player.sendPlayerListHeader(miniMessage.parse(text));
    }

    public void sendFooter(Player player, String text) {
        player.sendPlayerListFooter(miniMessage.parse(text));
    }

}