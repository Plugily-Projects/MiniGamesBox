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

package plugily.projects.minigamesbox.classic.utils.misc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import plugily.projects.minigamesbox.api.utils.misc.IDebugger;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 12.09.2021
 */
public class Debugger implements IDebugger {

  private final java.util.Set<String> listenedPerformance = new HashSet<>();
  private boolean enabled = false;
  private boolean deep = false;
  private final String debuggerPrefix;
  private final Logger logger;

  public Debugger(PluginMain plugin, boolean enable) {
    logger = Logger.getLogger(plugin.getName());
    debuggerPrefix = "[Debug] ";
    enabled = enable;
  }

  @Override
  public void setEnabled(boolean enable) {
    enabled = enable;
  }

  @Override
  public void deepDebug(boolean enable) {
    deep = enable;
  }

  @Override
  public void monitorPerformance(String task) {
    listenedPerformance.add(task);
  }

  @Override
  public void sendConsoleMsg(String msg) {
    if (ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16_R1) && msg.indexOf('#') >= 0) {
      msg = MiscUtils.matchColorRegex(msg);
    }

    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
  }

  @Override
  public void debug(String msg) {
    debug(Level.INFO, msg);
  }

  @Override
  public void debug(Level level, String msg) {
    if (!enabled && (level != Level.WARNING && level != Level.SEVERE)) {
      return;
    }
    logger.log(level, debuggerPrefix + ChatColor.translateAlternateColorCodes('&', msg));
  }

  @Override
  public void debug(String msg, Object... params) {
    debug(Level.INFO, msg, params);
  }

  @Override
  public void debug(Level level, String msg, Object... params) {
    if (!enabled && (level != Level.WARNING && level != Level.SEVERE)) {
      return;
    }
    logger.log(level, debuggerPrefix + msg, params);
  }

  @Override
  public void performance(String monitorName, String msg, Object... params) {
    if (!deep || !listenedPerformance.contains(monitorName)) {
      return;
    }
    logger.log(Level.INFO, debuggerPrefix + msg, params);
  }

  @Override
  public Set<String> getListenedPerformance() {
    return listenedPerformance;
  }

}
