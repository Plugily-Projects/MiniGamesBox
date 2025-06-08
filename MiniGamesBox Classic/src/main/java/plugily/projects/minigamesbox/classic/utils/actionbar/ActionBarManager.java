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

package plugily.projects.minigamesbox.classic.utils.actionbar;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.string.StringFormatUtils;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 02.09.2022
 */
public class ActionBarManager extends BukkitRunnable {

  private final PluginMain plugin;
  private final int period = 10;
  private Map<Player, List<ActionBar>> actionBars = new HashMap<>();
  private Map<String, Integer> flashing = new HashMap<>();


  public ActionBarManager(PluginMain plugin) {
    this.plugin = plugin;
    runTaskTimer(plugin, 0L, period);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void run() {
    if(actionBars.isEmpty()) {
      return;
    }
    for(Map.Entry<Player, List<ActionBar>> actionBarList : actionBars.entrySet()) {
      Player player = actionBarList.getKey();
      IPluginArena arena = plugin.getArenaRegistry().getArena(player);
      List<ActionBar> bars = new ArrayList<>(actionBarList.getValue());
      if(bars.isEmpty()) {
        return;
      }
      plugin.getDebugger().debug("[ActionBarManager] [Arena {0} ] Player {1} got the following bars {2}", arena.getId(), player.getName(), bars.toString());
      bars.stream().max(Comparator.comparingInt(ActionBar::getPriority)).ifPresent(actionBar -> {
        plugin.getDebugger().debug("[ActionBarManager] [Arena {0} ] Player {1} sending {2}", arena.getId(), player.getName(), actionBar.toString());
        switch(actionBar.getActionBarType()) {
          case FLASHING:
            if(flashing.containsKey(actionBar.getKey())) {
              List<String> messages = plugin.getLanguageManager().getLanguageListFromKey(actionBar.getKey());
              int size = flashing.get(actionBar.getKey());
              if(size >= messages.size()) {
                flashing.put(actionBar.getKey(), 0);
              } else {
                flashing.put(actionBar.getKey(), size + 1);
              }
              VersionUtils.sendActionBar(player, new MessageBuilder(messages.get(size)).integer((actionBar.getTicks() - actionBar.getExecutedTicks()) / 20).player(player).arena(arena).build());
              break;
            }
            flashing.put(actionBar.getKey(), -1);
            break;
          case COOLDOWN:
            bars.remove(actionBar);
            VersionUtils.sendActionBar(player, actionBar.getMessage().integer((actionBar.getTicks() - actionBar.getExecutedTicks()) / 20).player(player).arena(arena).build());
            break;
          case DISPLAY:
            VersionUtils.sendActionBar(player, actionBar.getMessage().integer((actionBar.getTicks() - actionBar.getExecutedTicks()) / 20).player(player).arena(arena).build());
            break;
          case PROGRESS:
            String progress = StringFormatUtils.getProgressBar(actionBar.getExecutedTicks() + 10, actionBar.getTicks(),
                10, "█", ChatColor.COLOR_CHAR + "a", ChatColor.COLOR_CHAR + "c");
            VersionUtils.sendActionBar(player, actionBar.getMessage().value(progress).integer((actionBar.getTicks() - actionBar.getExecutedTicks()) / 20).player(player).arena(arena).build());
            break;
          default:
            break;
        }
      });
      for(ActionBar actionBar : bars) {
        if(actionBar.getActionBarType() == ActionBar.ActionBarType.COOLDOWN || actionBar.getActionBarType() == ActionBar.ActionBarType.PROGRESS) {
          actionBar.addExecutedTicks(period);
        }
        removeFinishedActionBar(player, actionBar);
      }
    }
  }

  private void removeFinishedActionBar(Player player, ActionBar actionBar) {
    if(actionBar.getExecutedTicks() >= actionBar.getTicks()) {
      //Clear the ActionBar by default as on some changes such as world switch or teleportation the bar could stick
      VersionUtils.sendActionBar(player, " ");
      if(actionBar.getActionBarType() == ActionBar.ActionBarType.FLASHING) {
        flashing.remove(actionBar.getKey());
      }
      actionBars.get(player).remove(actionBar);
      plugin.getDebugger().debug("[ActionBarManager] Player {1} removing {2}", player.getName(), actionBar.toString());
    }
  }

  public Map<Player, List<ActionBar>> getActionBars() {
    return Collections.unmodifiableMap(actionBars);
  }

  public void addActionBar(Player player, ActionBar actionBar) {
    plugin.getDebugger().debug("[ActionBarManager] Player {1} added {2}", player.getName(), actionBar.toString());
    if(actionBars.containsKey(player)) {
      List<ActionBar> bars = new ArrayList<>(actionBars.get(player));
      actionBars.remove(player);
      if(bars.stream().anyMatch(bar -> bar.getActionBarType() == ActionBar.ActionBarType.DISPLAY) && bars.stream().anyMatch(bar -> bar.getPriority() == actionBar.getPriority())) {
        List<ActionBar> displayBars = bars.stream().filter(bar -> bar.getActionBarType() == ActionBar.ActionBarType.DISPLAY).filter(bar -> bar.getPriority() == actionBar.getPriority()).collect(Collectors.toList());
        bars.removeAll(displayBars);
      }
      bars.add(actionBar);
      actionBars.put(player, bars);
      plugin.getDebugger().debug("[ActionBarManager] Player {1} got the following bars {2}", player.getName(), bars.toString());
      return;
    }
    actionBars.put(player, new ArrayList<>(Collections.singleton(actionBar)));
  }

  public void clearActionBarsFromPlayer(Player player) {
    actionBars.remove(player);
  }
}
