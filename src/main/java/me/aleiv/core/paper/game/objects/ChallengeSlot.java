package me.aleiv.core.paper.game.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.Setter;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.Challenge;
import net.md_5.bungee.api.ChatColor;
import us.jcedeno.libs.rapidinv.ItemBuilder;

public class ChallengeSlot extends Slot{
    
    @Getter Challenge challenge;

    @Getter @Setter List<String> challengeInfo;
    @Getter @Setter List<String> infoPlayers;

    public ChallengeSlot(Core instance, Challenge challenge) {
        super(instance, Material.PAPER);
        this.challenge = challenge;
        this.isFound = false;
        this.challengeInfo = new ArrayList<>();
        this.infoPlayers = new ArrayList<>();

        var game = instance.getGame();

        var itemCode = game.getChallenges().get(challenge);
        this.itemCode = itemCode;
    }

    @Override
    public ItemStack getItem(){
        var name = (isFound ? ChatColor.of("#f58700") : ChatColor.of("#00e0f5")) + "Challenge"; 
        var code = this.getItemCode().getCustomModelData();

        var item = new ItemBuilder(material).name(name).meta(ItemMeta.class, meta -> meta.setCustomModelData(code)).flags(ItemFlag.HIDE_ATTRIBUTES);

        if(!infoPlayers.isEmpty()){
            for (String string : infoPlayers) {
                item.addLore(ChatColor.of("#94eaff") + string);
            }
        }

        item.addLore("\n");

        if(!challengeInfo.isEmpty()){
            for (String string : challengeInfo) {
                item.addLore(ChatColor.of("#94eaff") + string);
            }
        }

        return item.build();
    }

}
