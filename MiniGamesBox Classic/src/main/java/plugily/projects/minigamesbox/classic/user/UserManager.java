/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2021  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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
 */

package plugily.projects.minigamesbox.classic.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.Main;
import plugily.projects.minigamesbox.classic.api.StatisticType;
import plugily.projects.minigamesbox.classic.user.data.FileStats;
import plugily.projects.minigamesbox.classic.user.data.MysqlManager;
import plugily.projects.minigamesbox.classic.user.data.UserDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tom on 27/07/2014.
 */
public class UserManager {

  private final UserDatabase database;
  private final List<User> users = new ArrayList<>();
  private final Main plugin;

  public UserManager(Main main) {
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

  public List<User> getUsers(Arena arena) {
    return arena.getPlayers().stream().map(this::getUser).collect(Collectors.toList());
  }

  public void saveStatistic(User user, StatisticType stat) {
    if(stat.isPersistent()) {
      database.saveStatistic(user, stat);
    }
  }

  public void addExperience(Player player, int i) {
    User user = getUser(player);
    user.addStat(StatisticType.XP, i);
    if(player.hasPermission(PermissionsManager.getVip())) {
      user.addStat(StatisticType.XP, (int) Math.ceil(i / 2.0));
    }
    if(player.hasPermission(PermissionsManager.getMvp())) {
      user.addStat(StatisticType.XP, (int) Math.ceil(i / 2.0));
    }
    if(player.hasPermission(PermissionsManager.getElite())) {
      user.addStat(StatisticType.XP, (int) Math.ceil(i / 2.0));
    }
    updateLevelStat(user, ArenaRegistry.getArena(player));
  }

  public void addStat(Player player, StatisticType stat) {
    addStat(getUser(player), stat);
  }

  public void addStat(User user, StatisticType stat) {
    user.addStat(stat, 1);
    updateLevelStat(user, user.getArena());
  }

  public void updateLevelStat(User user, Arena arena) {
    if(Math.pow(50.0 * user.getStat(plugin.getStatsStorage().getStatisticType("LEVEL")), 1.5) < user.getStat(plugin.getStatsStorage().getStatisticType("XP"))) {
      user.addStat(plugin.getStatsStorage().getStatisticType("LEVEL"), 1);
      //Arena can be null when player has left the arena before this message the arean is retrieved.
      if(arena != null)
        user.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage(Messages.YOU_LEVELED_UP), user.getStat(plugin.getStatsStorage().getStatisticType("LEVEL"))));
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
