package me.junioraww.roleplay.utils;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {
  private static boolean patEnabled = true;
  private static double patParticleShift = 0;

  private static boolean leashEnabled = true;

  private static boolean giftsEnabled = true;
  private static boolean showLore = true;
  private static boolean tntExplodes = true;
  private static boolean tntExplodesInstantly = true;
  private static boolean tntExplodesAccumulate = true;
  private static boolean tntExplodesCauseFire = true;
  private static boolean tntExplodesBreakBlocks = true;
  private static int tntExplodesPower = 4;

  public static double getPatParticleShift() {
    return patParticleShift;
  }

  public static boolean isGiftsEnabled() {
    return giftsEnabled;
  }

  public static boolean isLeashEnabled() {
    return leashEnabled;
  }

  public static boolean isPatEnabled() {
    return patEnabled;
  }

  public static boolean showLore() {
    return showLore;
  }

  public static boolean tntExplodes() {
    return tntExplodes;
  }

  public static boolean tntExplodesInstantly() {
    return tntExplodesInstantly;
  }

  public static boolean isTntExplodesAccumulate() {
    return tntExplodesAccumulate;
  }

  public static int getTntExplodesPower() {
    return tntExplodesPower;
  }

  public static boolean isTntExplodesCauseFire() {
    return tntExplodesCauseFire;
  }

  public static boolean isTntExplodesBreakBlocks() {
    return tntExplodesBreakBlocks;
  }

  public static void init(FileConfiguration config) {
    patParticleShift = config.getDouble("features.pat.particle-y", 0);
    giftsEnabled = config.getBoolean("features.gifts.enabled", true);
    patEnabled = config.getBoolean("features.pat.enabled", true);
    leashEnabled = config.getBoolean("features.leash.enabled", true);
    tntExplodes = config.getBoolean("features.gifts.tnt.ignite", true);
    tntExplodesInstantly = config.getBoolean("features.gifts.tnt.instant", true);
    tntExplodesAccumulate = config.getBoolean("features.gifts.tnt.accumulate", true);
    tntExplodesPower = config.getInt("features.gifts.tnt.instant", 4);
    tntExplodesCauseFire = config.getBoolean("features.gifts.tnt.cause-fire", true);
    tntExplodesBreakBlocks = config.getBoolean("features.gifts.tnt.break-blocks", true);
    showLore = config.getBoolean("features.gifts.add-lore", true);
  }
}
