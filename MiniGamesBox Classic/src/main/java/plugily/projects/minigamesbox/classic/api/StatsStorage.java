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
package plugily.projects.minigamesbox.classic.api;

import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.placeholder.Placeholder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 * Class for accessing users statistics.
 */
public class StatsStorage {

  private final PluginMain plugin;

  private final Map<String, StatisticType> statistics = new HashMap<>();

  public StatsStorage(PluginMain plugin) {
    this.plugin = plugin;
    loadStats();
  }

  private void loadStats() {
    StatisticType.getStatistics().forEach((s, statisticType) -> {
      statistics.put(s, statisticType);
      loadExternals(statisticType);
    });
  }

  private void loadExternals(StatisticType statisticType) {
    if(statisticType.isPersistent()) {
      plugin.getUserManager().getDatabase().addColumn(statisticType.getName(), statisticType.getDatabaseParameters());
    }
    plugin.getPlaceholderManager().registerPlaceholder(new Placeholder("user_statistic_" + statisticType.getName(), Placeholder.PlaceholderExecutor.ALL) {
      @Override
      public String getValue(Player player) {
        return Integer.toString(getUserStats(player, statisticType));
      }

      @Override
      public String getValue(Player player, PluginArena arena) {
        return Integer.toString(getUserStats(player, statisticType));
      }
    });
  }

  /**
   * Get all UUID's sorted ascending by Statistic Type
   *
   * @param stat Statistic type to get (kills, deaths etc.)
   * @return Map of UUID keys and Integer values sorted in ascending order of requested statistic type
   */
  public Map<UUID, Integer> getStats(StatisticType stat) {
    return plugin.getUserManager().getDatabase().getStats(stat);
  }

  /**
   * Get user statistic based on StatisticType
   *
   * @param player        Online player to get data from
   * @param statisticType Statistic type to get (kills, deaths etc.)
   * @return int of statistic
   * @see StatisticType
   */
  public int getUserStats(Player player, StatisticType statisticType) {
    return plugin.getUserManager().getUser(player).getStatistic(statisticType);
  }


  /**
   * Returns whether option value is true or false
   *
   * @param key option to get value from
   * @return true or false based on user configuration
   */
  public String getStatisticName(String key) {
    StatisticType statisticType = statistics.get(key);

    if(statisticType == null) {
      throw new IllegalStateException("Statistic with key " + key + " does not exist");
    }

    return statisticType.getName();
  }


  /**
   * Returns whether option value is true or false
   *
   * @param key option to get value from
   * @return true or false based on user configuration
   */
  public StatisticType getStatisticType(String key) {
    StatisticType statisticType = statistics.get(key);

    if(statisticType == null) {
      throw new IllegalStateException("Statistic with key " + key + " does not exist");
    }

    return statisticType;
  }

  /**
   * Register a new statistic
   *
   * @param key           The key of the statistic
   * @param statisticType Contains the name and the persistent
   */
  public void registerStatistic(String key, StatisticType statisticType) {
    if(statistics.containsKey(key)) {
      throw new IllegalStateException("Statistic with key " + key + " was already registered");
    }
    loadExternals(statisticType);
    statistics.put(key, statisticType);
  }

  /**
   * Remove statistics that are not protected
   *
   * @param name The name of the Option
   */
  public void unregisterStatistic(String name) {
    StatisticType statisticType = statistics.get(name);
    if(statisticType == null) {
      return;
    }
    if(statisticType.isProtected()) {
      throw new IllegalStateException("Protected statistics cannot be removed!");
    }
    if(statisticType.isPersistent()) {
      plugin.getUserManager().getDatabase().dropColumn(statisticType.getName());
    }
    statistics.remove(name);
  }

  public Map<String, StatisticType> getStatistics() {
    return Collections.unmodifiableMap(statistics);
  }


}
