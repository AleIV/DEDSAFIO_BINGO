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

        var name = (isFound ? ChatColor.of("#f58700") : ChatColor.of("#00e0f5")) + formatName(material.toString()); 
        var code = this.getItemCode().getCustomModelData();

        var item = new ItemBuilder(material).name(name).meta(ItemMeta.class, meta -> meta.setCustomModelData(code)).flags(ItemFlag.HIDE_ATTRIBUTES).build();

        return item;
    }

    public String formatName(String string){
        var str = string;
        Core.getInstance().broadcastMessage(str);

        str.toLowerCase();
        return str;
        
        /*
        Core.getInstance().broadcastMessage(str);

        var array = str.toCharArray();

        var upper = true;
        var count = 0;
        
        for (char c : array) {
            if(upper){
                array[count] = Character.toUpperCase(c);
                upper = false;
            }else if(c == '_'){
                upper = true;
            }
            count++;
        }

        var newString = String.valueOf(array);

        Core.getInstance().broadcastMessage(newString);

        newString.replace("_", " ");

        Core.getInstance().broadcastMessage(newString);
        return newString;*/
    }

    public String getDisplay(){
        int code = itemCode.getCode();
        String c = Character.toString(code);

        if(isFound){
            return c + negativeSpace + found;

        }else{
            return c + negativeSpace + normal;

        }
    }

    public static String getFakeDisplay(){
        return normal + negativeSpace + interrogation;
        
    }

}
