package me.aleiv.core.paper.game.objects;

import org.bukkit.Material;

import lombok.Getter;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.Challenge;

public class ChallengeSlot extends Slot{
    
    @Getter Challenge challenge;

    public ChallengeSlot(Core instance, Challenge challenge) {
        super(instance, Material.PAPER);
        this.challenge = challenge;
        this.isFound = false;

        var game = instance.getGame();

        var itemCode = game.getChallenges().get(challenge);
        this.itemCode = itemCode;
    }

}
