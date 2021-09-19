package me.aleiv.core.paper.game.objects;

import lombok.Data;

@Data
public class ItemCode {
    
    int code;
    int customModelData;
    String description;
    String name;

    public ItemCode(int code){
        this.code = code;
    }

    public ItemCode(int code, int customModelData){
        this.code = code;
        this.customModelData = customModelData;
    }

    public ItemCode(int code, int customModelData, String description){
        this.code = code;
        this.customModelData = customModelData;
        this.description = description;
    }

    public ItemCode(int code, int customModelData, String name, String description){
        this.code = code;
        this.customModelData = customModelData;
        this.name = name;
        this.description = description;
    }
}
