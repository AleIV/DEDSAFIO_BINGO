package me.aleiv.core.paper.teams.demo;

import me.aleiv.core.paper.teams.TeamManager;

public class Program {

    public static void main(String[] args) throws Exception {
        var teamManager = new TeamManager();
        teamManager.restoreOldDataset("ffa");
        // Constantly take inputs from the user
        while (true) {
            System.out.print("Provide input: ");
            var input = System.console().readLine();
            if (input.equals("exit")) {
                break;
            }
            processInput(input);
        }

    }

    static void processInput(String input) {
        System.out.println("Processing input: " + input);

    }

}
