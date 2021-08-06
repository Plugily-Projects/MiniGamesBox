/*
 * MiniGamesBox - Library box with massive content that could be seen as minigames core.
 * Copyright (C)  2021  Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package plugily.projects.minigamesbox.classic.serialization;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import plugily.projects.minigamesbox.classic.compat.ServerVersion;
import plugily.projects.minigamesbox.classic.compat.VersionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 09.03.2019
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
    if(inventory == null) {
      return false;
    }
    try {
      path.mkdirs();

      File invFile = new File(path, player.getUniqueId().toString() + ".invsave");
      if(invFile.exists()) {
        invFile.delete();
      }

      FileConfiguration invConfig = YamlConfiguration.loadConfiguration(invFile);

      invConfig.set("ExperienceProgress", player.getExp());
      invConfig.set("ExperienceLevel", player.getLevel());
      invConfig.set("Current health", player.getHealth());
      invConfig.set("Max health", VersionUtils.getMaxHealth(player));
      invConfig.set("Food", player.getFoodLevel());
      invConfig.set("Saturation", player.getSaturation());
      invConfig.set("Fire ticks", player.getFireTicks());
      invConfig.set("GameMode", player.getGameMode().name());
      invConfig.set("Allow flight", player.getAllowFlight());

      invConfig.set("Size", inventory.getSize());
      invConfig.set("Max stack size", inventory.getMaxStackSize());
      List<String> activePotions = new ArrayList<>();
      for(PotionEffect potion : player.getActivePotionEffects()) {
        activePotions.add(potion.getType().getName() + "#" + potion.getDuration() + "#" + potion.getAmplifier());
      }
      invConfig.set("Active potion effects", activePotions);
      if(inventory.getHolder() instanceof Player) {
        invConfig.set("Holder", inventory.getHolder().getName());
      }

      ItemStack[] invContents = inventory.getContents();
      for(int i = 0; i < invContents.length; i++) {
        ItemStack itemInInv = invContents[i];
        if(itemInInv != null && itemInInv.getType() != Material.AIR) {
          invConfig.set("Slot " + i, itemInInv);
        }
      }

      if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
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
      return true;
    } catch(Exception ex) {
      ex.printStackTrace();
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

      InventoryHolder invHolder = invConfig.contains("Holder") ? Bukkit.getPlayer(invConfig.getString("Holder")) : null;
      Inventory inventory = Bukkit.getServer().createInventory(invHolder, InventoryType.PLAYER);
      inventory.setMaxStackSize(invConfig.getInt("Max stack size", 64));
      try {
        ItemStack[] invContents = new ItemStack[invSize];
        for(int i = 0; i < invSize; i++) {
          if(invConfig.contains("Slot " + i)) {
            invContents[i] = invConfig.getItemStack("Slot " + i);
          } else {
            invContents[i] = new ItemStack(Material.AIR);
          }
        }
        inventory.setContents(invContents);

      } catch(Exception ex) {
        ex.printStackTrace();
        Bukkit.getConsoleSender().sendMessage("Cannot save inventory of player! Could not get armor!");
        Bukkit.getConsoleSender().sendMessage("Disable inventory saving option in config.yml or restart the server!");
      }
      file.delete();
      return inventory;
    } catch(Exception ex) {
      ex.printStackTrace();
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
    try {
      FileConfiguration invConfig = YamlConfiguration.loadConfiguration(file);
      try {
        ItemStack[] armor = new ItemStack[player.getInventory().getArmorContents().length];
        for(int i = 0; i < armor.length; i++) {
          if(invConfig.contains("Armor " + i)) {
            armor[i] = invConfig.getItemStack("Armor " + i);
          } else {
            armor[i] = new ItemStack(Material.AIR);
          }
        }
        player.getInventory().setArmorContents(armor);
        if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
          ItemStack stack = invConfig.getItemStack("Offhand", new ItemStack(Material.AIR));
          player.getInventory().setItemInOffHand(stack);
        }
        VersionUtils.setMaxHealth(player, invConfig.getDouble("Max health"));
        player.setExp(0);
        player.setLevel(0);
        player.setLevel(invConfig.getInt("ExperienceLevel"));
        player.setExp(Float.parseFloat(invConfig.getString("ExperienceProgress")));
        player.setHealth(invConfig.getDouble("Current health"));
        player.setFoodLevel(invConfig.getInt("Food"));
        player.setSaturation(Float.parseFloat(invConfig.getString("Saturation")));
        player.setFireTicks(invConfig.getInt("Fire ticks"));
        player.setGameMode(GameMode.valueOf(invConfig.getString("GameMode", "")));
        player.setAllowFlight(invConfig.getBoolean("Allow flight"));
        List<String> activePotions = invConfig.getStringList("Active potion effects");
        for(String potion : activePotions) {
          String[] splited = potion.split("#", 3);
          player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(splited[0]), Integer.parseInt(splited[1]), Integer.parseInt(splited[2])));
        }
      } catch(Exception ignored) {
      }

      Inventory inventory = getInventoryFromFile(plugin, stringId);

      for(int i = 0; i < inventory.getContents().length; i++) {
        ItemStack item = inventory.getItem(i);

        if(item != null) {
          player.getInventory().setItem(i, item);
        }
      }

      player.updateInventory();
    } catch(Exception ignored) {
      //ignore any exceptions
    }
  }

}
