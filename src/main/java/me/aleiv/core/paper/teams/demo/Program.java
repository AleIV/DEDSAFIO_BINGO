package me.aleiv.core.paper.teams.demo;

import java.util.UUID;
import java.util.logging.Logger;

import me.aleiv.core.paper.teams.TeamManager;
import me.aleiv.core.paper.teams.exceptions.EmptyDatasetException;
import me.aleiv.core.paper.teams.exceptions.TeamAlreadyExistsException;

public class Program {
    private static Logger logger = Logger.getLogger("Demo-program");

    public static void main(String[] args) throws Exception {
        var teamManager = new SimpleTeamManager("redis://localhost");
        teamManager.initialize();

        try {
            teamManager.restoreOldDataset("ffa");
        } catch (EmptyDatasetException e) {
            e.printStackTrace();
        }
        // Constantly take inputs from the user
        while (true) {
            logger.info("Provide input: ");
            var input = System.console().readLine();
            if (input.equals("exit")) {
                closeProgram(teamManager);
                break;
            }
            processInput(input, teamManager);
        }

    }

    static void processInput(String input, TeamManager teamManager) {
        var inputArray = input.split(" ");
        // Return in case of invalid input
        if (inputArray.length == 0) {
            logger.info("Invalid input");
            return;
        }
        if (inputArray[0].equals("add-random")) {
            if (inputArray.length < 3) {
                logger.warning("Invalid input. \nCorrect syntax: add-random <teamName> <amountOfRandomMembers>");
                return;
            }
            // The team's name is the first argument
            var teamName = inputArray[1];
            // The amount of random members is the second argument.
            var amount = Integer.parseInt(inputArray[2]);
            // Create random mates
            var teamIds = new UUID[amount];
            for (int i = 0; i < amount; i++)
                teamIds[i] = UUID.randomUUID();
            // Create team
            try {
                var team = teamManager.createTeam(teamName, teamIds);
                logger.info("Team created: " + team.getTeamID());
            } catch (TeamAlreadyExistsException e) {
                e.printStackTrace();
            }

        } else if (inputArray[0].equals("remove")) {
            if (inputArray.length == 2) {
            } else {
                logger.warning("Invalid input");
            }
        } else if (inputArray[0].equals("list")) {
            teamManager.printContentsOfSet();
        }

    }

    static void closeProgram(TeamManager teamManager) {
        teamManager.disconect();
    }

}
