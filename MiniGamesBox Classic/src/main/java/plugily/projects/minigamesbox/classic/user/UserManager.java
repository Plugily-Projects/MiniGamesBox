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

package plugily.projects.minigamesbox.classic.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.api.stats.IStatisticType;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.api.user.IUserManager;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.user.data.FileStats;
import plugily.projects.minigamesbox.classic.user.data.MysqlManager;
import plugily.projects.minigamesbox.api.user.data.UserDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class UserManager implements IUserManager {

  private final UserDatabase database;
  private final HashMap<UUID, User> users = new HashMap<>();
  private final PluginMain plugin;

  public UserManager(PluginMain plugin) {
    this.plugin = plugin;
    if(plugin.getConfigPreferences().getOption("DATABASE")) {
      database = new MysqlManager(plugin);
    } else {
      database = new FileStats(plugin);
    }
    Bukkit.getScheduler().runTaskLater(plugin, this::loadStatsForPlayersOnline, 40);
  }

  private void loadStatsForPlayersOnline() {
    Bukkit.getServer().getOnlinePlayers().stream().map(this::getUser).forEach(this::loadStatistics);
  }

  @Override
  public User getUser(Player player) {
    java.util.UUID playerId = player.getUniqueId();

    if (users.containsKey(playerId)){
      return users.get(playerId);
    }

    plugin.getDebugger().debug("Registering new user {0} ({1})", playerId, player.getName());
    User user = new User(playerId);
    users.put(playerId, user);
    return user;
  }

  @Override
  public List<IUser> getUsers(IPluginArena arena) {
    List<User> users = new ArrayList<>(arena.getPlayers().size());

    for (Player player : arena.getPlayers()) {
      users.add(getUser(player));
    }

    return new ArrayList<>(users);
  }

  @Override
  public void saveStatistic(IUser user, IStatisticType stat) {
    if(stat.isPersistent()) {
      database.saveStatistic(user, stat);
    }
  }

  @Override
  public void addExperience(Player player, int i) {
    User user = getUser(player);
    int expBoost = plugin.getPermissionsManager().getPermissionCategoryValue("EXP_BOOSTER", player);
    i += (i * (expBoost / 100));
    user.adjustStatistic(plugin.getStatsStorage().getStatisticType("EXP"), i);
    updateLevelStat(user, plugin.getArenaRegistry().getArena(player));
  }

  @Override
  public void addStat(Player player, IStatisticType stat) {
    addStat(getUser(player), stat);
  }

  @Override
  public void addStat(IUser user, IStatisticType stat) {
    user.adjustStatistic(stat, 1);
    updateLevelStat(user, user.getArena());
  }

  @Override
  public void updateLevelStat(IUser user, IPluginArena arena) {
    IStatisticType nextLevelExp = plugin.getStatsStorage().getStatisticType("NEXT_LEVEL_EXP");

    if(user.getStatistic(nextLevelExp) < user.getStatistic(plugin.getStatsStorage().getStatisticType("EXP"))) {
      user.adjustStatistic(plugin.getStatsStorage().getStatisticType("LEVEL"), 1);

      int level = user.getStatistic(plugin.getStatsStorage().getStatisticType("LEVEL"));

      user.setStatistic(nextLevelExp, (int) Math.ceil(Math.pow(50.0 * level, 1.5)));

      //Arena can be null when player has left the arena before this message is retrieved.
      if(arena != null)
        new MessageBuilder("IN_GAME_LEVEL_UP").asKey().arena(arena).player(user.getPlayer()).integer(level).sendPlayer();
    }
  }

  @Override
  public void saveAllStatistic(IUser user) {
    database.saveAllStatistic(user);
  }

  @Override
  public void loadStatistics(IUser user) {
    database.loadStatistics(user);
  }

  @Override
  public void removeUser(IUser user) {
    users.remove(user.getUniqueId());
  }

  @Override
  public UserDatabase getDatabase() {
    return database;
  }

}
