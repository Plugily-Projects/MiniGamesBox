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
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
  private final Map<ArenaState, List<String>> bossbarLines = new EnumMap<>(ArenaState.class);
  private List<BossBar> gameBars = new ArrayList<>();

  public BossbarManager(PluginArena arena) {
    this.arena = arena;
    this.plugin = arena.getPlugin();
    arena.setArenaOption("BOSSBAR_INTERVAL", plugin.getConfig().getInt("Bossbar.Interval", 10));
    this.interval = arena.getArenaOption("BOSSBAR_INTERVAL");
    this.currentLine = 0;

    String bossBarTitle = new MessageBuilder("BOSSBAR_TITLE").asKey().arena(arena).build();

    for(ArenaState arenaState : ArenaState.values()) {
      if(arenaState == ArenaState.FULL_GAME) {
        continue;
      }
      List<String> titlesList = plugin.getLanguageManager().getLanguageList("Bossbar.Content." + arenaState.getFormattedName());
      titlesList.add(bossBarTitle);
      bossbarLines.put(arenaState, titlesList);
    }
    plugin.getDebugger().debug("Arena {0} loaded Bossbar content: {1}", arena.getId(), bossbarLines.toString());
  }

  public void bossBarUpdate() {
    if(gameBars.isEmpty()) {
      return;
    }
    List<String> values = new ArrayList<>(bossbarLines.get(arena.getArenaState() == ArenaState.FULL_GAME ? ArenaState.STARTING : arena.getArenaState()));

    if(currentLine > values.size() - 1) {
      currentLine = 0;
    }
    for(BossBar bar : gameBars) {
      String bossbarMessage = new MessageBuilder(values.get(currentLine)).arena(arena).player(bar.getPlayers().get(0)).build();
      bar.setTitle(bossbarMessage);
    }

    if(arena.getArenaOption("BAR_TOGGLE_VALUE") > interval) {
      currentLine++;
      arena.setArenaOption("BAR_TOGGLE_VALUE", 0);
      arena.changeArenaOptionBy("BAR_TOGGLE_VALUE", 1);
    }

    arena.changeArenaOptionBy("BAR_TOGGLE_VALUE", 1);
  }

  /**
   * Executes boss bar action for arena
   *
   * @param action add or remove a player from boss bar
   * @param player player
   */
  public void doBarAction(PluginArena.BarAction action, Player player) {
    switch(action) {
      case ADD:
        if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1) && plugin.getConfigPreferences().getOption("BOSSBAR")) {
          BossBar bossBar = Bukkit.createBossBar(new MessageBuilder("BOSSBAR_TITLE").asKey().arena(arena).player(player).build(), BarColor.BLUE, BarStyle.SOLID);
          bossBar.addPlayer(player);
          gameBars.add(bossBar);
        }
        break;
      case REMOVE:
        List<BossBar> bars = gameBars.stream().filter(bossBar -> bossBar.getPlayers().contains(player)).collect(Collectors.toList());
        for(BossBar bar : bars) {
          bar.removePlayer(player);
          gameBars.remove(bar);
        }
        break;
      default:
        break;
    }
  }

  public void setProgress(double progress) {
    gameBars.forEach(bossbar -> bossbar.setProgress(progress));
  }

}
