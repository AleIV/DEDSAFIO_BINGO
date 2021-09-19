package me.aleiv.core.paper.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.Challenge;

public class ChallengeEASY implements Listener{
    
    Core instance;

    public ChallengeEASY(Core instance){
        this.instance = instance;
    }

    @EventHandler
    public void onJump(PlayerJumpEvent e) {
        var block = e.getPlayer().getLocation().getBlock().getType().toString();

        if(!block.contains("BED")) return;

        var manager = instance.getBingoManager();

        var player = e.getPlayer();
        var table = manager.findTable(player.getUniqueId());

        if(table != null && table.getSelectedChallenge().contains(Challenge.JUMP_BED)){

            manager.attempToFind(player, Challenge.JUMP_BED);

        }

    }
}
