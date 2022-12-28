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


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 23.10.2021
 */
public class ArenaOption {
  private static final Map<String, ArenaOption> options = new HashMap<>();


  //found in arena.yml
  static {
    /**
     * Current arena timer, ex. 30 seconds before game starts.
     */
    options.put("TIMER", new ArenaOption("null", 0, true));
    /**
     * Minimum players in arena needed to start.
     */
    options.put("MINIMUM_PLAYERS", new ArenaOption("minimumplayers", 1, true));
    /**
     * Maximum players arena can hold, users with full games permission can bypass this!
     */
    options.put("MAXIMUM_PLAYERS", new ArenaOption("maximumplayers", 16, true));
    /**
     * Value for toggling boss bar message status.
     */
    options.put("BAR_TOGGLE_VALUE", new ArenaOption("null", 0, true));
    options.put("BOSSBAR_INTERVAL", new ArenaOption("null", 10, true));
  }

  private final String path;
  private Integer value;
  private final boolean protectedOption;


  public ArenaOption(String path, int value, boolean protectedOption) {
    this.path = path;
    this.value = value;
    this.protectedOption = protectedOption;
  }

  public ArenaOption(String path, int value) {
    this.path = path;
    this.value = value;
    this.protectedOption = false;
  }

  public String getPath() {
    return path;
  }

  /**
   * @return default value of option if absent in config
   */
  public Integer getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  /**
   * @return whether option is protected and cannot be unregistered
   */
  public boolean isProtected() {
    return protectedOption;
  }

  public static Map<String, ArenaOption> getOptions() {
    return Collections.unmodifiableMap(options);
  }
}
