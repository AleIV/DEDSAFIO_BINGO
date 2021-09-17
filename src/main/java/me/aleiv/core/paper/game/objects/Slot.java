package me.aleiv.core.paper.game.objects;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.Setter;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.utilities.NegativeSpaces;
import net.md_5.bungee.api.ChatColor;
import us.jcedeno.libs.rapidinv.ItemBuilder;

public class Slot {

    @Setter @Getter boolean isFound;
    @Getter ItemCode itemCode;

    Material material;


    public static String negativeSpace = NegativeSpaces.get(-18);
    
    public static String interrogation = Character.toString('\uEAA0');
    public static String normal = Character.toString('\uEAA2');
    public static String found = Character.toString('\uEAA3');

    public Slot(Core instance, Material material) {
        this.material = material;
        this.isFound = false;

        var game = instance.getGame();

        var itemCode = game.getMaterials().get(material);
        this.itemCode = itemCode;
    }

    public ItemStack getItem(){
        ItemStack item;
        var name = material.toString().replace("_", " ");

        item = new ItemBuilder(material).name(ChatColor.of("#1fb2db") + name)
                .meta(ItemMeta.class, meta -> meta.setCustomModelData(this.getItemCode().getCustomModelData())).flags(ItemFlag.HIDE_ATTRIBUTES).build();

        return item;
    }

    public String getDisplay(){
        int code = itemCode.getCode();
        String c = Character.toString(code);

        if(isFound){
            return found + negativeSpace + c;

        }else{
            return normal + negativeSpace + c;

        }
    }

    public static String getFakeDisplay(){
        return normal + negativeSpace + interrogation;
        
    }

}
