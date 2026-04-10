package me.junioraww.roleplay;

import me.junioraww.roleplay.listeners.Leash;
import me.junioraww.roleplay.listeners.Pat;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
  private static Main plugin;

  public static Main getPlugin() {
    return plugin;
  }

  @Override
  public void onEnable() {
    plugin = this;
    getServer().getPluginManager().registerEvents(new Leash(), this);
    getServer().getPluginManager().registerEvents(new Pat(), this);
  }

  @Override
  public void onDisable() {
    plugin = null;
  }
}
