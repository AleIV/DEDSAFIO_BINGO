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
import me.aleiv.core.paper.utilities.NegativeSpaces;
import net.md_5.bungee.api.ChatColor;
import us.jcedeno.libs.rapidinv.ItemBuilder;

public class Slot {

    static Core instance = Core.getInstance();
    @Setter @Getter boolean isFound;
    @Getter ItemCode itemCode;

    Material material;

    public @Setter @Getter static String negativeSpace = NegativeSpaces.get(-18);
    
    public static String interrogation = Character.toString('\uEAA0');
    public static String normal = Character.toString('\uEAA2');
    public static String found = Character.toString('\uEAA3');

    public Slot(Material material) {
        
        this.material = material;
        this.isFound = false;

        var game = instance.getGame();

        var itemCode = game.getMaterials().get(material);
        this.itemCode = itemCode;
    }

    public ItemStack getItem(){
        var name = (isFound ? ChatColor.of("#f58700") + "Obtenido" : ChatColor.of("#00e0f5") + "Encuentra");
        var code = this.getItemCode().getCustomModelData();

        var item = new ItemBuilder(material).meta(ItemMeta.class, meta -> meta.setCustomModelData(code)).flags(ItemFlag.HIDE_ATTRIBUTES);
        var lore = formatName(material.toString());

        item.name(name);
        item.addLore(ChatColor.WHITE + lore);

        return item.build();
    }

    public String formatName(String string){
        var str = string.toLowerCase();
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
        return newString.replace("_", " ");
    }

    public List<String> formatDescription(String string, int wordLimit){
        var description = string.split(" ");

        List<String> finalDesc = new ArrayList<>();
        var line = "";
        var count = 0;
        for (var str : description) {
            if(count < wordLimit){
                line += str + " ";
            }else{
                finalDesc.add(line);
                count = 0;
                line = "";
                line += str + " ";
            }
            count++;
        }
        finalDesc.add(line);

        return finalDesc;
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
