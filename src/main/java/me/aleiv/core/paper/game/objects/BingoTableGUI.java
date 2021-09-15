package me.aleiv.core.paper.game.objects;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import lombok.Data;
import us.jcedeno.libs.rapidinv.RapidInv;

@Data
public class BingoTableGUI {
    RapidInv gui;

    Slot[][] board = new Slot[5][5];

    static List<Integer> positions = List.of(2,3,4,5,6,11,12,13,14,15,20,21,22,23,24,29,30,31,32,33,38,39,40,41,42);

    public BingoTableGUI(Table table){
        this.gui = new RapidInv(6*9, "test");
        
        var count = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                var slot = table.getBoard()[i][j];
                ItemStack item;
                if(slot instanceof ChallengeSlot){
                    var challengeSlot = (ChallengeSlot) slot;
                    item = challengeSlot.getItem();
                }else{
                    item = slot.getItem();
                }
                gui.setItem(positions.get(count), item, handler ->{
                    handler.setCancelled(true);
                });
                count++;
            }
        }
    }
    
}
