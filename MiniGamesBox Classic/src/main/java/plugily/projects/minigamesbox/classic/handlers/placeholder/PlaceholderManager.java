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

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.classic.Main;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class PlaceholderManager extends PlaceholderExpansion {

  private final Main plugin;
  private List<Placeholder> registeredPlaceholders = new ArrayList<>();

  public PlaceholderManager(Main plugin) {
    this.plugin = plugin;
    insertDefaultPlaceholders();
    register();
  }

  private void insertDefaultPlaceholders() {
    registerPlaceholder(new Placeholder("kills") {
      @Override
      public String getValue(Player player) {
        return Integer.toString(plugin.getStatsStorage().getUserStats(player, plugin.getStatsStorage().getStatisticType("KILLS")));
      }
    });
    registerPlaceholder(new Placeholder("deaths") {
      @Override
      public String getValue(Player player) {
        return Integer.toString(plugin.getStatsStorage().getUserStats(player, plugin.getStatsStorage().getStatisticType("DEATHS")));
      }
    });
    registerPlaceholder(new Placeholder("games_played") {
      @Override
      public String getValue(Player player) {
        return Integer.toString(plugin.getStatsStorage().getUserStats(player, plugin.getStatsStorage().getStatisticType("GAMES_PLAYED")));
      }
    });
    registerPlaceholder(new Placeholder("level") {
      @Override
      public String getValue(Player player) {
        return Integer.toString(plugin.getStatsStorage().getUserStats(player, plugin.getStatsStorage().getStatisticType("LEVEL")));
      }
    });
    registerPlaceholder(new Placeholder("exp") {
      @Override
      public String getValue(Player player) {
        return Integer.toString(plugin.getStatsStorage().getUserStats(player, plugin.getStatsStorage().getStatisticType("XP")));
      }
    });
    registerPlaceholder(new Placeholder("arena_players_online") {
      @Override
      public String getValue(Player player) {
        return Integer.toString(ArenaRegistry.getArenaPlayersOnline());
      }
    });
    registerPlaceholder(new Placeholder("exp_to_next_level") {
      @Override
      public String getValue(Player player) {
        return Double.toString(Math.ceil(Math.pow(50 * plugin.getStatsStorage().getUserStats(player, plugin.getStatsStorage().getStatisticType("LEVEL")), 1.5)));
      }
    });
    registerPlaceholder(new Placeholder("players", Placeholder.PlaceholderType.ARENA) {
      @Override
      public String getValue(Player player, Arena arena) {
        return Integer.toString(arena.getPlayers().size());
      }
    });
    registerPlaceholder(new Placeholder("max_players", Placeholder.PlaceholderType.ARENA) {
      @Override
      public String getValue(Player player, Arena arena) {
        return Integer.toString(arena.getMaximumPlayers());
      }
    });
    registerPlaceholder(new Placeholder("state", Placeholder.PlaceholderType.ARENA) {
      @Override
      public String getValue(Player player, Arena arena) {
        return arena.getArenaState().toString().toLowerCase();
      }
    });
    registerPlaceholder(new Placeholder("state_pretty", Placeholder.PlaceholderType.ARENA) {
      @Override
      public String getValue(Player player, Arena arena) {
        return arena.getArenaState().getPlaceholder();
      }
    });
    registerPlaceholder(new Placeholder("mapname", Placeholder.PlaceholderType.ARENA) {
      @Override
      public String getValue(Player player, Arena arena) {
        return arena.getMapName();
      }
    });

  }

  public void registerPlaceholder(Placeholder command) {
    registeredPlaceholders.add(command);
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public @NotNull String getIdentifier() {
    return "villagedefense";
  }

  @Override
  public @NotNull String getAuthor() {
    return "Plugily Projects";
  }

  @Override
  public @NotNull String getVersion() {
    return "1.1.0";
  }

  @Override
  public String onPlaceholderRequest(Player player, @NotNull String id) {
    if(player == null) {
      return null;
    }
    for(Placeholder placeholder : registeredPlaceholders) {
      if(placeholder.getPlaceholderType() == Placeholder.PlaceholderType.ARENA) {
        continue;
      }
      if(id.toLowerCase().equalsIgnoreCase(placeholder.getId())) {
        return placeholder.getValue(player);
      }
    }
    String[] data = id.split(":", 2);
    if(data.length < 2) {
      return null;
    }
    Arena arena = ArenaRegistry.getArena(data[0]);
    if(arena == null) {
      return null;
    }
    for(Placeholder placeholder : registeredPlaceholders) {
      if(placeholder.getPlaceholderType() == Placeholder.PlaceholderType.GLOBAL) {
        continue;
      }

      if(data[1].toLowerCase().equalsIgnoreCase(placeholder.getId())) {
        return placeholder.getValue(player);
      }
    }
    return null;
  }
}
