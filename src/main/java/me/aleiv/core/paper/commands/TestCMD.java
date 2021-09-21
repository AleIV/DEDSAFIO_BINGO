package me.aleiv.core.paper.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.game.objects.BingoTableGUI;
import me.aleiv.core.paper.utilities.Frames;
import me.aleiv.core.paper.utilities.NegativeSpaces;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;

@CommandAlias("test")
@CommandPermission("admin.perm")
public class TestCMD extends BaseCommand {

    private @NonNull Core instance;

    Random random = new Random();

    public TestCMD(Core instance) {
        this.instance = instance;

    }

    @Subcommand("mix")
    public void mix(Player sender){
        Bukkit.dispatchCommand(sender, "bingo remove-play " + sender.getName());
        Bukkit.dispatchCommand(sender, "bingo add-play " + sender.getName());

    }

    @Subcommand("chain")
    public void test(Player sender){
        var task = new BukkitTCT();

        var countdown = Frames.getFramesCharsIntegers(361, 489);
        
        countdown.forEach(frame -> {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        instance.sendActionBar(player, frame + "");
                    });
                }

            }, 100);
        });

        task.execute();

    }
/*
    @Subcommand("neg1")
    public void neg1(CommandSender sender, Integer neg){
        Timer.setNeg1(NegativeSpaces.get(neg));

    }

    @Subcommand("neg2")
    public void neg2(CommandSender sender, Integer neg){
        Timer.setNeg2(NegativeSpaces.get(neg));

    }

    @Subcommand("neg3")
    public void neg3(CommandSender sender, Integer neg){
        Timer.setNeg3(NegativeSpaces.get(neg));

    }*/

    @Subcommand("neg")
    public void neg(CommandSender sender, Integer neg){
        BingoTableGUI.neg = NegativeSpaces.get(neg);

    }

    @Subcommand("neg2")
    public void neg4(CommandSender sender, Integer neg){
        BingoTableGUI.neg2 = NegativeSpaces.get(neg);

    }

    @Subcommand("blank")
    public void blank(CommandSender sender, Boolean bool){
        instance.getBingoManager().setBlankTab(bool);

    }

    @Subcommand("test-item")
    public void neg2(CommandSender sender, Material material){
        var mat = instance.getGame().getMaterials();
        instance.broadcastMessage(Character.toString(mat.get(material).getCode()) + material.toString());
        instance.broadcastMessage("");
        instance.broadcastMessage("");

    }

}
