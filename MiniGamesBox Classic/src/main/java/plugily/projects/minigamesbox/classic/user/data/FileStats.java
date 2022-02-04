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
package plugily.projects.minigamesbox.classic.user.data;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import plugily.projects.commonsbox.database.MysqlDatabase;
import plugily.projects.commonsbox.sorter.SortUtils;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.api.StatisticType;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class FileStats implements UserDatabase, Runnable {

  private final PluginMain plugin;
  private final FileConfiguration config;
  private final BukkitTask updateTask;
  private final AtomicBoolean updateRequired = new AtomicBoolean(false);

  public FileStats(PluginMain plugin) {
    this.plugin = plugin;
    this.config = ConfigUtils.getConfig(plugin, "stats");
    this.updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 40, 40);
  }

  @Override
  public void saveStatistic(User user, StatisticType stat) {
    config.set(user.getUniqueId().toString() + "." + stat.getName(), user.getStatistic(stat));
    updateRequired.set(true);
  }

  @Override
  public void saveAllStatistic(User user) {
    updateStats(user);
    updateRequired.set(true);
  }

  @Override
  public void loadStatistics(User user) {
    String uuid = user.getUniqueId().toString();
    plugin.getStatsStorage().getStatistics().forEach((s, statisticType) -> user.setStatistic(statisticType, config.getInt(uuid + "." + statisticType.getName())));
  }

  @Override
  public void addColumn(String columnName, String columnProperties) {
    //skip
  }

  @Override
  public void dropColumn(String columnName) {
    //skip
  }

  @NotNull
  @Override
  public Map<UUID, Integer> getStats(StatisticType stat) {
    Map<UUID, Integer> stats = new TreeMap<>();
    for(String string : config.getKeys(false)) {
      if(string.equals("data-version")) {
        continue;
      }
      try {
        stats.put(UUID.fromString(string), config.getInt(string + "." + stat.getName()));
      } catch(IllegalArgumentException ex) {
        plugin.getLogger().log(Level.WARNING, "Cannot load the UUID for {0}", string);
      }
    }
    return SortUtils.sortByValue(stats);
  }

  @Override
  public void disable() {
    for(Player player : plugin.getServer().getOnlinePlayers()) {
      updateStats(plugin.getUserManager().getUser(player));
    }
    updateTask.cancel();
    // Save the last time before disabling
    run();
  }

  @Override
  public MysqlDatabase getMySQLDatabase() {
    return null;
  }

  @Override
  public String getPlayerName(UUID uuid) {
    return Bukkit.getOfflinePlayer(uuid).getName();
  }

  private void updateStats(User user) {
    String uuid = user.getUniqueId().toString();

    plugin.getStatsStorage().getStatistics().forEach((s, statisticType) -> {
      if(statisticType.isPersistent()) {
        String path = uuid + "." + statisticType.getName();
        int value = user.getStatistic(statisticType);
        if(value > 0 || config.contains(path)) {
          config.set(path, value);
        }
      }
    });
  }

  // Save the config to the file
  @Override
  public void run() {
    if(updateRequired.get()) {
      updateRequired.set(false);
      Bukkit.getScheduler().runTask(plugin, () -> ConfigUtils.saveConfig(plugin, config, "stats"));
    }
  }
}
