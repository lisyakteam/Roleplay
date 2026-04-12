package me.junioraww.roleplay.listeners;

import me.junioraww.roleplay.utils.Config;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class Pat implements Listener {
  @EventHandler
  public void playerClick(PlayerInteractEntityEvent event) {
    if (!event.getHand().getGroup().test(EquipmentSlot.HAND)) return;
    var player = event.getPlayer();

    if (player.isSneaking() && player.getActiveItem().isEmpty()) {
      var other = event.getRightClicked();

      if (other instanceof Player interacted) {
        var particleLoc = interacted.getLocation().clone();

        if (interacted.isSneaking()) particleLoc.add(0, 1, 0);
        else if (interacted.isSwimming()) particleLoc.add(0, 0.3, 0);
        else particleLoc.add(0, Config.getPatParticleShift(), 0);

        interacted.getWorld().spawnParticle(Config.getPatParticle(), particleLoc, 4, 0.3, 0, 0.3, 0);
        player.swingMainHand();
      }
    }
  }
}
