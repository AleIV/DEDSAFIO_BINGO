package me.aleiv.core.paper.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.BingoDifficulty;
import me.aleiv.core.paper.Game.BingoMode;
import me.aleiv.core.paper.Game.BingoType;
import me.aleiv.core.paper.Game.GameStage;
import me.aleiv.core.paper.game.Table;
import me.aleiv.core.paper.game.Timer;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("admin-bingo")
@CommandPermission("admin.perm")
public class AdminBingoCMD extends BaseCommand {

    private @NonNull Core instance;
    String adminPerm = "admin.perm";

    Random random = new Random();

    public AdminBingoCMD(Core instance) {
        this.instance = instance;

    }

    @Subcommand("start")
    public void start(CommandSender sender){
        var game = instance.getGame();
        if(game.getGameStage() != GameStage.LOBBY){
            sender.sendMessage(ChatColor.of(game.getColor3()) + "The game only can be started in lobby stage. \n Restart or finish the game to replay.");

        }else{
        
            instance.getBingoManager().startGame();

        }

    }

    @Subcommand("mix")
    public void mix(Player sender){
        Bukkit.dispatchCommand(sender, "bingo remove-play " + sender.getName());
        Bukkit.dispatchCommand(sender, "bingo add-play " + sender.getName());

    }

    @Subcommand("test")
    public void test(Player sender){
        var task = new BukkitTCT();

        for (int i = 0; i < 5; i++) {
            final var u = i;
            task.addWithDelay(new BukkitRunnable(){
                @Override
                public void run() {
                    instance.broadcastMessage("TEST " + u);
                    sender.teleport(Bukkit.getWorld("world").getSpawnLocation().add(random.nextInt(10) , 10, random.nextInt(10)));
                }
                
            }, 1000);
        }

        task.execute();

    }

    @Subcommand("restart")
    public void restart(CommandSender sender){
        var game = instance.getGame();
        if(game.getGameStage() == GameStage.INGAME){
            instance.getBingoManager().restartGame();

        }else{
            
            sender.sendMessage(ChatColor.of(game.getColor3()) + "The game only can be restarted in game stage.");

        }

    }

    @Subcommand("difficulty")
    public void difficulty(CommandSender sender){
        var game = instance.getGame();
        var difficulty = game.getBingoDifficulty();

        if(difficulty == BingoDifficulty.EASY){

            game.setBingoDifficulty(BingoDifficulty.MEDIUM);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo difficulty switched to MEDIUM.");

        }else if(difficulty == BingoDifficulty.MEDIUM){

            game.setBingoDifficulty(BingoDifficulty.HARD);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo difficulty switched to HARD.");
    
        }else if(difficulty == BingoDifficulty.HARD){

            game.setBingoDifficulty(BingoDifficulty.EXPERT);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo difficulty switched to EXPERT.");
    
        }else if(difficulty == BingoDifficulty.EXPERT){

            game.setBingoDifficulty(BingoDifficulty.EASY);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo difficulty switched to EASY.");
    
        }

    }

    @Subcommand("neg")
    public void neg(CommandSender sender, Integer neg){
        instance.getGame().setNeg(neg);

    }

    @Subcommand("neg2")
    public void neg2(CommandSender sender, Integer neg){
        instance.getGame().setNeg2(neg);

    }

    @Subcommand("neg3")
    public void neg3(CommandSender sender, Integer neg){
        instance.getGame().setNeg3(neg);

    }

    @Subcommand("blank")
    public void blank(CommandSender sender, Boolean bool){
        instance.getBingoManager().setBlankTab(bool);

    }

    @Subcommand("timer")
    public void timer(CommandSender sender, Integer i){
        instance.getGame().setTimer(new Timer(instance, i));

    }

    @Subcommand("test")
    public void neg2(CommandSender sender, Material material, Integer neg){
        var mat = instance.getGame().getMaterials();
        instance.broadcastMessage(Character.toString(mat.get(material).getCode()) + instance.getNegativeSpaces().get(neg) + Character.toString('\uE008'));
        instance.broadcastMessage("");
        instance.broadcastMessage("");

    }

