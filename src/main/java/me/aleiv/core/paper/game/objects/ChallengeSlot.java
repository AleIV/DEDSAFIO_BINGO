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

    private List<Challenge> nonInfoLore = List.of(Challenge.BREAK_RULE_1);

    public ChallengeSlot(Core instance, Challenge challenge) {
        super(Material.PAPER);
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
        var name = (isFound ? ChatColor.of("#f58700") + "DEDsafío completado": ChatColor.of("#00e0f5") + "DEDsafío");
        var code = this.getItemCode().getCustomModelData();

        var item = new ItemBuilder(material).name(name).meta(ItemMeta.class, meta -> meta.setCustomModelData(code)).flags(ItemFlag.HIDE_ATTRIBUTES);

        var description = formatDescription(itemCode.getDescription(), 5);

        for (var desc : description) {
            item.addLore(ChatColor.WHITE + desc);
        }

        if(!nonInfoLore.contains(challenge)){
            if(!infoPlayers.isEmpty()){
                item.addLore(" ");
                for (String string : infoPlayers) {
                    item.addLore(ChatColor.of("#ffee2e") + string);
                }
            }
    
            if(!challengeInfo.isEmpty()){
                item.addLore(" ");
                for (String string : challengeInfo) {
                    item.addLore(ChatColor.of("#ffee2e") + formatName(string));
                }
            }
        }

        return item.build();
    }

}
