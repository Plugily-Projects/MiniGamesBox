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

package plugily.projects.minigamesbox.api.user.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugily.projects.minigamesbox.api.stats.IStatisticType;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.database.MysqlDatabase;

import java.util.Map;
import java.util.UUID;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public interface UserDatabase {

  /**
   * Saves player statistic into yaml or MySQL storage based on user choice
   *
   * @param user user to retrieve statistic from
   * @param stat stat to save to storage
   */
  void saveStatistic(IUser user, IStatisticType stat);

  /**
   * Saves player statistic into yaml or MySQL storage based on user choice
   *
   * @param user user to retrieve statistic from
   */
  void saveAllStatistic(IUser user);

  /**
   * Loads player statistic from yaml or MySQL storage based on user choice
   *
   * @param user user to load statistic for
   */
  void loadStatistics(IUser user);

  /**
   * Add a column on mysql database instances, skips for file stats
   *
   * @param columnName       The name of the column
   * @param columnProperties The column specification
   */
  void addColumn(String columnName, String columnProperties);

  /**
   * Drop a column on mysql database instances, skips for file stats
   *
   * @param columnName The name of the column
   */
  void dropColumn(String columnName);

  /**
   * Get all UUID's sorted ascending by Statistic Type
   *
   * @param stat Statistic type to get (kills, deaths etc.)
   * @return Map of UUID keys and Integer values sorted in ascending order of requested statistic type
   */
  @NotNull
  Map<UUID, Integer> getStats(IStatisticType stat);

  /**
   * Disable the database
   */
  void disable();


  /**
   * Get the MySQL Database if there is any
   *
   * @return MySQL database
   */
  MysqlDatabase getMySQLDatabase();

  /**
   * Get the name of the player providing the UUID
   *
   * @param uuid the UUID
   * @return the player's name
   */
  @Nullable
  String getPlayerName(UUID uuid);
}
