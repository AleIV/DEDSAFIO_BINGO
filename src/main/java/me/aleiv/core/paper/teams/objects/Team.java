package me.aleiv.core.paper.teams.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import lombok.Data;

@Data
public class Team {
    
    UUID teamUUID;
    UUID leaderUUID;
    List<UUID> members;
    String teamName;
    String displayName;
    int teamNumber;

    int points;

    static int globalTeamNumber = 0;

    public Team(UUID leaderUUID){
        this.teamUUID = UUID.randomUUID();
        this.leaderUUID = leaderUUID;
        this.members = new ArrayList<>();
        members.add(leaderUUID);
        this.teamName = "";
        this.displayName = "";
        this.teamNumber = globalTeamNumber++;
        
    }

    public boolean isMember(UUID uuid) {
        return members.stream().anyMatch(member -> member.getMostSignificantBits() == uuid.getMostSignificantBits());
    }

    public Stream<Player> getPlayerStream() {
        return members.stream().map(Bukkit::getOfflinePlayer).filter(Objects::nonNull)
                .filter(OfflinePlayer::isOnline).map(OfflinePlayer::getPlayer);
    }

    public void sendTeamMessage(String str) {
        getPlayerStream().forEach(player -> player.sendMessage(str));
    }

    public List<String> getListOfMembers() {
        var names = new ArrayList<String>();
        for (var member : members) {
            var ofPlayer = Bukkit.getOfflinePlayer(member);
            if (ofPlayer != null)
                names.add(ofPlayer.getName());
        }
        return names;
    }

    
}
