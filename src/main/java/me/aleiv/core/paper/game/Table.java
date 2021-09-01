package me.aleiv.core.paper.game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import lombok.Data;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.events.BingoEvent;
import net.md_5.bungee.api.ChatColor;

@Data
public class Table implements Cloneable{

    Core instance;
    
    UUID uuid;
    List<Material> selectedItems;
    Slot[][] board = new Slot[5][5];
    
    public Table(){
        this.uuid = UUID.randomUUID();
        this.selectedItems = new ArrayList<>();

    }

    public void checkBingoFull(){
        
        var game = instance.getGame();

        if(game.isBingoTypeFull() && isBingoFull()){
            //FULL BINGO CASE

            List<String> winners = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(player ->{

                if(player.getUniqueId().toString() == uuid.toString()){
                    winners.add(player.getName());
                }    
            });
            

            Bukkit.getOnlinePlayers().forEach(player ->{
                if(player.getUniqueId().toString() == this.uuid.toString()){
                    instance.broadcastMessage(ChatColor.of(game.getColor2()) + player.getName() + " WON!");
                }
            });
    
            Bukkit.getScheduler().runTaskAsynchronously(instance, task ->{
                Bukkit.getPluginManager().callEvent(new BingoEvent(winners, true));
            });

        }else if(!game.isBingoTypeFull() && isBingoLine()){
            //ONE LINE CASE

            List<String> winners = new ArrayList<>();
            winners.add(uuid.toString());

            Bukkit.getOnlinePlayers().forEach(player ->{
                if(player.getUniqueId().toString() == this.uuid.toString()){
                    instance.broadcastMessage(ChatColor.of(game.getColor2()) + player.getName() + " WON!");
                }
            });

            Bukkit.getScheduler().runTaskAsynchronously(instance, task ->{
                Bukkit.getPluginManager().callEvent(new BingoEvent(winners, true));
            });
        }

    }

    public boolean isBingoFull(){
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {

                var slot = board[i][j];
                if(!slot.isFound){
                    return false;
                }
                
            }
        }
        return true;
    }

    public boolean isBingoLine() {

        var length = board[0].length;
        var width = board.length;

        for (var i = 0; i < length; i++) {
            if (checkColumn(board, i)) {
                instance.broadcastMessage("Column " + i + " is the same");
                return true;
            }
        }

        for (var i = 0; i < width; i++) {
            if (checkRow(board, i)) {
                instance.broadcastMessage("Row " + i + " is the same");
                return true;
            }
        }

        if (checkDiagonal(board)) {
            
            instance.broadcastMessage("Diagonal is the same");
            return true;
        }

        return false;
    }

    // Method to check if all values in the column are the same
    public boolean checkColumn(final Slot[][] matrix, final int column) {
        var width = matrix.length;
        for (var i = 1; i < width; i++) {
            if (!matrix[i][column].isFound()) {
                return false;
            }
        }
        return true;
    }

    // Method to check if all values in the row are the same
    public boolean checkRow(final Slot[][] matrix, final int row) {
        var length = matrix[0].length;
        for (var i = 1; i < length; i++) {
            if (!matrix[row][i].isFound()) {
                return false;
            }
        }
        return true;
    }

    // Check if all values in the diagonal are the same
    public boolean checkDiagonal(final Slot[][] matrix) {
        var length = matrix[0].length;
        var matchOne = true;

        for (var i = 1; i < length+1; i++) {
            if (!matrix[i-1][i-1].isFound()) {
                matchOne = false;
                break;
            }
        }

        var matchTwo = true;

        for (var i = 1; i < length+1; i++) {
            if (!matrix[length - i][i - 1].isFound()) {
                matchTwo = false;
                break;
            }
        }

        return matchOne || matchTwo;
    }

    public String getPosDisplay(int x, int z){
        var slot = board[x][z];
        return slot.getDisplay();
    }

    public void sendTableDisplay(CommandSender sender){
                    
        String[] message = {
        " " + getPosDisplay(0, 0) + getPosDisplay(0, 1) + getPosDisplay(0, 2) + getPosDisplay(0, 3) + getPosDisplay(0, 4) + "\n",
        " " + "\n",
        " " + getPosDisplay(1, 0) + getPosDisplay(1, 1) + getPosDisplay(1, 2) + getPosDisplay(1, 3) + getPosDisplay(1, 4) + "\n",
        " " + "\n",
        " " + getPosDisplay(2, 0) + getPosDisplay(2, 1) + getPosDisplay(2, 2) + getPosDisplay(2, 3) + getPosDisplay(2, 4) + "\n",
        " " + "\n",
        " " + getPosDisplay(3, 0) + getPosDisplay(3, 1) + getPosDisplay(3, 2) + getPosDisplay(3, 3) + getPosDisplay(3, 4) + "\n",
        " " + "\n",
        " " + getPosDisplay(4, 0) + getPosDisplay(4, 1) + getPosDisplay(4, 2) + getPosDisplay(4, 3) + getPosDisplay(4, 4) + "\n",
        " " + "\n",
        " " + "\n"
        };

        sender.sendMessage(message);
    }
    
}
