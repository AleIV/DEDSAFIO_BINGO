package me.aleiv.core.paper.teams;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import lombok.Data;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.teams.objects.Team;

@Data
public class TeamManager implements Listener{
    Core instance;

    HashMap<UUID, Team> teamMap = new HashMap<>();
    boolean teamManagement;
    boolean friendlyFire;
    int teamSize;

    public TeamManager(Core instance){
        this.instance = instance;
    }

    public Team getPlayerTeam(final UUID uuid) {
        if (teamMap.isEmpty()) return null;

        return teamMap.values().stream().filter(team -> team.isMember(uuid)).findFirst().orElse(null);
    }

    public boolean hasTeam(final Player player) {
        return getPlayerTeam(player.getUniqueId()) != null;
    }

    public boolean createTeam(){
        return true;
    }

}
