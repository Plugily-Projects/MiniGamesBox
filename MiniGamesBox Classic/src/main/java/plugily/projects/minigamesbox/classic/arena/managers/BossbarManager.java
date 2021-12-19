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

package plugily.projects.minigamesbox.classic.arena.managers;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 27.11.2021
 */
public class BossbarManager {

  private final PluginMain plugin;
  private final PluginArena arena;
  private final int interval;
  private int currentLine;
  private final Map<ArenaState, List<String>> bossbar = new EnumMap<>(ArenaState.class);
  private BossBar gameBar;

  public BossbarManager(PluginArena arena) {
    this.arena = arena;
    this.plugin = arena.getPlugin();
    arena.setArenaOption("BOSSBAR_INTERVAL", plugin.getConfig().getInt("Bossbar.Interval", 10));
    this.interval = arena.getArenaOption("BOSSBAR_INTERVAL");
    this.currentLine = 0;

    for(ArenaState arenaState : ArenaState.values()) {
      if(arenaState == ArenaState.FULL_GAME) {
        continue;
      }
      bossbar.put(arenaState, plugin.getLanguageManager().getLanguageList("Bossbar.Content." + arenaState.getFormattedName()));
    }

    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1) && plugin.getConfigPreferences().getOption("BOSSBAR")) {
      gameBar = Bukkit.createBossBar(plugin.getChatManager().colorMessage("BOSSBAR_TITLE"), BarColor.BLUE, BarStyle.SOLID);
    }
  }

  public void bossBarUpdate() {
    if(gameBar == null) {
      return;
    }
    List<String> values;
    if(arena.getArenaState() == ArenaState.FULL_GAME) {
      values = bossbar.get(ArenaState.STARTING);
    } else {
      values = bossbar.get(arena.getArenaState());
    }
    int lines = values.size() - 1;
    if(currentLine >= lines) {
      currentLine = 0;
    }
    if(arena.getArenaOption("BAR_TOGGLE_VALUE") > interval) {
      currentLine++;
      arena.setArenaOption("BAR_TOGGLE_VALUE", 0);
      arena.changeArenaOptionBy("BAR_TOGGLE_VALUE", 1);
    }
    String bossbarMessage = plugin.getChatManager().formatMessage(values.get(currentLine), arena);

    gameBar.setTitle(bossbarMessage);
    arena.changeArenaOptionBy("BAR_TOGGLE_VALUE", 1);
  }

  /**
   * Executes boss bar action for arena
   *
   * @param action add or remove a player from boss bar
   * @param player player
   */
  public void doBarAction(PluginArena.BarAction action, Player player) {
    if(gameBar == null) {
      return;
    }
    switch(action) {
      case ADD:
        gameBar.addPlayer(player);
        break;
      case REMOVE:
        gameBar.removePlayer(player);
        break;
      default:
        break;
    }
  }

  /**
   * Returns boss bar of the game.
   * Please use doBarAction if possible
   *
   * @return game boss bar
   * @see #doBarAction(PluginArena.BarAction, Player)
   */
  public BossBar getGameBar() {
    return gameBar;
  }

  public void setProgress(double progress) {
    if(gameBar == null) {
      return;
    }
    gameBar.setProgress(progress);
  }

}
