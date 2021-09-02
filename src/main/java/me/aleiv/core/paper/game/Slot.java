package me.aleiv.core.paper.game;

import org.bukkit.Material;

import lombok.Data;
import me.aleiv.core.paper.Core;

@Data
public class Slot {

    Core instance;
    boolean isFound;
    ItemCode itemCode;
    Material material;
    public static String negativeSpace;
    
    public static String twitch = Character.toString('\uE007');
    public static String normal = Character.toString('\uE008');
    public static String found = Character.toString('\uE009');


    public Slot(Core instance, Material material) {
        this.instance = instance;
        this.material = material;
        this.isFound = false;

        var game = instance.getGame();
        var itemCode = game.getMaterials().get(material);

        this.itemCode = itemCode;

        negativeSpace = instance.getNegativeSpaces().get(-18);
    }

    public String getDisplay(){
        var game = instance.getGame();

        int code = itemCode.getCode();
        String c = Character.toString(code);

        if(isFound){
            return c + negativeSpace + found;

        }else if(game.isNormalMode()){
            return c + negativeSpace + normal;

        }else{
            return c + negativeSpace + twitch;
        }
        
    }

}
