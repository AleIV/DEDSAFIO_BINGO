package me.aleiv.core.paper.teams.demo;

import java.util.Random;
import java.util.UUID;

import me.aleiv.core.paper.teams.TeamManager;

public class Program {
    private static Random random = new Random();

    public static void main(String[] args) throws Exception {
        var teamManager = new TeamManager();
        teamManager.restoreOldDataset("ffa");
        // teamManager.createTeam("team-" + getRandomNonNegativeInt(), getRandomIds());

        while (teamManager.isPipelineUp()) {
            teamManager.printContentsOfSet();
            
            Thread.sleep(1000);
        }
    }

    private static UUID[] getRandomIds() {
        var uuids = new UUID[random.nextInt(10)];
        for (int i = 0; i < uuids.length; i++) {
            uuids[i] = UUID.randomUUID();
        }
        return uuids;
    }

    private static int getRandomNonNegativeInt() {
        return Math.abs(random.nextInt());
    }

}
