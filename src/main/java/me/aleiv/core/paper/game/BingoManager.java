package me.aleiv.core.paper.game;

import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.event.Listener;

import me.aleiv.core.paper.Core;

public class BingoManager implements Listener {

    Core instance;

    Table globalTable;

    Random random = new Random();

    public BingoManager(Core instance) {
        this.instance = instance;
    }

    public void selectItems(Table table) {

        //TODO: TEST
        
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {

                var options = instance.getGame().getMaterials().keySet().stream().collect(Collectors.toList());

                var material = options.get(random.nextInt(options.size()));

                table.getBoard()[i][j] = new Slot(instance, material);
                table.getSelectedItems().add(material);

            }
        }

        /*

        var game = instance.getGame();
        var difficulty = game.getBingoDifficulty();

        List<List<Material>> diff = new ArrayList<>();

        table.getSelectedItems().clear();

        switch (difficulty) {

            case EASY: {
                diff = game.getClassEasy().stream().collect(Collectors.toList());
            }
                break;

            case MEDIUM: {
                diff = game.getClassMedium().stream().collect(Collectors.toList());
            }
                break;

            case HARD: {
                diff = game.getClassHard().stream().collect(Collectors.toList());
            }
                break;

            case EXPERT: {
                diff = game.getClassExpert().stream().collect(Collectors.toList());
            }
                break;

            default:
                break;
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {

                var options = diff.get(random.nextInt(diff.size()));
                diff.remove(options);

                var material = options.get(random.nextInt(options.size()));

                table.getBoard()[i][j] = new Slot(instance, material);
                table.getSelectedItems().add(material);

            }
        }*/
    }

}