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
package plugily.projects.minigamesbox.classic.user.data;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.api.stats.IStatisticType;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.api.user.data.UserDatabase;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.database.MysqlDatabase;
import plugily.projects.minigamesbox.sorter.SortUtils;

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
public class FileStats implements UserDatabase {

  private final PluginMain plugin;
  private final FileConfiguration config;
  private final AtomicBoolean updateRequired = new AtomicBoolean(false);

  public FileStats(PluginMain plugin) {
    this.plugin = plugin;
    this.config = ConfigUtils.getConfig(plugin, "stats");
  }

  @Override
  public void saveStatistic(IUser user, IStatisticType stat) {
    config.set(user.getUniqueId().toString() + "." + stat.getName(), user.getStatistic(stat));
    ConfigUtils.saveConfig(plugin, config, "stats");
  }

  @Override
  public void saveAllStatistic(IUser user) {
    updateStats(user);
  }

  @Override
  public void loadStatistics(IUser user) {
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
  public Map<UUID, Integer> getStats(IStatisticType stat) {
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
      ConfigUtils.saveConfig(plugin, config, "stats");
  }

  @Override
  public MysqlDatabase getMySQLDatabase() {
    return null;
  }

  @Override
  public String getPlayerName(UUID uuid) {
    return config.getString(uuid + ".playername", Bukkit.getOfflinePlayer(uuid).getName());
  }

  private void updateStats(IUser user) {
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
    config.set(uuid + ".playername", user.getPlayer().getName());
    ConfigUtils.saveConfig(plugin, config, "stats");
  }
}
