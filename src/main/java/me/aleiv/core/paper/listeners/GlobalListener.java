package me.aleiv.core.paper.listeners;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.GameStage;
import me.aleiv.core.paper.events.GameStartedEvent;
import me.aleiv.core.paper.events.GameTickEvent;
import me.aleiv.core.paper.game.BingoManager;
import me.aleiv.core.paper.game.objects.Table;
import me.aleiv.core.paper.utilities.FastBoard;
import net.md_5.bungee.api.ChatColor;

public class GlobalListener implements Listener {

    Core instance;

    HashMap<UUID, UUID> enderEyes;
    Random random = new Random();

    public GlobalListener(Core instance) {
        this.instance = instance;
        this.enderEyes = new HashMap<>();
    }

    @EventHandler
    public void entitySpawn(EntitySpawnEvent e) {
        var entity = e.getEntity();

        if (entity instanceof EnderSignal ender) {

            var players = ender.getLocation().getNearbyPlayers(1).stream().toList();
            if (!players.isEmpty()) {
                final var player = players.get(0);
                var uuid = player.getUniqueId();
                var enderUuid = ender.getUniqueId();
                enderEyes.put(enderUuid, uuid);

                Bukkit.getScheduler().runTaskLater(instance, task -> {

                    var manager = instance.getScatterManager();
                    var world = Bukkit.getWorld("world_the_end");
                    var loc = genLoc(world);
                    loc.setY(100);
                    manager.Qteleport(player, loc);

                }, 20 * 5);

            }

        }
    }

    public int getR(int i) {
        var neg = random.nextBoolean();
        var rand = random.nextInt(i) + 1;
        return neg ? rand * 1 : rand * -1;
    }

    public Location genLoc(final World world) {
        var worldBorder = 80;
        var loc = new Location(world, getR(worldBorder), 0, getR(worldBorder));
        loc.setY(world.getHighestBlockYAt(loc));
        return loc;
    }

    @EventHandler
    public void onCreature(EntityDamageByEntityEvent e) {
        var entity = e.getEntity();
        if (entity instanceof EnderDragon dragon) {
            if (e.getDamager()instanceof Player player && player.hasPermission("admin.perm"))
                return;
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void customSpawns(GameTickEvent e) {
        var game = instance.getGame();
        var boards = game.getBoards();

        Bukkit.getScheduler().runTask(instance, () -> {
            for (var board : boards.values()) {
                var uuid = board.getPlayer().getUniqueId();
                var table = instance.getBingoManager().findTable(uuid);

                instance.getBingoManager().updateBoard(board, table);
            }

            var timer = game.getTimer();
            if (timer.isActive()) {
                var currentTime = (int) game.getGameTime();
                timer.refreshTime(currentTime);

            }

            if(e.getSecond() % 5 == 0){
                Bukkit.getOnlinePlayers().forEach(player ->{
                    instance.sendHeader(player, getPlayerHeader(player));
                });
            }

        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();
        var uuid = player.getUniqueId().toString();
        var game = instance.getGame();

        var boards = game.getBoards();
        var board = new FastBoard(player);
        var manager = instance.getBingoManager();

        var table = manager.findTable(player.getUniqueId());

        if (table == null) {
            board.updateTitle(Table.getNullTitle());

        } else {
            board.updateTitle(table.getTitle());
        }

        instance.getBingoManager().updateBoard(board, table);
        boards.put(uuid, board);

        var timer = game.getTimer();
        timer.getBossbar().addPlayer(player);

        instance.sendHeader(player, getPlayerHeader(player));

    }

    public String getPlayerHeader(Player player){
        var manager = instance.getTeamManager();
        var header = new StringBuilder();
        var team = manager.getPlayerTeam(player.getUniqueId());

        if(team != null){
            var points = team.getPoints() == null || team.getPoints() < 1 ? 0 : team.getPoints();
            header.append("\n");
            header.append(team.getTeamName() + " ");
            header.append(ChatColor.GOLD + "" + points + Table.getStar() + " ");
            var uuids = team.getMembers();
            for (UUID uuid : uuids){
                var p = Bukkit.getPlayer(uuid);
                if(p != null){
                    header.append(ChatColor.DARK_RED + p.getName() + " ");
                }
            }
        }
        

        return header.toString();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        var player = e.getPlayer();
        var uuid = player.getUniqueId().toString();

        var boards = instance.getGame().getBoards();
        var board = boards.remove(uuid);

        if (board != null) {
            board.delete();
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        var player = e.getPlayer();
        var lobby = Bukkit.getWorld("lobby");
        if (player.getWorld() == lobby) {
            var loc = new Location(lobby, 0.5, 126, 0.5, 90, -0);
            e.setRespawnLocation(loc);
        }
    }

    @EventHandler
    public void onRespawn(PlayerDeathEvent e) {
        var player = e.getEntity();
        var lobby = Bukkit.getWorld("lobby");
        var game = instance.getGame();
        var scatter = instance.getScatterManager();

        if (player.getWorld() == lobby) {
            var loc = new Location(lobby, 0.5, 126, 0.5, 90, -0);
            scatter.Qteleport(player, loc);

        } else if (game.getGameStage() == GameStage.INGAME) {
            player.setHealth(20);
            player.setGameMode(GameMode.SPECTATOR);
            player.getInventory().clear();
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setExp(0);
            player.setLevel(0);

            var loc = scatter.generateLocation();
            Bukkit.getScheduler().runTaskLater(instance, task -> {
                scatter.Qteleport(player, loc);
                player.getActivePotionEffects().forEach(all -> player.removePotionEffect(all.getType()));
            }, 20 * BingoManager.respawnSeconds);

        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        var entity = e.getEntity();
        if (entity instanceof Player player && player.getGameMode() == GameMode.SPECTATOR
                && e.getCause() == DamageCause.VOID) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onStart(GameStartedEvent e) {

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setHealth(20.0);
            player.getInventory().clear();
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setExp(0);
            player.setLevel(0);
            player.getActivePotionEffects().forEach(all -> player.removePotionEffect(all.getType()));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 20, 20));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20 * 5, 20));
        });

        Bukkit.getWorlds().forEach(world -> {
            world.setTime(0L);

        });

        var manager = instance.getBingoManager();
        manager.selectTables();
        instance.getGame().setGameStage(GameStage.INGAME);
        instance.broadcastMessage(ChatColor.of("#74ebfb") + "Game started!");

    }

    @EventHandler
    public void onFriendlyFire(EntityDamageByEntityEvent e){
        var damaged = e.getEntity();
        var entity = e.getDamager();
        if(damaged instanceof Player playerDamaged){
            var manager = instance.getBingoManager();
            var table = manager.findTable(playerDamaged.getUniqueId());
            if(table != null){
                if(entity instanceof Player player){
                    if(!table.isPlaying(player.getUniqueId()))
                        e.setCancelled(true);
                    
                }else if(entity instanceof Projectile proj && proj.getShooter() != null && proj.getShooter() instanceof Player player){
                    if(!table.isPlaying(player.getUniqueId()))
                        e.setCancelled(true);
                    
                }
            }
        }
    }

}
