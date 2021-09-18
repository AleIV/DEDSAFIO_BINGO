package me.aleiv.core.paper.teams.demo;

import me.aleiv.core.paper.teams.TeamManager;

public class Program {

    public static void main(String[] args) throws Exception {
        var teamManager = new TeamManager();
        teamManager.restoreOldDataset("ffa");

        while (teamManager.isPipelineUp()) {
            //teamManager.printContentsOfSet();

            Thread.sleep(1000);
        }
    }

}
