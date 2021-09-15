package me.aleiv.core.paper.teams.deprecated;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.Listener;

import lombok.Data;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.teams.objects.Team;

@Data
public class TeamManager implements Listener {
    Core instance;

    HashMap<UUID, Team> teamMap = new HashMap<>();
    boolean teamManagement;
    boolean friendlyFire;
    int teamSize = 1;

    public TeamManager(Core instance) {
        this.instance = instance;
    }

    public boolean isTeams() {
        return teamSize != 1;
    }

    public boolean createTeam() {
        return true;
    }

}
