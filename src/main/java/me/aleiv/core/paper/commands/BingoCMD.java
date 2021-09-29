package me.aleiv.core.paper.commands;

import org.bukkit.Bukkit;
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
        
            instance.getBingoManager().startGame();
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

        var round = game.getBingoRound();
        var manager = instance.getBingoManager();

        Bukkit.getOnlinePlayers().forEach(player ->{
            var loc = player.getLocation();
            player.playSound(loc, "bingo.horn", 1, 1);
        });

        var timer = game.getTimer();
        var time = 0;

        switch (round) {
            case ONE:{
                time = 1200;

            }break;
            case TWO:{
                time = 1800;

            }break;
            case THREE:{
                time = 3600;

            }break;
        
            default:
                break;
        }

        final var v = time;
        timer.setPreStart(time);

        Bukkit.getScheduler().runTaskLater(instance, task->{
            timer.start(v, (int) game.getGameTime());
        }, 20*5);

        manager.selectTables();
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
    public void setTables(CommandSender sender) {
        var manager = instance.getBingoManager();
        final var color = instance.getGame().getColor1();
        
        manager.selectTables();
        sender.sendMessage(ChatColor.of(color) + "Tables selected.");

    }

    @Subcommand("remove-tables")
    public void removeTables(CommandSender sender) {
        var game = instance.getGame();

        game.getTables().clear();
        
        final var color = instance.getGame().getColor1();
        sender.sendMessage(ChatColor.of(color) + "Tables cleared.");

    }

}
