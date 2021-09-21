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

package plugily.projects.minigamesbox.classic.utils.services.exception;

import org.bukkit.Bukkit;
import plugily.projects.minigamesbox.classic.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class ExceptionLogHandler extends Handler {

  private final List<String> blacklistedClasses = new ArrayList<>();

  private final Main plugin;

  public ExceptionLogHandler(Main plugin) {
    this.plugin = plugin;
    Bukkit.getLogger().addHandler(this);
    addBlacklistedClass("plugily.projects." + plugin.getDescription().getName().toLowerCase() + ".user.data.MysqlManager");
    addBlacklistedClass("plugily.projects." + plugin.getDescription().getName().toLowerCase() + ".plugily.projects.commonsbox.database.MysqlDatabase");
  }

  /**
   * Blacklist classes that should not be reported if found on stacktrace
   *
   * @param blacklist the class path that should be blacklisted
   */
  public void addBlacklistedClass(String blacklist) {
    if(blacklistedClasses.contains(blacklist)) {
      throw new IllegalStateException("Class " + blacklist + " is already blacklisted!");
    }
    blacklistedClasses.add(blacklist);
  }

  @Override
  public void close() {
    //unused in this handler
  }

  @Override
  public void flush() {
    //unused in this handler
  }

  @Override
  public void publish(LogRecord record) {
    try {
      Throwable throwable = record.getThrown();
      if(throwable == null || throwable.getCause() == null) {
        return;
      }
      StackTraceElement[] element = throwable.getCause().getStackTrace();
      if(element.length == 0 || element[0] == null || !element[0].getClassName().contains("plugily.projects." + plugin.getDescription().getName().toLowerCase())) {
        return;
      }
      if(containsBlacklistedClass(throwable)) {
        return;
      }
      new ReportedException(plugin, throwable);
      record.setThrown(null);
      record.setMessage(plugin.getPluginPrefix() + "We have found a bug in the code. Contact us at our official discord server (Invite link: https://discordapp.com/invite/UXzUdTP) with the following error given above!");
    } catch(ArrayIndexOutOfBoundsException ignored) {
      //ignored
    }
  }

  private boolean containsBlacklistedClass(Throwable throwable) {
    for(StackTraceElement element : throwable.getStackTrace()) {
      for(String blacklist : blacklistedClasses) {
        if(element.getClassName().contains(blacklist)) {
          return true;
        }
      }
    }
    return false;
  }

}
