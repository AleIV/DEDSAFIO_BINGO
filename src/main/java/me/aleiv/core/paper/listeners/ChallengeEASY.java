package me.aleiv.core.paper.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.potion.PotionEffectType;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.BingoFase;
import me.aleiv.core.paper.Game.BingoRound;
import me.aleiv.core.paper.Game.Challenge;

public class ChallengeEasy implements Listener {

    Core instance;

    public ChallengeEasy(Core instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onShield(PlayerItemBreakEvent e) {
        var game = instance.getGame();
        if (game.getBingoFase() != BingoFase.CHALLENGE && game.getBingoRound() != BingoRound.ONE)
            return;

        var item = e.getBrokenItem();

        if (item.getType() == Material.SHIELD) {
            var manager = instance.getBingoManager();

            var player = e.getPlayer();
            var table = manager.findTable(player.getUniqueId());
            if (table != null) {

                manager.attempToFind(player, Challenge.SHIELD_BREAK, "");
            }

        }
    }

    @EventHandler
    public void onEntityPotionEffect(EntityPotionEffectEvent e) {
        var game = instance.getGame();
        if (game.getBingoFase() != BingoFase.CHALLENGE && game.getBingoRound() != BingoRound.ONE)
            return;

        if (e.getEntity() instanceof Player) {

            var manager = instance.getBingoManager();

            var player = (Player) e.getEntity();
            var table = manager.findTable(player.getUniqueId());

            var newEffect = e.getNewEffect();

            if(table == null || newEffect == null) return;

            var type = newEffect.getType();
            if (type.equals(PotionEffectType.DOLPHINS_GRACE)
                    && e.getCause().equals(EntityPotionEffectEvent.Cause.DOLPHIN)) {

                manager.attempToFind(player, Challenge.DOLPHIN_SWIM, "");

            }else if(type.equals(PotionEffectType.POISON)){
                manager.attempToFind(player, Challenge.GET_POISON, "");

            }
        }
    }

    @EventHandler
    public void onSkyHigh(PlayerJumpEvent e) {
        var game = instance.getGame();
        if (game.getBingoFase() != BingoFase.CHALLENGE && game.getBingoRound() != BingoRound.ONE)
            return;

        var manager = instance.getBingoManager();
        var player = e.getPlayer();
        var loc = player.getLocation();
        var table = manager.findTable(player.getUniqueId());

        if (loc.getY() >= 255 && table != null) {
            manager.attempToFind(player, Challenge.MAXIMUM_HEIGHT, "");
        }
    }

    @EventHandler
    public void growTree(StructureGrowEvent e) {
        var game = instance.getGame();
        if (game.getBingoFase() != BingoFase.CHALLENGE && game.getBingoRound() != BingoRound.ONE)
            return;

        var player = e.getPlayer();

        var manager = instance.getBingoManager();
        var table = manager.findTable(player.getUniqueId());

        if (player != null && table != null && player.getWorld().getEnvironment() == Environment.NETHER) {

            manager.attempToFind(player, Challenge.NETHER_TREE, "");

        }

    }

    @EventHandler
    public void onShootTarget(ProjectileHitEvent e) {
        var game = instance.getGame();
        if (game.getBingoFase() != BingoFase.CHALLENGE && game.getBingoRound() != BingoRound.ONE)
            return;

        var block = e.getHitBlock();
        if (block != null && block.getType() == Material.TARGET) {
            var proj = e.getEntity();

            if (proj.getShooter() != null && proj.getShooter()instanceof Player player) {
                var manager = instance.getBingoManager();
                var table = manager.findTable(player.getUniqueId());

                if (table != null) {

                    manager.attempToFind(player, Challenge.SHOOT_TARGET, "");

                }
            }
        }
    }

    @EventHandler
    public void entityDeath(EntityDeathEvent e) {
        var game = instance.getGame();
        if (game.getBingoFase() != BingoFase.CHALLENGE && game.getBingoRound() != BingoRound.ONE)
            return;

        var entity = e.getEntity();
        var player = entity.getKiller();

        if(player == null) return;

        var manager = instance.getBingoManager();
        var table = manager.findTable(player.getUniqueId());

        if(table == null) return;

        if (entity instanceof Monster monster) {

            manager.attempToFind(player, Challenge.HOSTILE_KILL, monster.getType().toString());

        }else if(entity instanceof Animals animal){

            manager.attempToFind(player, Challenge.ANIMAL_KILL, animal.getType().toString());

        }else if(entity instanceof Sheep sheep && sheep.getColor() == DyeColor.PINK){
            
            var biome = sheep.getLocation().getBlock().getBiome().toString();
            manager.attempToFind(player, Challenge.HOSTILE_KILL, biome);

        }
    }


    @EventHandler
    public void onJump(PlayerJumpEvent e) {
        var game = instance.getGame();
        if (game.getBingoFase() != BingoFase.CHALLENGE && game.getBingoRound() != BingoRound.ONE)
            return;

        var block = e.getPlayer().getLocation().getBlock().getType().toString();

        if (!block.contains("BED")) return;

        var manager = instance.getBingoManager();

        var player = e.getPlayer();
        var table = manager.findTable(player.getUniqueId());

        if (table != null) {
            //TODO: MAKE ALL TEAM JUMP
            manager.attempToFind(player, Challenge.JUMP_BED, "");
        }

    }

    @EventHandler
    public void creeperIgnite(PlayerInteractAtEntityEvent e){
        var game = instance.getGame();
        if (game.getBingoFase() != BingoFase.CHALLENGE && game.getBingoRound() != BingoRound.ONE)
            return;
        
        var player = e.getPlayer();
        var entity = e.getRightClicked();
        var hand = player.getEquipment().getItemInMainHand();
        if(entity instanceof Creeper creeper && hand != null && hand.getType() == Material.FLINT_AND_STEEL){
            var manager = instance.getBingoManager();
            var table = manager.findTable(player.getUniqueId());
            if(table != null){
                manager.attempToFind(player, Challenge.CREEPER_IGNITE, "");
            }
        }

    }

    
}
