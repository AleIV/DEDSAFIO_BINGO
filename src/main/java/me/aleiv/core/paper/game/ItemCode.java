package me.aleiv.core.paper.game;

import lombok.Data;

@Data
public class ItemCode {
    
    int code;

    public ItemCode(int code){
        this.code = code;
    }
}
