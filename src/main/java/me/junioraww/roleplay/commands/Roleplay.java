package me.junioraww.roleplay.commands;

import me.junioraww.roleplay.Main;
import me.junioraww.roleplay.utils.Locale;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Roleplay implements CommandExecutor, TabCompleter {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
    if (args.length == 1) {
      if (args[0].equalsIgnoreCase("reload")) reload(sender);
      else if (args[0].equalsIgnoreCase("info")) sendInfo(sender);
      else sender.sendMessage(Locale.getRich("unknown-arg"));
    }
    else sendInfo(sender);

    return true;
  }

  private void reload(CommandSender sender) {
    if (!sender.hasPermission("roleplay.reload")) return;
    Main.getPlugin().reload();
    sender.sendMessage(Locale.getRich("reloaded"));
  }

  private void sendInfo(CommandSender sender) {
    sender.sendRichMessage("<gold>Roleplay Plugin</gold> " +
            "<light_purple><bold><click:open_url:\"https://github.com/lisyakteam/Roleplay\">[REPOSITORY]</click></bold></light_purple>" +
            "\n<yellow>Made by </yellow><white>@junioraww</white>" +
            "\n\n<gradient:#00ff00:green>Submit your fun stuff to our Github!");
    sender.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.AMBIENT, 1, 1));
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
    if (args.length == 1) return List.of("info", "reload");
    return List.of();
  }
}
