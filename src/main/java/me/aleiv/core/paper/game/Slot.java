package me.aleiv.core.paper.game;

import org.bukkit.Material;

import lombok.Data;
import me.aleiv.core.paper.Core;

@Data
public class Slot {

    boolean isFound;
    ItemCode itemCode;
    Material material;
    public static String negativeSpace;
    
    public static String twitch = Character.toString('\uE007');
    public static String normal = Character.toString('\uE008');
    public static String found = Character.toString('\uE009');


    public Slot(Material material) {
        this.material = material;
        this.isFound = false;

        var instance = Core.getInstance();
        var game = instance.getGame();

        if(!game.getMaterials().containsKey(material)){
            instance.broadcastMessage("DOESNT CONTAIN " + material.toString());
        }

        var itemCode = game.getMaterials().get(material);

        this.itemCode = itemCode;

        negativeSpace = instance.getNegativeSpaces().get(-18);
    }

    public String getDisplay(){
        var game = Core.getInstance().getGame();

        int code = itemCode.getCode();
        String c = Character.toString(code);

        if(isFound){
            return found + negativeSpace + c;

        }else if(game.isNormalMode()){
            return normal + negativeSpace + c;

        }else{
            return twitch + negativeSpace + c;
        }
        
    }

}
