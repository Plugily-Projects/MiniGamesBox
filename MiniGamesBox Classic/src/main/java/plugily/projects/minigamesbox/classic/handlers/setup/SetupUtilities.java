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


package plugily.projects.minigamesbox.classic.handlers.setup;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class SetupUtilities {

  private final PluginMain plugin;
  private final FileConfiguration config;
  private final PluginArena arena;

  SetupUtilities(PluginMain plugin, FileConfiguration config, PluginArena arena) {
    this.plugin = plugin;
    this.config = config;
    this.arena = arena;
  }

  public String isOptionDone(String path) {
    String option = config.getString(path);

    if(option != null) {
      return color("&a&l✔ Completed &7(value: &8" + option + "&7)");
    }

    return color("&c&l✘ Not Completed");
  }

  public String isOptionDoneSection(String path, int minimum) {
    org.bukkit.configuration.ConfigurationSection section = config.getConfigurationSection(path);

    if(section != null) {
      int keysSize = section.getKeys(false).size();

      if(keysSize < minimum) {
        return color("&c&l✘ Not Completed | &cPlease add more locations");
      }

      return color("&a&l✔ Completed &7(value: &8" + keysSize + "&7)");
    }

    return color("&c&l✘ Not Completed");
  }

  public String isOptionDoneBool(String path) {
    String option = config.getString(path);

    if(option != null) {
      if(Bukkit.getServer().getWorlds().get(0).getSpawnLocation().equals(LocationSerializer.getLocation(option))) {
        return color("&c&l✘ Not Completed");
      }

      return color("&a&l✔ Completed");
    }

    return color("&c&l✘ Not Completed");
  }

  public int getMinimumValueHigherThanZero(String path) {
    int amount = config.getInt("instances." + arena.getId() + "." + path);
    return amount == 0 ? 1 : amount;
  }

  private String color(String msg) {
    return plugin.getChatManager().colorRawMessage(msg);
  }

}
