package me.junioraww.roleplay.listeners;

import me.junioraww.roleplay.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Leash implements Listener {
  private final Map<String, Chicken> leashedPlayers = new HashMap<>();

  public Leash() {
    startUpdateTask();
  }

  private void setupTeam(Player player, Chicken hook) {
    Scoreboard scoreboard = player.getScoreboard();
    Team collisionTeam = scoreboard.getTeam("no_collision");
    if (collisionTeam == null) {
      collisionTeam = scoreboard.registerNewTeam("no_collision");
      collisionTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
    }
    collisionTeam.addEntities(player, hook);
  }

  @EventHandler
  public void onLeash(PlayerInteractEntityEvent event) {
    if (event.getHand() != EquipmentSlot.HAND) return;
    if (!(event.getRightClicked() instanceof Player target)) return;

    Player holder = event.getPlayer();
    String username = target.getScoreboardEntryName().toLowerCase();

    if (leashedPlayers.containsKey(username)) {
      Chicken hook = leashedPlayers.remove(username);
      if (hook != null) {
        holder.give(new ItemStack(Material.LEAD, 1));
        target.getWorld().playSound(target.getLocation(), "minecraft:item.lead.untied", 1f, 1f);
        hook.remove();
      }
      event.setCancelled(true);
      return;
    }

    ItemStack item = holder.getInventory().getItemInMainHand();
    if (item.getType() != Material.LEAD) return;
    event.setCancelled(true);

    Chicken hook = target.getWorld().spawn(target.getLocation(), Chicken.class, chicken -> {
      chicken.setInvisible(true);
      chicken.setSilent(true);
      chicken.setInvulnerable(true);
      chicken.setCollidable(false);
      chicken.setAI(false);
      chicken.setPersistent(false);
      chicken.setBaby();
      chicken.setAgeLock(true);
    });

    setupTeam(target, hook);
    target.addPassenger(hook);
    hook.setLeashHolder(holder);
    leashedPlayers.put(username, hook);

    target.getWorld().playSound(target.getLocation(), "minecraft:item.lead.tied", 1f, 1f);

    if (holder.getGameMode() != org.bukkit.GameMode.CREATIVE) {
      item.setAmount(item.getAmount() - 1);
    }
  }

  private void startUpdateTask() {
    final int[] localVelocityTimeout = { 0 };

    Bukkit.getScheduler().runTaskTimer(Main.getPlugin(), () -> {
      leashedPlayers.entrySet().removeIf(entry -> {
        String playerName = entry.getKey();
        Chicken hook = entry.getValue();
        Player target = Bukkit.getPlayer(playerName);

        if (!hook.isValid() || !hook.isLeashed()) {
          removeTeam(target, hook);
          hook.remove();
          return true;
        }

        if (target == null || !target.isOnline() || target.isDead() || !target.getWorld().equals(hook.getWorld())) {
          removeTeam(target, hook);
          removeLeash(hook);
          return true;
        }

        Entity holder = hook.getLeashHolder();
        double distance = holder.getLocation().distance(target.getLocation());

        if (distance > 15) {
          removeTeam(target, hook);
          removeLeash(hook);
          return true;
        } else if (distance > 5.0 && localVelocityTimeout[0] < Bukkit.getCurrentTick()) {
          Vector direction = holder.getLocation().toVector().subtract(target.getLocation().toVector()).normalize();
          direction.multiply(2);
          target.getWorld().playSound(target.getLocation(), "minecraft:item.lead.untied", 1f, 1f);
          target.getWorld().spawnParticle(Particle.CLOUD, target.getLocation(), 32, 0.4, 0.8, 0.4, 0.05);
          if (target.getVehicle() != null) target.getVehicle().setVelocity(direction); // UPD: check for vehicle (like horses)
          else target.setVelocity(direction);
          localVelocityTimeout[0] = Bukkit.getCurrentTick() + 20;
        }

        hook.teleport(target.getLocation());
        return false;
      });
    }, 1L, 1L);
  }

  @EventHandler
  public void onUnleash(EntityUnleashEvent event) {
    if (event.getEntity() instanceof Chicken chicken) {
      leashedPlayers.values().remove(chicken);
      chicken.remove();
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    removeLeash(event.getPlayer().getScoreboardEntryName().toLowerCase());
  }

  private void removeLeash(String playerName) {
    Chicken hook = leashedPlayers.remove(playerName);
    if (hook != null) removeLeash(hook);
  }

  private void removeLeash(Chicken hook) {
    Item item = (Item) hook.getWorld().spawnEntity(hook.getLocation(), EntityType.ITEM);
    hook.getWorld().playSound(hook.getLocation(), "minecraft:item.lead.untied", 1f, 1f);
    item.setItemStack(new ItemStack(Material.LEAD, 1));
    hook.remove();
  }

  private void removeTeam(@Nullable Player player, Chicken chicken) {
    if (player == null) return;
    Scoreboard scoreboard = player.getScoreboard();
    Team team = scoreboard.getTeam("no_collision");
    if (team != null) {
      team.removeEntity(player);
      if (chicken != null) team.removeEntity(chicken);
    }
  }
}