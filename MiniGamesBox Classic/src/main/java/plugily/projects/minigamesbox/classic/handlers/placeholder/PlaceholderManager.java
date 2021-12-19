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


package plugily.projects.minigamesbox.classic.handlers.placeholder;

import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;

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
    new PAPIPlaceholders(plugin);
    insertDefaultPlaceholders();
  }

  private void insertDefaultPlaceholders() {
    registerPlaceholder(new Placeholder("arena_players_online", Placeholder.PlaceholderExecutor.PLACEHOLDER_API) {
      @Override
      public String getValue(Player player) {
        return Integer.toString(plugin.getArenaRegistry().getArenaPlayersOnline());
      }
    });
    registerPlaceholder(new Placeholder("exp_to_next_level", Placeholder.PlaceholderExecutor.PLACEHOLDER_API) {
      @Override
      public String getValue(Player player) {
        return Double.toString(Math.ceil(Math.pow(50 * plugin.getStatsStorage().getUserStats(player, plugin.getStatsStorage().getStatisticType("LEVEL")), 1.5)));
      }
    });
    registerPlaceholder(new Placeholder("players", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.PLACEHOLDER_API) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return Integer.toString(arena.getPlayers().size());
      }
    });
    registerPlaceholder(new Placeholder("max_players", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.PLACEHOLDER_API) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return Integer.toString(arena.getMaximumPlayers());
      }
    });
    registerPlaceholder(new Placeholder("state", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.PLACEHOLDER_API) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return arena.getArenaState().toString().toLowerCase();
      }
    });
    registerPlaceholder(new Placeholder("state_pretty", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.PLACEHOLDER_API) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return arena.getArenaState().getPlaceholder();
      }
    });
    registerPlaceholder(new Placeholder("mapname", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.PLACEHOLDER_API) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return arena.getMapName();
      }
    });
    registerPlaceholder(new Placeholder("timer", Placeholder.PlaceholderType.ARENA, Placeholder.PlaceholderExecutor.PLACEHOLDER_API) {
      @Override
      public String getValue(Player player, PluginArena arena) {
        return Integer.toString(arena.getTimer());
      }
    });
  }

  public void registerPlaceholder(Placeholder command) {
    switch(command.getPlaceholderExecutor()) {
      case PLACEHOLDER_API:
        registeredPAPIPlaceholders.add(command);
        break;
      case INTERNAL:
        registeredInternalPlaceholders.add(command);
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
