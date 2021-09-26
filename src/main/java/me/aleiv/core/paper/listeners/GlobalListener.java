package me.aleiv.core.paper.listeners;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
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

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.GameStage;
import me.aleiv.core.paper.events.GameStartedEvent;
import me.aleiv.core.paper.events.GameTickEvent;
import me.aleiv.core.paper.game.BingoManager;
import me.aleiv.core.paper.game.objects.Table;
import me.aleiv.core.paper.utilities.FastBoard;

public class GlobalListener implements Listener {

    Core instance;

    HashMap<UUID, UUID> enderEyes; 
    Random random = new Random();

    public GlobalListener(Core instance) {
        this.instance = instance;
        this.enderEyes = new HashMap<>();
    }



    @EventHandler
    public void entitySpawn(EntitySpawnEvent e){
        var entity = e.getEntity();

        if(entity instanceof EnderSignal ender){

            var players = ender.getLocation().getNearbyPlayers(1).stream().toList();
            if(!players.isEmpty()){
                final var player = players.get(0);
                var uuid = player.getUniqueId();
                var enderUuid = ender.getUniqueId();
                enderEyes.put(enderUuid, uuid);

                Bukkit.getScheduler().runTaskLater(instance, task ->{
                    var entitys = ender.getLocation().getNearbyEntities(3, 3, 3).stream()
                        .filter(ent -> ent instanceof Item item && item.getItemStack().getType() == Material.ENDER_EYE).toList();
    
                    if(entitys.isEmpty()){
                        
                        //this player break a ender eye
                        if(enderEyes.containsKey(enderUuid)){
                            var manager = instance.getScatterManager();
                            var world = Bukkit.getWorld("world_the_end");
                            var loc = genLoc(world);
                            loc.setY(100);
                            manager.Qteleport(player, loc);
                            var dragonLoc = genLoc(world);
                            world.spawnEntity(dragonLoc, EntityType.ENDER_DRAGON);
                        }
    
                    }
    
                }, 20*5);
            }

        }
    }

    public int getR(int i){
        var neg = random.nextBoolean();
        var rand = random.nextInt(i)+1;
        return neg ? rand*1 : rand*-1;
    }

    public Location genLoc(final World world){
        var worldBorder = 80;
        var loc = new Location(world, getR(worldBorder), 0, getR(worldBorder));
        loc.setY(world.getHighestBlockYAt(loc));
        return loc;
    }

    @EventHandler
    public void onCreature(EntityDamageByEntityEvent e){
        var entity = e.getEntity();
        if(entity instanceof EnderDragon dragon){
            if(e.getDamager() instanceof Player player && player.hasPermission("admin.perm")) return;
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

        instance.sendHeader(player, "<gradient:#5e4fa2:#f79459>Welcome to BINGOOOO!</gradient>");

        var timer = game.getTimer();
        timer.getBossbar().addPlayer(player);

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
    public void onRespawn(PlayerRespawnEvent e){
        var player = e.getPlayer();
        var lobby = Bukkit.getWorld("lobby");
        if(player.getWorld() == lobby){
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
            
        }else if(game.getGameStage() == GameStage.INGAME){
            player.setHealth(20);
            player.setGameMode(GameMode.SPECTATOR);

            var loc = scatter.generateLocation();
            Bukkit.getScheduler().runTaskLater(instance, task ->{
                scatter.Qteleport(player, loc);
                player.getInventory().clear();
                player.setHealth(20.0);
                player.setFoodLevel(20);
            }, 20*BingoManager.respawnSeconds);

        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        var entity = e.getEntity();
        if(entity instanceof Player player && player.getGameMode() == GameMode.SPECTATOR && e.getCause() == DamageCause.VOID){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onStart(GameStartedEvent e) {

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.getInventory().clear();
            player.setHealth(20.0);
            player.setFoodLevel(20);
        });

        Bukkit.getWorlds().forEach(world ->{
            world.setTime(0L);
            
        });

    }

}
