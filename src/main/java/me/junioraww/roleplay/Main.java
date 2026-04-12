package me.junioraww.roleplay;

import com.destroystokyo.paper.profile.PlayerProfile;
import me.junioraww.roleplay.listeners.Gifts;
import me.junioraww.roleplay.listeners.Leash;
import me.junioraww.roleplay.listeners.Pat;
import me.junioraww.roleplay.utils.Config;
import me.junioraww.roleplay.utils.Locale;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin {
  private static Main plugin;
  private static final List<PlayerProfile> giftTextures = new ArrayList<>();

  public static Main getPlugin() {
    return plugin;
  }
  public static List<PlayerProfile> getGiftTextures() {
    return giftTextures;
  }

  @Override
  public void onEnable() {
    plugin = this;

    load();
  }

  public void load() {
    giftTextures.clear();

    if (Config.isLeashEnabled()) getServer().getPluginManager().registerEvents(new Leash(), this);
    if (Config.isPatEnabled()) getServer().getPluginManager().registerEvents(new Pat(), this);
    if (Config.isGiftsEnabled()) getServer().getPluginManager().registerEvents(new Gifts(), this);

    String localeConfigPath = "locales/" + getConfig().getString("locale") + ".yml";

    File folder = getDataFolder();
    File giftFile = new File(folder, "gifts.yml");
    File localeFile = new File(folder, localeConfigPath);

    saveDefaultConfig();
    if (!giftFile.exists()) saveResource("gifts.yml", false);
    if (!localeFile.exists()) saveResource(localeConfigPath, false);

    YamlConfiguration localeConfig = new YamlConfiguration();
    try {
      localeConfig.load(localeFile);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    Config.init(getConfig());
    Locale.init(localeConfig);

    if (Config.isGiftsEnabled()) {
      Gifts.loadTextures(giftFile, giftTextures);
      Gifts.init();
    }
  }

  @Override
  public void onDisable() {
    plugin = null;
  }
}
