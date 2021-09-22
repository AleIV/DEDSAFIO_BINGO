package me.aleiv.core.paper.game.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.BingoFase;
import me.aleiv.core.paper.Game.BingoRound;
import me.aleiv.core.paper.Game.Challenge;
import me.aleiv.core.paper.utilities.Frames;
import me.aleiv.core.paper.utilities.NegativeSpaces;

public class Table{
    
    @Getter UUID uuid;
    @Getter List<Material> selectedItems;
    @Getter List<Challenge> selectedChallenge;

    @Getter Slot[][] board = new Slot[5][5];
    @Getter List<UUID> members;

    @Getter @Setter boolean foundLine;
    @Getter @Setter boolean foundFull;
    @Getter int objectsFound;

    private static String neg2 = NegativeSpaces.get(-8);
    private static String neg3 = NegativeSpaces.get(-6);
    
    public static String logo = Character.toString('\uEAA1');
    public static List<Character> barFrames = Frames.getFramesCharsIntegers(0, 19);

    Random random = new Random();
    
    public Table(){
        this.uuid = UUID.randomUUID();
        this.selectedItems = new ArrayList<>();
        this.selectedChallenge = new ArrayList<>();
        this.members = new ArrayList<>();
        this.foundLine = false;
        this.foundFull = false;
        this.objectsFound = 0;

    }

    public Stream<Player> getPlayerStream() {
        return members.stream().map(Bukkit::getOfflinePlayer).filter(Objects::nonNull)
                .filter(OfflinePlayer::isOnline).map(OfflinePlayer::getPlayer);
    }

    public void selectItems(Core instance) {

        var game = instance.getGame();
        var round = game.getBingoRound();
        var table = this;

        var fase = game.getBingoFase();

        this.objectsFound = 0;

        if(fase == BingoFase.ITEMS){
            var rounds = game.getItemRounds();

            List<List<Material>> diff = new ArrayList<>();

            table.getSelectedItems().clear();
    
            var currentRound = round;
    
            switch (round) {
                case ONE: diff = rounds.get(BingoRound.ONE).stream().collect(Collectors.toList()); break;
                case TWO: diff = rounds.get(BingoRound.TWO).stream().collect(Collectors.toList()); break;
                default: break;
            }
    
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
    
                    var diffRand = getRand(diff);
                    var options = diff.get(diffRand);
                    diff.remove(diffRand);
    
                    var material = options.get(getRand(options));
    
                    table.getBoard()[i][j] = new Slot(instance, material);
                    table.getSelectedItems().add(material);
    
                    if(diff.isEmpty()){
                        switch (currentRound) {
                            case TWO: {
                                currentRound = BingoRound.ONE;
                                diff = rounds.get(BingoRound.ONE).stream().collect(Collectors.toList());
                            } break;
    
                            default: 
                                break;
                        }
                    }
    
                }
            }
            
        }else if(fase == BingoFase.CHALLENGE){
            var rounds = game.getChallengeRounds();

            List<Challenge> diff = new ArrayList<>();

            table.getSelectedChallenge().clear();
    
            var currentRound = round;
    
            switch (currentRound) {
                case ONE: diff = rounds.get(BingoRound.ONE).stream().collect(Collectors.toList()); break;
                case TWO: diff = rounds.get(BingoRound.TWO).stream().collect(Collectors.toList()); break;
                case THREE: diff = rounds.get(BingoRound.THREE).stream().collect(Collectors.toList()); break;
                default: break;
            }

            /*for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    var diffRand = getRand(diff);
                    var challenge = diff.get(diffRand);

                    table.getSelectedChallenge().add(challenge);
                    table.getBoard()[i][j] = new ChallengeSlot(instance, challenge);
                }
            }*/
    
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
    
                    var diffRand = getRand(diff);
                    var challenge = diff.get(diffRand);
                    diff.remove(diffRand);
    
                    table.getBoard()[i][j] = new ChallengeSlot(instance, challenge);
                    table.getSelectedChallenge().add(challenge);
    
                    if(diff.isEmpty()){
                        switch (currentRound) {
                            case TWO: {
                                currentRound = BingoRound.ONE;
                                diff = rounds.get(BingoRound.ONE).stream().collect(Collectors.toList());
                            } break;
                            case THREE: {
                                currentRound = BingoRound.TWO;
                                diff = rounds.get(BingoRound.TWO).stream().collect(Collectors.toList());
                            } break;
    
                            default: 
                                break;
                        }
                    }
    
                }
            }
        }

    }

    public int getRand(List<?> list) {
        return random.nextInt(list.size());
    }

    public void addItemFound(){
        this.objectsFound++;
    }
    
    public String getPosDisplay(int x, int z){
        var slot = board[x][z];
        return slot.getDisplay();
    }

    public boolean checkLine(){

        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            var count = 0;
            for (int j = 0; j < 5; j++) {
                var slot = board[i][j];

                if(slot.isFound()){
                    list.add(j);
                    count++;
                }
            }

            if(count == 5) {
                return true;
            }
        }

        for (int i = 0; i < 5; i++) {
            if(is5of(list, i)) {
                return true;
            }
        }
        
        return false;
    }

    public boolean is5of(List<Integer> list, Integer i){
        var ns = list.stream().filter(n -> n == i).collect(Collectors.toList());
        return ns.size() == 5;
    }

    public String getTitle(){
        var percent = ((objectsFound-1)*100)/25;
        var barN = (int) ((percent*barFrames.size())/100);

        var bar = objectsFound < 0 ? barFrames.get(0) : barFrames.get(barN);

        return logo + neg2 + bar + neg3;
    }

    public static String getNullTitle(){
        return logo + neg2 + barFrames.get(0) + neg3;
    }


    public boolean isPlaying(UUID uuid) {
        return members.stream().anyMatch(member -> member.getMostSignificantBits() == uuid.getMostSignificantBits());
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

        if(checkLine()){
            return true;
        }

        if (checkDiagonal(board)) {
            return true;
        }

        return false;
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

    public void sendTableDisplay(Player sender){
                    
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
