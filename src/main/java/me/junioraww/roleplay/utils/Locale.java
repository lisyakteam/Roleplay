package me.junioraww.roleplay.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class Locale {
  private static FileConfiguration config;
  private static final Map<String, Component> richComponents = new HashMap<>();

  public static void init(FileConfiguration _config) {
    config = _config;
    var serializer = MiniMessage.miniMessage();
    for (String key : config.getKeys(false)) {
      String translation = config.getString(key, "");
      if (translation.contains("<")) richComponents.put(
              key, serializer.deserialize(translation)
      );
    }
  }

  public static String get(String key) {
    return config.getString(key, "Empty translation");
  }

  public static Component getRich(String key) {
    Component translation = richComponents.get(key);
    if (translation == null) return Component.text("<!i>Empty translation");
    return translation;
  }
}
