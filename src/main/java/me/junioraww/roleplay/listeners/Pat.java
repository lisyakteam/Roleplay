package me.junioraww.roleplay.listeners;

import me.junioraww.roleplay.utils.Config;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class Pat implements Listener {
  @EventHandler
  public void playerClick(PlayerInteractEntityEvent event) {
    if (!event.getHand().getGroup().test(EquipmentSlot.HAND)) return;
    Player player = event.getPlayer();

    if (player.isSneaking() && player.getActiveItem().isEmpty()) {
      Entity interacted = event.getRightClicked();
      EntityType type = interacted.getType();

      if (!Config.patMobsAllowed() && type != EntityType.PLAYER) return;
      if (type == EntityType.OCELOT) return;

      Location particleLoc = interacted.getLocation().clone();
      double shiftY = interacted.getBoundingBox().getHeight();

      if (interacted.isSneaking()) particleLoc.add(0, shiftY, 0);
      else particleLoc.add(0, shiftY, 0);

      interacted.getWorld().spawnParticle(Config.getPatParticle(), particleLoc, 4, 0.3, 0, 0.3, 0);
      player.swingMainHand();
    }
  }
}
