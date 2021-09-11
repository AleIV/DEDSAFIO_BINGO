package me.aleiv.core.paper.game.objects;

import lombok.Data;

@Data
public class Position {
    int x;
    int z;
    
    public Position(int x, int z){
        this.x = x;
        this.z = z;
    }

}
