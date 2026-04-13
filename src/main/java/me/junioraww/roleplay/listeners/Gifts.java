package me.junioraww.roleplay.listeners;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.Gson;
import me.junioraww.roleplay.Main;
import me.junioraww.roleplay.utils.Config;
import me.junioraww.roleplay.utils.Locale;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Gifts implements Listener {
  static NamespacedKey giftKey;

  public static void init() {
    giftKey = new NamespacedKey(Main.getPlugin(), "gift");;
  }

  @EventHandler
  public void click(InventoryClickEvent event) {
    if (event.getClickedInventory() instanceof CraftingInventory inventory) {
      if (event.getCurrentItem() == null) return;
      if (inventory.getResult() == null) return;
      if (!event.getCurrentItem().getPersistentDataContainer().has(giftKey)) return;
      if (!inventory.getResult().getPersistentDataContainer().has(giftKey)) return;

      ItemStack result = inventory.getResult().clone();

      if (event.getCurrentItem().getPersistentDataContainer().get(giftKey, PersistentDataType.STRING)
              .equals(inventory.getResult().getPersistentDataContainer().get(giftKey, PersistentDataType.STRING))) {
        ItemStack[] matrix = inventory.getMatrix();
        for (int i = 0; i < 9; i++) {
          if (i == 4) matrix[i] = null;
          else matrix[i].setAmount(matrix[i].getAmount() - 1);
        }
        inventory.setMatrix(matrix);
        event.getWhoClicked().getInventory().addItem(result);
      }
    }
  }

  @EventHandler
  public void craft(PrepareItemCraftEvent event) {
    CraftingInventory inv = event.getInventory();
    ItemStack[] matrix = inv.getMatrix();

    if (!isGiftRecipe(matrix)) {
      return;
    }

    ItemStack itemInside = matrix[4] == null
            ? new ItemStack(Material.AIR)
            : matrix[4].clone();

    ItemStack current = inv.getResult();
    if (isGift(current)) return;

    ItemStack gift = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) gift.getItemMeta();

    var textures = Main.getGiftTextures();
    meta.setPlayerProfile(
            textures.get(ThreadLocalRandom.current().nextInt(textures.size()))
    );

    meta.getPersistentDataContainer().set(
            giftKey,
            PersistentDataType.STRING,
            new Gson().toJson(itemInside.serialize())
    );

    meta.customName(Locale.getRich("gift"));

    if (Config.showLore()) {
      meta.lore(List.of(
              MiniMessage.miniMessage().deserialize(
                      Locale.get("gift-lore")
                              .replace("%NAME%", event.getView().getPlayer().getName())
              )
      ));
    }

    gift.setItemMeta(meta);
    inv.setResult(gift);
  }

  private boolean isGiftRecipe(ItemStack[] matrix) {
    for (int i = 0; i < 9; i++) {
      if (i == 4) continue;

      ItemStack item = matrix[i];
      if (item == null || item.getType() != giftMatrix[i]) {
        return false;
      }
    }
    return true;
  }

  private boolean isGift(ItemStack item) {
    if (item == null || item.getType() != Material.PLAYER_HEAD) return false;

    ItemMeta meta = item.getItemMeta();
    if (meta == null) return false;

    return meta.getPersistentDataContainer().has(
            giftKey,
            PersistentDataType.STRING
    );
  }

  @EventHandler
  public void giftBroken(BlockBreakEvent event) {
    Block block = event.getBlock();
    if (!(block.getState() instanceof Skull)) return;

    CraftBlock craftBlock = (CraftBlock) block;
    ServerLevel level = craftBlock.getCraftWorld().getHandle();

    BlockPos pos = craftBlock.getPosition();
    BlockEntity be = level.getBlockEntity(pos);

    if (!(be instanceof SkullBlockEntity skullBe)) return;

    RegistryAccess provider = level.registryAccess();
    CompoundTag tag = skullBe.saveWithoutMetadata(provider);

    if (!tag.contains("components")) return;
    Optional<CompoundTag> A = tag.getCompound("components");
    if (A.isEmpty()) return;
    Optional<CompoundTag> B = A.get().getCompound("minecraft:custom_data");
    if (B.isEmpty()) return;
    Optional<CompoundTag> C = B.get().getCompound("PublicBukkitValues");
    if (C.isEmpty()) return;
    Optional<String> D = C.get().getString("roleplay:gift");
    if (D.isEmpty()) return;

    String gift = D.get();

    Map<String, Object> map = new Gson().fromJson(
            gift,
            new com.google.gson.reflect.TypeToken<Map<String, Object>>() {}.getType()
    );

    ItemStack give = ItemStack.deserialize(new HashMap<>(map));

    event.setDropItems(false);
    Location center = block.getLocation().toCenterLocation();

    if (give.getType() == Material.TNT) {
      if (Config.tntExplodes()
              && event.getPlayer().getInventory().getItemInMainHand().getType() != Material.SHEARS) {
        if (Config.tntExplodesInstantly()) {
          int i_power = Config.getTntExplodesPower();
          int power = Config.isTntExplodesAccumulate() ? i_power * give.getAmount() : i_power;
          center.createExplosion(power, Config.isTntExplodesCauseFire(), Config.isTntExplodesBreakBlocks());
        } else {
          for (int tnt = 0; tnt < give.getAmount(); tnt++) {
            block.getWorld().spawnEntity(center, EntityType.TNT);
          }
        }

        return;
      }
    }

    if (give.getType() == Material.AIR) {
      event.getPlayer().sendMessage(Locale.getRich("gift-empty"));
    }
    else block.getWorld().dropItemNaturally(center, give);
  }

  private final Material[] giftMatrix = new Material[]{
          Material.STICK,
          Material.PAPER,
          Material.STICK,
          Material.PAPER,
          Material.AIR,
          Material.PAPER,
          Material.STICK,
          Material.PAPER,
          Material.STICK
  };

  public static void loadTextures(File giftFile, List<PlayerProfile> giftTextures) {
    try {
      YamlConfiguration giftConfig = new YamlConfiguration();
      giftConfig.load(giftFile);
      List<String> textures = giftConfig.getStringList("textures");
      for (int i = 0; i < textures.size(); i++) {
        String texture = textures.get(i);
        String username = "gift_" + i;
        UUID uuid = UUID.nameUUIDFromBytes(
                ("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8)
        );
        PlayerProfile profile = Bukkit.createProfile(uuid, username);
        profile.setProperty(new ProfileProperty("textures", texture));
        giftTextures.add(profile);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
