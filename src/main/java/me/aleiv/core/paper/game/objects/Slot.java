package me.aleiv.core.paper.game.objects;

import org.bukkit.Material;

import lombok.Data;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.utilities.NegativeSpaces;

@Data
public class Slot {

    boolean isFound;
    ItemCode itemCode;
    Material material;
    public static String negativeSpace = NegativeSpaces.get(-18);
    
    public static String interrogation = Character.toString('\uEAA0');
    public static String normal = Character.toString('\uEAA2');
    public static String found = Character.toString('\uEAA3');
    


    public Slot(Material material) {
        this.material = material;
        this.isFound = false;

        var instance = Core.getInstance();
        var game = instance.getGame();

        var itemCode = game.getMaterials().get(material);
        this.itemCode = itemCode;
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
