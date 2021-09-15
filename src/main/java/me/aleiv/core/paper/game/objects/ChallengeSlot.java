package me.aleiv.core.paper.game.objects;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.Challenge;
import net.md_5.bungee.api.ChatColor;
import us.jcedeno.libs.rapidinv.ItemBuilder;

public class ChallengeSlot extends Slot{
    
    Challenge challenge;

    public ChallengeSlot(Challenge challenge) {
        super(Material.AIR);
        this.challenge = challenge;
        this.isFound = false;

        var instance = Core.getInstance();
        var game = instance.getGame();

        var itemCode = game.getChallenges().get(challenge);
        this.itemCode = itemCode;
    }

    @Override
    public ItemStack getItem(){
        ItemStack item;
        var name = material.toString().replace("_", " ");

        if(isFound){
            item = new ItemBuilder(material).name(ChatColor.of("#38db1f") + name)
                .meta(ItemMeta.class, meta -> meta.setCustomModelData(this.getItemCode().getCustomModelData())).enchant(Enchantment.MENDING).flags(ItemFlag.HIDE_ATTRIBUTES).flags(ItemFlag.HIDE_ENCHANTS).build();
        }else{
            item = new ItemBuilder(material).name(ChatColor.of("#1fb2db") + name)
                .meta(ItemMeta.class, meta -> meta.setCustomModelData(this.getItemCode().getCustomModelData())).flags(ItemFlag.HIDE_ATTRIBUTES).build();
        }

        return item;
    }

}
