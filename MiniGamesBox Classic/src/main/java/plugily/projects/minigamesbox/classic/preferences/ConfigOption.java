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

package plugily.projects.minigamesbox.classic.preferences;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 18.09.2021
 */
public class ConfigOption {

  private static final Map<String, ConfigOption> options = new HashMap<>();

  static {
    //LOCALE
    options.put("DEBUG", new ConfigOption("Debug", false, true));
    options.put("DEV_DEBUG", new ConfigOption("Developer-Mode", false, true));
    options.put("BOSSBAR", new ConfigOption("Bossbar.Display", true, true));
    options.put("BUNGEEMODE", new ConfigOption("Bungee-Mode", false, true));
    options.put("INVENTORY_MANAGER", new ConfigOption("Inventory-Manager", true, true));
    options.put("BLOCKED_LEAVE_COMMAND", new ConfigOption("Block.In-Game.Leave", false, true));
    //Commands.Whitelist
    //Commands.Shorter
    options.put("BLOCK_IN_GAME_COMMANDS", new ConfigOption("Block.In-Game.Commands", true, true));
    options.put("BLOCK_IN_GAME_ITEM_MOVE", new ConfigOption("Block.In-Game.Item-Move", true, true));
    options.put("DATABASE", new ConfigOption("Database", false, true));
    options.put("REWARDS", new ConfigOption("Rewards", true, true));
    options.put("PLUGIN_CHAT_FORMAT", new ConfigOption("Plugin-Chat-Format", true, true));
    options.put("SEPARATE_ARENA_CHAT", new ConfigOption("Separate-Arena-Chat", true, true));
    options.put("FIREWORK", new ConfigOption("Firework", true, true));
    options.put("SIGN_BLOCK_STATES", new ConfigOption("Sign-Block-States", true, true));
    options.put("HOLIDAYS", new ConfigOption("Holidays", true, true));
    options.put("POWERUPS", new ConfigOption("Powerups", false, false));
    options.put("KITS", new ConfigOption("Kits", false, false));
    options.put("LEADERBOARDS", new ConfigOption("Leaderboard", true, true));


    options.put("EXTERNAL_PARTIES", new ConfigOption("Parties.External", true, true));
    options.put("PARTIES", new ConfigOption("Parties.Own", false, true));
    options.put("FALL_DAMAGE", new ConfigOption("Damage.Fall", false, true));
    options.put("DROWNING_DAMAGE", new ConfigOption("Damage.Drowning", false, true));
    options.put("FIRE_DAMAGE", new ConfigOption("Damage.Fire", false, true));

    options.put("WEATHER_CYCLE", new ConfigOption("Cycle.Weather", false, true));
    options.put("DAYLIGHT_CYCLE", new ConfigOption("Cycle.Daylight.Enable", false, true));

    //Custom Permission
    //Basic Permission
    //Time-Manager

    options.put("SPECTATORS", new ConfigOption("Spectators", true, true));

    options.put("UPDATE_CHECKER", new ConfigOption("Update-Notifier.Stable", true, true));
    options.put("BETA_UPDATE_CHECKER", new ConfigOption("Update-Notifier.Beta", true, true));
  }

  private final String path;
  private final boolean value;
  private final boolean protectedOption;


  public ConfigOption(String path, boolean value, boolean protectedOption) {
    this.path = path;
    this.value = value;
    this.protectedOption = protectedOption;
  }

  public ConfigOption(String path, boolean value) {
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
  public boolean getValue() {
    return value;
  }

  /**
   * @return whether option is protected and cannot be unregistered
   */
  public boolean isProtected() {
    return protectedOption;
  }

  public static Map<String, ConfigOption> getOptions() {
    return Collections.unmodifiableMap(options);
  }
}
