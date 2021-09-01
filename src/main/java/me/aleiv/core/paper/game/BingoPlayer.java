package me.aleiv.core.paper.game;

import lombok.Data;

@Data
public class BingoPlayer {

    String uuid;
    Table table;
    
    public BingoPlayer(String uuid){
        this.uuid = uuid;
        this.table = null;
        
    }
}
