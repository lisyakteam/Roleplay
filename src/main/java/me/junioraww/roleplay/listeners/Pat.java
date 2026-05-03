package me.junioraww.roleplay.listeners;

import me.junioraww.roleplay.Main;
import me.junioraww.roleplay.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.BoundingBox;

public class Pat implements Listener {
  private static final NamespacedKey key = NamespacedKey.minecraft("roleplay");;
  private static final AttributeModifier.Operation operation = AttributeModifier.Operation.MULTIPLY_SCALAR_1;

  private static final AttributeModifier[] cached = new AttributeModifier[]{
          new AttributeModifier(key, -0.03, operation),
          new AttributeModifier(key, -0.06, operation),
  };

  @EventHandler
  public void playerClick(PlayerInteractEntityEvent event) {
    if (!event.getHand().getGroup().test(EquipmentSlot.HAND)) return;
    Player player = event.getPlayer();

    if (player.isSneaking() && player.getActiveItem().isEmpty()) {
      Entity interacted = event.getRightClicked();
      EntityType type = interacted.getType();

      if (interacted instanceof LivingEntity e) {
        if (!Config.patMobsAllowed() && type != EntityType.PLAYER) return;
        if (type == EntityType.OCELOT) return;

        // TODO use ClientboundAttributesPacket

        if (Config.patScaling()) {
          if (e.getAttribute(Attribute.SCALE) == null) e.registerAttribute(Attribute.SCALE);
          var scale = e.getAttribute(Attribute.SCALE);

          if (scale == null || scale.getModifier(key) != null) return;
          scale.addTransientModifier(cached[0]);

          Bukkit.getScheduler().runTaskLater(Main.getPlugin(), task -> {
            scale.removeModifier(cached[0]);
            scale.addTransientModifier(cached[1]);
          }, 2L);

          Bukkit.getScheduler().runTaskLater(Main.getPlugin(), task -> {
            scale.removeModifier(cached[1]);
            scale.addTransientModifier(cached[0]);
          }, 4L);

          Bukkit.getScheduler().runTaskLater(Main.getPlugin(), task -> {
            scale.removeModifier(cached[0]);
          }, 5L);
        }

        Location particleLoc = interacted.getLocation().clone();

        BoundingBox box = interacted.getBoundingBox();
        double shiftY = box.getHeight();
        double area = (box.getWidthX() + box.getWidthZ()) / 6;

        if (interacted.isSneaking()) particleLoc.add(0, shiftY, 0);
        else particleLoc.add(0, shiftY, 0);

        interacted.getWorld().spawnParticle(Config.getPatParticle(), particleLoc, 4, area, 0, area, 0);
        player.swingMainHand();
      }
    }
  }
}
