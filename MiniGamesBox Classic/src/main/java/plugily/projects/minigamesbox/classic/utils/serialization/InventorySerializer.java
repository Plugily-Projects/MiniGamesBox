/*
 *  MiniGamesBox - Library box with massive content that could be seen as minigames core.
 *  Copyright (C) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.minigamesbox.classic.utils.serialization;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class InventorySerializer {

  private InventorySerializer() {
  }

  /**
   * Saves player inventory to file in plugin directory
   *
   * @param plugin javaplugin to get data folder
   * @param player player to save data
   * @return true if saved properly, false if inventory is null or couldn't save
   */
  public static boolean saveInventoryToFile(JavaPlugin plugin, Player player) {
    PlayerInventory inventory = player.getInventory();
    File path = new File(plugin.getDataFolder(), "inventories");
    path.mkdirs();

    try {
      File invFile = new File(path, player.getUniqueId() + ".invsave");
      if(invFile.exists()) {
        invFile.delete();
      }


      FileConfiguration invConfig = YamlConfiguration.loadConfiguration(invFile);

      invConfig.set("ExperienceProgress", player.getExp());
      invConfig.set("ExperienceLevel", player.getLevel());
      invConfig.set("Current health", player.getHealth());
      invConfig.set("Food", player.getFoodLevel());
      invConfig.set("Saturation", player.getSaturation());
      invConfig.set("Fire ticks", player.getFireTicks());
      invConfig.set("GameMode", player.getGameMode().name());
      invConfig.set("Allow flight", player.getAllowFlight());
      invConfig.set("Flight speed", player.getFlySpeed());
      invConfig.set("Walk speed", player.getWalkSpeed());

      invConfig.set("Size", inventory.getSize());
      invConfig.set("Max stack size", inventory.getMaxStackSize());
      Collection<PotionEffect> activeEffects = player.getActivePotionEffects();
      List<String> activePotions = new ArrayList<>(activeEffects.size());

      double maxHealth = VersionUtils.getMaxHealth(player);

      for(PotionEffect potion : activeEffects) {
        activePotions.add(potion.getType().getName() + "#" + potion.getDuration() + "#" + potion.getAmplifier());
        if(potion.getType().equals(PotionEffectType.HEALTH_BOOST)) {
          // Health boost effect gives +2 hearts per level, health is counted in half hearts so amplifier * 4
          maxHealth -= (potion.getAmplifier() + 1) * 4;
        }
      }
      invConfig.set("Active potion effects", activePotions);
      invConfig.set("Max health", maxHealth);

      org.bukkit.entity.HumanEntity holder = inventory.getHolder();
      if(holder instanceof Player) {
        invConfig.set("Holder", holder.getName());
      }

      ItemStack[] invContents = inventory.getContents();
      for(int i = 0; i < invContents.length; i++) {
        ItemStack itemInInv = invContents[i];
        if(itemInInv != null && itemInInv.getType() != Material.AIR) {
          if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_8_8) && itemInInv.getItemMeta() instanceof SkullMeta) {
            SkullMeta skullMeta = ((SkullMeta) itemInInv.getItemMeta());
            if(skullMeta.getOwner() != null) {
              try {
                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                Object profile = profileField.get(skullMeta);

                Field name = profile.getClass().getDeclaredField("name");
                name.setAccessible(true);
                name.set(profile, "plugily");

                itemInInv.setItemMeta(skullMeta);
              } catch(NoSuchFieldException | IllegalAccessException | NullPointerException e) {
                itemInInv = XMaterial.BEDROCK.parseItem();
              }
            }
          }
          invConfig.set("Slot " + i, itemInInv);
        }
      }

      if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9)) {
        invConfig.set("Offhand", inventory.getItemInOffHand());
      }

      ItemStack[] armorContents = inventory.getArmorContents();
      for(int b = 0; b < armorContents.length; b++) {
        ItemStack itemStack = armorContents[b];
        if(itemStack != null && itemStack.getType() != Material.AIR) {
          invConfig.set("Armor " + b, itemStack);
        }
      }

      invConfig.save(invFile);
      plugin.getLogger().log(Level.INFO, "Saved inventory of {0}", player.getName());
      return true;
    } catch(Exception ex) {
      Bukkit.getConsoleSender().sendMessage("Cannot save inventory of player!");
      Bukkit.getConsoleSender().sendMessage("Disable inventory saving option in config.yml or restart the server!");
      return false;
    }
  }

  private static Inventory getInventoryFromFile(JavaPlugin plugin, String uuid) {
    File file = new File(plugin.getDataFolder(), "inventories" + File.separator + uuid + ".invsave");
    if(!file.exists() || file.isDirectory() || !file.getAbsolutePath().endsWith(".invsave")) {
      return Bukkit.createInventory(null, 9);
    }
    try {
      FileConfiguration invConfig = YamlConfiguration.loadConfiguration(file);
      int invSize = invConfig.getInt("Size", 36);
      if(invSize > 36 || invSize < 1) {
        invSize = 36;
      }

      String holder = invConfig.getString("Holder");
      Inventory inventory = plugin.getServer().createInventory(holder != null ? Bukkit.getPlayer(holder) : null, InventoryType.PLAYER);
      inventory.setMaxStackSize(invConfig.getInt("Max stack size", 64));
      ItemStack[] invContents = new ItemStack[invSize];
      for(int i = 0; i < invSize; i++) {
        if(invConfig.contains("Slot " + i)) {
          invContents[i] = invConfig.getItemStack("Slot " + i);
        } else {
          invContents[i] = new ItemStack(Material.AIR);
        }
      }
      try {
        inventory.setContents(invContents);
      } catch(IllegalArgumentException ex) {
        Bukkit.getConsoleSender().sendMessage("Cannot get inventory of player! Inventory has more items than the default content size.");
        Bukkit.getConsoleSender().sendMessage("Disable inventory saving option in config.yml or restart the server!");
      }
      file.delete();
      return inventory;
    } catch(Exception ex) {
      Bukkit.getConsoleSender().sendMessage("Cannot save inventory of player!");
      Bukkit.getConsoleSender().sendMessage("Disable inventory saving option in config.yml or restart the server!");
      return Bukkit.createInventory(null, 9);
    }
  }

  /**
   * Loads inventory of player from data folder
   *
   * @param plugin javaplugin to get data folder
   * @param player load inventory of this player
   */
  public static void loadInventory(JavaPlugin plugin, Player player) {
    String stringId = player.getUniqueId().toString();

    File file = new File(plugin.getDataFolder(), "inventories" + File.separator + stringId + ".invsave");
    if(!file.exists() || file.isDirectory() || !file.getAbsolutePath().endsWith(".invsave")) {
      return;
    }

    FileConfiguration invConfig = YamlConfiguration.loadConfiguration(file);
    PlayerInventory playerInventory = player.getInventory();

    try {
      try {
        ItemStack[] armor = new ItemStack[playerInventory.getArmorContents().length];
        for(int i = 0; i < armor.length; i++) {
          if(invConfig.contains("Armor " + i)) {
            armor[i] = invConfig.getItemStack("Armor " + i);
          } else {
            armor[i] = new ItemStack(Material.AIR);
          }
        }
        playerInventory.setArmorContents(armor);
        if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9)) {
          playerInventory.setItemInOffHand(invConfig.getItemStack("Offhand", new ItemStack(Material.AIR)));
        }
        VersionUtils.setMaxHealth(player, invConfig.getDouble("Max health"));
        player.setExp(0);
        player.setLevel(0);
        player.setLevel(invConfig.getInt("ExperienceLevel"));
        try {
          player.setExp(Float.parseFloat(invConfig.getString("ExperienceProgress", "0")));
        } catch(NumberFormatException ignored) {
        }
        player.setHealth(invConfig.getDouble("Current health"));
        player.setFoodLevel(invConfig.getInt("Food"));
        try {
          player.setSaturation(Float.parseFloat(invConfig.getString("Saturation", "0")));
        } catch(NumberFormatException ignored) {
        }
        player.setFireTicks(invConfig.getInt("Fire ticks"));
        GameMode gameMode = GameMode.SURVIVAL;
        try {
          gameMode = GameMode.valueOf(invConfig.getString("GameMode", "").toUpperCase());
        } catch(IllegalArgumentException ignored) {
        }
        player.setGameMode(gameMode);

        player.setAllowFlight(invConfig.getBoolean("Allow flight"));
        player.setWalkSpeed((float) invConfig.getDouble("Walk speed"));
        player.setFlySpeed((float) invConfig.getDouble("Flight speed"));
        List<String> activePotions = invConfig.getStringList("Active potion effects");
        for(String potion : activePotions) {
          String[] splited = potion.split("#", 3);
          if(splited.length == 0)
            continue;

          PotionEffectType effectType = PotionEffectType.getByName(splited[0]);

          if(effectType != null) {
            try {
              player.addPotionEffect(new PotionEffect(effectType, splited.length < 2 ? 30 : Integer.parseInt(splited[1]),
                  splited.length < 3 ? 1 : Integer.parseInt(splited[2])));
            } catch(NumberFormatException ignored) {
            }
          }
        }
      } catch(Exception ignored) {
      }

      Inventory inventory = getInventoryFromFile(plugin, stringId);

      for(int i = 0; i < inventory.getContents().length; i++) {
        ItemStack item = inventory.getItem(i);

        if(item != null) {
          playerInventory.setItem(i, item);
        }
      }

      player.updateInventory();
      plugin.getLogger().log(Level.INFO, "Loaded inventory of {0}", player.getName());
    } catch(Exception ignored) {
      //ignore any exceptions
    }
  }

}
