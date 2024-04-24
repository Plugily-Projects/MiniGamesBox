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


package plugily.projects.minigamesbox.classic.handlers.placeholder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.states.ArenaState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class PlaceholderManager {

  private final PluginMain plugin;
  private List<Placeholder> registeredPAPIPlaceholders = new ArrayList<>();
  private List<Placeholder> registeredInternalPlaceholders = new ArrayList<>();

  public PlaceholderManager(PluginMain plugin) {
    this.plugin = plugin;
    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      plugin.getDebugger().debug(plugin.getPluginMessagePrefix() + "Hooking into PlaceholderAPI");
      new PAPIPlaceholders(plugin);
    }
    insertDefaultPlaceholders();
  }

  private void insertDefaultPlaceholders() {
    registerPlaceholder(new Placeholder("arena_players_online", Placeholder.PlaceholderExecutor.ALL) {

      @Override
      public String getValue() {
        return Integer.toString(plugin.getArenaRegistry().getArenaPlayersOnline());
      }

      @Override
      public String getValue(Player player) {
        return getValue();
      }

      @Override
      public String getValue(IPluginArena arena) {
        return getValue();
      }

      @Override
      public String getValue(Player player, IPluginArena arena) {
        return getValue();
      }
    });
    registerPlaceholder(new Placeholder("players", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.PLACEHOLDER_API) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return Integer.toString(arena.getPlayers().size());
      }

      @Override
      public String getValue(IPluginArena arena) {
        return Integer.toString(arena.getPlayers().size());
      }
    });
    //dup of arena_option_max_players
    registerPlaceholder(new Placeholder("max_players", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.PLACEHOLDER_API) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return Integer.toString(arena.getMaximumPlayers());
      }

      @Override
      public String getValue(IPluginArena arena) {
        return Integer.toString(arena.getMaximumPlayers());
      }
    });
    registerPlaceholder(new Placeholder("state", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.PLACEHOLDER_API) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return arena.getArenaState().toString().toLowerCase();
      }

      @Override
      public String getValue(IPluginArena arena) {
        return arena.getArenaState().toString().toLowerCase();
      }
    });
    registerPlaceholder(new Placeholder("state_pretty", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.PLACEHOLDER_API) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return ArenaState.getPlaceholder(arena.getArenaState());
      }

      @Override
      public String getValue(IPluginArena arena) {
        return ArenaState.getPlaceholder(arena.getArenaState());
      }
    });
    registerPlaceholder(new Placeholder("name", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.PLACEHOLDER_API) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return arena.getMapName();
      }

      @Override
      public String getValue(IPluginArena arena) {
        return arena.getMapName();
      }
    });
    registerPlaceholder(new Placeholder("timer", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.PLACEHOLDER_API) {
      @Override
      public String getValue(Player player, IPluginArena arena) {
        return Integer.toString(arena.getTimer());
      }

      @Override
      public String getValue(IPluginArena arena) {
        return Integer.toString(arena.getTimer());
      }
    });
    registerPlaceholder(new Placeholder("user_kit", Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player) {
        if (!plugin.getConfigPreferences().getOption("KITS")) {
          return null;
        }
        return plugin.getUserManager().getUser(player).getKit().getName();
      }

      @Override
      public String getValue(Player player, IPluginArena arena) {
        if (!plugin.getConfigPreferences().getOption("KITS")) {
          return null;
        }
        return plugin.getUserManager().getUser(player).getKit().getName();
      }
    });

  }

  public void registerPlaceholder(Placeholder placeholder) {
    switch (placeholder.getPlaceholderExecutor()) {
      case PLACEHOLDER_API:
        registeredPAPIPlaceholders.add(placeholder);
        break;
      case INTERNAL:
        registeredInternalPlaceholders.add(placeholder);
        break;
      case ALL:
        registeredPAPIPlaceholders.add(placeholder);
        registeredInternalPlaceholders.add(placeholder);
        break;
      default:
        break;
    }
  }

  public List<Placeholder> getRegisteredInternalPlaceholders() {
    return Collections.unmodifiableList(registeredInternalPlaceholders);
  }

  public List<Placeholder> getRegisteredPAPIPlaceholders() {
    return Collections.unmodifiableList(registeredPAPIPlaceholders);
  }
}
