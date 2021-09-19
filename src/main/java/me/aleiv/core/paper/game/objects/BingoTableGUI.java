package me.aleiv.core.paper.game.objects;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import lombok.Data;
import us.jcedeno.libs.rapidinv.RapidInv;

@Data
public class BingoTableGUI {
    RapidInv gui;

    public static String neg3 = "";
    public static String neg4 = "";

    private static String guiUnicode = Character.toString('\uE021');
    Slot[][] board = new Slot[5][5];

    private static List<String> foundRows = List.of(Character.toString('\uE022'), Character.toString('\uE023'),
         Character.toString('\uE024'), Character.toString('\uE025'), Character.toString('\uE026'));

    private static List<String> nonFoundRows = List.of(Character.toString('\uE027'), Character.toString('\uE028'),
         Character.toString('\uE029'), Character.toString('\uE030'), Character.toString('\uE031'));

    static List<Integer> positions = List.of(11,12,13,14,15,20,21,22,23,24,29,30,31,32,33,38,39,40,41,42,47,48,49,50,51);

    public BingoTableGUI(Table table){
        
        var guiName = new StringBuilder();
        guiName.append(neg4);
        var c = 1;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                var slot = table.getBoard()[i][j];
                if(slot.isFound()){
                    guiName.append(foundRows.get(j));
                }else{
                    guiName.append(nonFoundRows.get(j));
                }

            }

            for (int k = 0; k < c; k++) {
                guiName.append(neg3);
            }
            c++;
        }

        this.gui = new RapidInv(6*9, guiUnicode + guiName);
        
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
