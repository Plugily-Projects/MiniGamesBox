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

import plugily.projects.minigamesbox.api.stats.IStatisticType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 19.09.2021
 */
public class StatisticType implements IStatisticType {

  private static final Map<String, StatisticType> statistics = new HashMap<>();

  static {
    statistics.put("WINS", new StatisticType("wins", true, "int(11) NOT NULL DEFAULT '0'", true));
    statistics.put("LOSES", new StatisticType("loses", true, "int(11) NOT NULL DEFAULT '0'", true));
    statistics.put("GAMES_PLAYED", new StatisticType("games_played", true, "int(11) NOT NULL DEFAULT '0'", true));
    statistics.put("LEVEL", new StatisticType("level", true, "int(11) NOT NULL DEFAULT '0'", true));
    statistics.put("EXP", new StatisticType("exp", true, "int(11) NOT NULL DEFAULT '0'", true));
    statistics.put("NEXT_LEVEL_EXP", new StatisticType("next_level_exp", true, "int(11) NOT NULL DEFAULT '0'", true));
  }

  private final String name;
  private final boolean persistent;
  private final String databaseParameters;
  private final boolean protectedStatistic;

  public StatisticType(String name, boolean persistent, String databaseParameters, boolean protectedStatistic) {
    this.name = name;
    this.persistent = persistent;
    this.databaseParameters = databaseParameters;
    this.protectedStatistic = protectedStatistic;
  }

  public StatisticType(String name, boolean persistent, String databaseParameters) {
    this.name = name;
    this.persistent = persistent;
    this.databaseParameters = databaseParameters;
    this.protectedStatistic = false;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDatabaseParameters() {
    return databaseParameters;
  }

  /**
   * @return default persistent of option if absent in config
   */
  @Override
  public boolean isPersistent() {
    return persistent;
  }

  /**
   * @return whether option is protected and cannot be unregistered
   */
  @Override
  public boolean isProtected() {
    return protectedStatistic;
  }

  public static Map<String, IStatisticType> getStatistics() {
    return Collections.unmodifiableMap(statistics);
  }
}
