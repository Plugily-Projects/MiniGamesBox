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

package plugily.projects.minigamesbox.classic.kits.basekits;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;

import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public abstract class Kit {

  private static final PluginMain plugin = JavaPlugin.getPlugin(PluginMain.class);

  private final FileConfiguration kitsConfig = ConfigUtils.getConfig(plugin, "kits");

  private String name = "";

  private String key = "";
  private boolean unlockedOnDefault = false;
  private String[] description = new String[0];

  protected Kit() {
  }

  public Kit(String name) {
    setName(name);
    setKey(name);
  }

  public Kit(String name, String key) {
    setName(name);
    setKey(key);
  }

  public abstract boolean isUnlockedByPlayer(Player p);

  public boolean isUnlockedOnDefault() {
    return unlockedOnDefault;
  }

  public void setUnlockedOnDefault(boolean unlockedOnDefault) {
    this.unlockedOnDefault = unlockedOnDefault;
  }

  /**
   * @return main plugin
   */
  public PluginMain getPlugin() {
    return plugin;
  }

  /**
   * @return config file of kits
   */
  public FileConfiguration getKitsConfig() {
    return kitsConfig;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if(name != null) {
      this.name = name;
    }
  }

  public void setKey(String key) {
    this.key = key;
  }


  public String getKey() {
    return key;
  }

  public String[] getDescription() {
    return description.clone();
  }

  public void setDescription(String[] description) {
    if(description != null) {
      this.description = description.clone();
    }
  }

  public void setDescription(List<String> description) {
    if(description != null) {
      this.description = description.toArray(new String[0]);
    }
  }

  public abstract ItemStack getItemStack();

  public abstract void giveKitItems(Player player);

  public abstract void reStock(Player player);

}
