package me.aleiv.core.paper.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.BingoFase;
import me.aleiv.core.paper.Game.BingoRound;
import me.aleiv.core.paper.Game.GameStage;
import me.aleiv.core.paper.game.objects.BingoTableGUI;
import me.aleiv.core.paper.game.objects.Table;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("bingo")
public class BingoCMD extends BaseCommand {

    private @NonNull Core instance;

    public BingoCMD(Core instance) {
        this.instance = instance;

    }

    @Default
    public void checkPlayer(Player sender){
        var game = instance.getGame();
        var uuid = sender.getUniqueId();

        var manager = instance.getBingoManager();
        var table = manager.findTable(uuid);

        if(table == null){
            sender.sendMessage(ChatColor.of(game.getColor3()) + "" + sender.getName() + " table doesn't exist.");
        }else{
            var gui = new BingoTableGUI(table).getGui();
            gui.open(sender);
        }
    }

    @Subcommand("table")
    @CommandCompletion("@players")
    @CommandPermission("table.perm")
    public void checkPlayer(Player sender, @Flags("other") Player target) {
        var game = instance.getGame();
        var uuid = target.getUniqueId();

        var manager = instance.getBingoManager();
        var table = manager.findTable(uuid);

        if(table == null){
            sender.sendMessage(ChatColor.of(game.getColor3()) + "" + target.getName() + " table doesn't exist.");
        }else{
            var gui = new BingoTableGUI(table).getGui();
            gui.open(sender);
        }
    }

    @CommandPermission("admin.perm")
    @Subcommand("start")
    public void start(CommandSender sender){
        var game = instance.getGame();
        if(game.getGameStage() != GameStage.LOBBY){
            sender.sendMessage(ChatColor.of(game.getColor3()) + "The game only can be started in lobby stage. \n Restart or finish the game to replay.");

        }else{
        
            instance.broadcastMessage(ChatColor.of("#74ebfb") + "Starting scatter...");
            instance.getScatterManager().runKernelScatter();
        }

    }

    @CommandPermission("admin.perm")
    @Subcommand("restart")
    public void restart(CommandSender sender){
        var game = instance.getGame();
        if(game.getGameStage() == GameStage.INGAME || game.getGameStage() == GameStage.POSTGAME){
            instance.getBingoManager().restartGame();

        }else{
            
            sender.sendMessage(ChatColor.of(game.getColor3()) + "The game only can be restarted in game stage.");

        }

    }

    @Subcommand("total-start")
    @CommandPermission("admin.perm")
    public void setTimer(CommandSender sender) {
        
        var game = instance.getGame();
        final var color = game.getColor1();

        instance.getBingoManager().totalStart();
        sender.sendMessage(ChatColor.of(color) + "Tables selected & timer set.");

    }

    @Subcommand("round")
    @CommandPermission("admin.perm")
    public void round(CommandSender sender, BingoRound round){
        var game = instance.getGame();

        game.setBingoRound(round);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        sender.sendMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo round switched to " + round.toString());

    }

    @Subcommand("fase")
    @CommandPermission("admin.perm")
    public void fase(CommandSender sender, BingoFase fase){
        var game = instance.getGame();

        game.setBingoFase(fase);
        var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
        sender.sendMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo fase switched to " + fase.toString());

    }

    @Subcommand("select-tables")
    @CommandPermission("admin.perm")
    public void setTables(CommandSender sender, Boolean bool) {
        var manager = instance.getBingoManager();
        final var color = instance.getGame().getColor1();
        
        if(bool){
            manager.selectTables();
            sender.sendMessage(ChatColor.of(color) + "Tables selected.");
        }

    }

    @Subcommand("select-table")
    @CommandPermission("admin.perm")
    public void selectTable(CommandSender sender, @Flags("other") Player target) {
        var manager = instance.getBingoManager();
        var man = instance.getTeamManager();
        final var color = instance.getGame().getColor1();

        var table = manager.findTable(target.getUniqueId());
        var team = man.getPlayerTeam(target.getUniqueId());

        if(table != null){
            sender.sendMessage(ChatColor.of(color) + "Player already has a table.");

        }else if(team != null){
            table = new Table(team.getTeamID(), team.getMembers());
            table.selectItems(instance);
            instance.getGame().getTables().add(table);

            sender.sendMessage(ChatColor.of(color) + "Added table for the player.");
        }else{
            sender.sendMessage(ChatColor.of(color) + "Player doesn't have a team.");
            
        }

    }

    @Subcommand("remove-table")
    @CommandPermission("admin.perm")
    public void removeTable(CommandSender sender, @Flags("other") Player target) {
        var manager = instance.getBingoManager();
        var man = instance.getTeamManager();
        final var color = instance.getGame().getColor1();

        var table = manager.findTable(target.getUniqueId());
        var team = man.getPlayerTeam(target.getUniqueId());

        if(table == null){
            sender.sendMessage(ChatColor.of(color) + "Player doesn't have a table.");

        }else if(team != null){
            table.getMembers().clear();
            instance.getGame().getTables().remove(table);

            sender.sendMessage(ChatColor.of(color) + "Removed table for the player.");
        }else{
            sender.sendMessage(ChatColor.of(color) + "Player doesn't have a team.");
            
        }

    }

    @Subcommand("remove-tables")
    @CommandPermission("admin.perm")
    public void removeTable(CommandSender sender, Boolean bool) {
        var game = instance.getGame();

        game.getTables().clear();
        
        final var color = instance.getGame().getColor1();
        sender.sendMessage(ChatColor.of(color) + "Tables cleared.");

    }

    @Subcommand("global-cmd")
    @CommandPermission("admin.perm")
    public void globalCMD(CommandSender sender, String text) {
        instance.getTeamManager().sendCommandToNodes(text);
        sender.sendMessage(ChatColor.YELLOW + "CMD SENT.");

    }



}
