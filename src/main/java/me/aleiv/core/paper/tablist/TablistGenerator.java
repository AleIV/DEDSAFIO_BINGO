package me.aleiv.core.paper.tablist;

import org.bukkit.entity.Player;

import me.aleiv.core.paper.Core;

public abstract class TablistGenerator {
  protected Core plugin;

  public TablistGenerator(Core plugin) {
    this.plugin = plugin;
  }

  public abstract String[] generateHeaderFooter(Player paramPlayer);

  public abstract TabEntry[] generateBars(Player paramPlayer);
}
