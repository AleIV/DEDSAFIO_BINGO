package me.aleiv.core.paper.game.objects;

import lombok.Data;

@Data
public class ItemCode {
    
    int code;
    int customModelData;

    public ItemCode(int code){
        this.code = code;
    }

    public ItemCode(int code, int customModelData){
        this.code = code;
        this.customModelData = customModelData;
    }
}
