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

package plugily.projects.minigamesbox.classic.arena.options;

import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.placeholder.Placeholder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 23.12.2021
 */
public class ArenaOptionManager {

  private final PluginMain plugin;
  private final Map<String, ArenaOption> arenaOptions = new HashMap<>();


  public ArenaOptionManager(PluginMain plugin) {
    this.plugin = plugin;
    loadArenaOptions();
  }

  private void loadArenaOptions() {
    ArenaOption.getOptions().forEach((s, option) -> {
      arenaOptions.put(s, new ArenaOption(option.getPath(), plugin.getConfig().getInt(option.getPath(), option.getValue()), option.isProtected()));
      loadExternals(s, option);
    });
  }

  private void loadExternals(String key, ArenaOption arenaOption) {
    plugin.getPlaceholderManager().registerPlaceholder(new Placeholder("option_" + key.toLowerCase(), Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return String.valueOf(arenaOption.getValue());
      }

      @Override
      public String getValue(PluginArena arena) {
        return String.valueOf(arenaOption.getValue());
      }
    });
  }


  /**
   * Register a new arena option
   *
   * @param name   The name of the arena option
   * @param option Contains the path and the default value
   */
  public void registerArenaOption(String name, ArenaOption option) {
    if(arenaOptions.containsKey(name)) {
      throw new IllegalStateException("Arena option with path " + name + " was already registered");
    }
    arenaOptions.put(name, new ArenaOption(option.getPath(), plugin.getConfig().getInt(option.getPath(), option.getValue()), option.isProtected()));
    loadExternals(name, option);
  }

  /**
   * Remove arena options that are not protected
   *
   * @param name The name of the arena option
   */
  public void unregisterArenaOption(String name) {
    ArenaOption option = arenaOptions.get(name);
    if(option == null) {
      return;
    }
    if(option.isProtected()) {
      throw new IllegalStateException("Protected arena option " + name + " cannot be removed!");
    }
    arenaOptions.remove(name);
  }

  public Map<String, ArenaOption> getArenaOptions() {
    return Collections.unmodifiableMap(arenaOptions);
  }

}
