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

package plugily.projects.minigamesbox.classic.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.api.StatisticType;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.user.data.FileStats;
import plugily.projects.minigamesbox.classic.user.data.MysqlManager;
import plugily.projects.minigamesbox.classic.user.data.UserDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class UserManager {

  private final UserDatabase database;
  private final List<User> users = new ArrayList<>();
  private final PluginMain plugin;

  public UserManager(PluginMain main) {
    this.plugin = main;
    if(plugin.getConfigPreferences().getOption("DATABASE")) {
      database = new MysqlManager(plugin);
    } else {
      database = new FileStats(plugin);
    }
    loadStatsForPlayersOnline();
  }

  private void loadStatsForPlayersOnline() {
    Bukkit.getServer().getOnlinePlayers().stream().map(this::getUser).forEach(this::loadStatistics);
  }

  public User getUser(Player player) {
    java.util.UUID playerId = player.getUniqueId();

    for(User user : users) {
      if(user.getUniqueId().equals(playerId)) {
        return user;
      }
    }

    plugin.getDebugger().debug("Registering new user {0} ({1})", playerId, player.getName());
    User user = new User(playerId);
    users.add(user);
    return user;
  }

  public List<User> getUsers(PluginArena arena) {
    return arena.getPlayers().stream().map(this::getUser).collect(Collectors.toList());
  }

  public void saveStatistic(User user, StatisticType stat) {
    if(stat.isPersistent()) {
      database.saveStatistic(user, stat);
    }
  }

  public void addExperience(Player player, int i) {
    User user = getUser(player);
    int expBoost = plugin.getPermissionsManager().getPermissionCategoryValue("EXP_BOOSTER", player);
    i += (i * (expBoost / 100));
    user.addStat(plugin.getStatsStorage().getStatisticType("XP"), i);
    updateLevelStat(user, plugin.getArenaRegistry().getArena(player));
  }

  public void addStat(Player player, StatisticType stat) {
    addStat(getUser(player), stat);
  }

  public void addStat(User user, StatisticType stat) {
    user.addStat(stat, 1);
    updateLevelStat(user, user.getArena());
  }

  public void updateLevelStat(User user, PluginArena arena) {
    if(Math.pow(50.0 * user.getStat(plugin.getStatsStorage().getStatisticType("LEVEL")), 1.5) < user.getStat(plugin.getStatsStorage().getStatisticType("XP"))) {
      user.addStat(plugin.getStatsStorage().getStatisticType("LEVEL"), 1);
      //Arena can be null when player has left the arena before this message is retrieved.
      if(arena != null)
        user.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage("IN_GAME_LEVEL_UP"), user.getStat(plugin.getStatsStorage().getStatisticType("LEVEL"))));
    }
  }

  public void saveAllStatistic(User user) {
    database.saveAllStatistic(user);
  }

  public void loadStatistics(User user) {
    database.loadStatistics(user);
  }

  public void removeUser(User user) {
    users.remove(user);
  }

  public UserDatabase getDatabase() {
    return database;
  }

}