    @Subcommand("type")
    public void type(CommandSender sender){
        var game = instance.getGame();
        var isFullBingo = game.isBingoTypeFull();

        if(isFullBingo){
            game.setBingoType(BingoType.LINE);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo type switched to LINE.");

        }else{
            game.setBingoType(BingoType.FULL);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo type switched to FULL.");
    
        }

    }

    @Subcommand("mode")
    public void mode(CommandSender sender) {
        var game = instance.getGame();
        var normalMode = game.isNormalMode();

        if (normalMode) {
            game.setBingoMode(BingoMode.TWITCH);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.DARK_PURPLE + "Twitch Rivals mode Enabled.");

        } else {
            game.setBingoMode(BingoMode.NORMAL);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.of(game.getColor1()) + "Normal mode Enabled.");

        }

        var title = instance.getBingoManager().getTitle();
        var boards = game.getBoards();

        for (var board : boards.values()) {
            var uuid = board.getPlayer().getUniqueId();
            var table = instance.getBingoManager().findTable(uuid);

            board.updateTitle("" + title);
            instance.getBingoManager().updateBoard(board, table);
        }

    }

    @Subcommand("stage")
    @CommandCompletion("@stages")
    public void gameStage(CommandSender sender, String stage) {

        var game = instance.getGame();

        switch (stage) {
            case "LOBBY": {
                game.setGameStage(GameStage.LOBBY);
                var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
                instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Game stage switched to LOBBY stage.");
            }
                break;

            case "STARTING": {
                game.setGameStage(GameStage.STARTING);
                var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
                instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Game stage switched to STARTING stage.");
            }
                break;

            case "INGAME": {
                game.setGameStage(GameStage.INGAME);
                var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
                instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Game stage switched to INGAME stage.");
            }
                break;

            case "POSTGAME": {
                game.setGameStage(GameStage.POSTGAME);
                var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
                instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Game stage switched to POSTGAME stage.");
            }
                break;

            default: {
                sender.sendMessage(ChatColor.of(game.getColor3()) + "Invalid stage.");

            }
                break;
        }

    }

    @Subcommand("add-play")
    @CommandCompletion("@players")
    public void addPlay(CommandSender sender, @Flags("other") Player target) {
        var game = instance.getGame();
        var uuid = target.getUniqueId();
        var tables = game.getTables();
        var manager = instance.getBingoManager();

        var table = manager.findTable(uuid);

        /*if(bingoPlayer == null){
            sender.sendMessage(ChatColor.of(game.getColor3()) + "Bingo player " + target.getName() + " doesn't exist.");

        }else */if(table != null){
            sender.sendMessage(ChatColor.of(game.getColor3()) + "Bingo player " + target.getName() + " is already playing.");

        }else{
            table = new Table();
            
            table.getMembers().add(uuid);
            
            tables.add(table);
            instance.getBingoManager().selectItems(table);
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo player " + target.getName() + " is now playing.");
        }

    }

    @Subcommand("remove-play")
    @CommandCompletion("@players")
    public void removePlay(CommandSender sender, @Flags("other") Player target) {
        var game = instance.getGame();
        var uuid = target.getUniqueId();

        var tables = game.getTables();
        var manager = instance.getBingoManager();
        var table = manager.findTable(uuid);
        
        /*if(bingoPlayer == null){
            sender.sendMessage(ChatColor.of(game.getColor3()) + "Bingo player " + target.getName() + " doesn't exist.");

        }else */if(table == null){

            sender.sendMessage(ChatColor.of(game.getColor3()) + "Bingo player " + target.getName() + " is already not playing.");

        }else{

            tables.remove(table);
            table.getMembers().clear();
            var senderName = ChatColor.GRAY + "[" + sender.getName().toString() + "] ";
            instance.broadcastMessage(senderName + ChatColor.of(game.getColor4()) + "Bingo player " + target.getName() + " is now not playing.");
        }

    }

}