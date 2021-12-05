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

package plugily.projects.minigamesbox.classic.preferences;

import org.bukkit.configuration.ConfigurationSection;
import plugily.projects.minigamesbox.classic.PluginMain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 12.09.2021
 */
public class ConfigPreferences {

  private final PluginMain plugin;
  private final Map<String, ConfigOption> options = new HashMap<>();
  private final Map<String, Integer> customPermissions = new HashMap<>();

  public ConfigPreferences(PluginMain plugin) {
    this.plugin = plugin;
    loadOptions();

    //load custom permissions
    ConfigurationSection section = plugin.getConfig().getConfigurationSection("Custom-Permissions");
    if(section != null) {
      for(String key : section.getKeys(false)) {
        customPermissions.put(key, section.getInt(key));
        plugin.getDebugger().debug("Loaded custom permission {0}", key);
      }
    }
  }

  private void loadOptions() {
    ConfigOption.getOptions().forEach((s, option) -> options.put(s, new ConfigOption(option.getPath(), plugin.getConfig().getBoolean(option.getPath(), option.getValue()), option.isProtected())));
  }

  /**
   * Returns whether option value is true or false
   *
   * @param name option to get value from
   * @return true or false based on user configuration
   */
  public boolean getOption(String name) {
    if(!options.containsKey(name)) {
      throw new IllegalStateException("Option with name " + name + " does not exist");
    }
    return options.get(name).getValue();
  }

  public Map<String, Integer> getCustomPermissions() {
    return customPermissions;
  }


  /**
   * Register a new config option
   *
   * @param name   The name of the Option
   * @param option Contains the path and the default value
   */
  public void registerOption(String name, ConfigOption option) {
    if(options.containsKey(name)) {
      throw new IllegalStateException("Option with path " + name + " was already registered");
    }
    options.put(name, option);
  }

  /**
   * Remove config options that are not protected
   *
   * @param name The name of the Option
   */
  public void unregisterOption(String name) {
    ConfigOption option = options.get(name);
    if(option == null) {
      return;
    }
    if(option.isProtected()) {
      throw new IllegalStateException("Protected options cannot be removed!");
    }
    options.remove(name);
  }

  public Map<String, ConfigOption> getOptions() {
    return Collections.unmodifiableMap(options);
  }

}
