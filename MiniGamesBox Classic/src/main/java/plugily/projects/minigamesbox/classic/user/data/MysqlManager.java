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
import org.bukkit.plugin.IllegalPluginAccessException;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.api.stats.IStatisticType;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.api.user.data.UserDatabase;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.database.MysqlDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class MysqlManager implements UserDatabase {

  private final PluginMain plugin;
  private final MysqlDatabase database;
  private final String createTableStatement;

  public MysqlManager(PluginMain plugin) {
    this.plugin = plugin;
    this.createTableStatement = "CREATE TABLE IF NOT EXISTS `" + getTableName() + "` (`UUID` char(36) NOT NULL PRIMARY KEY, `name` varchar(32) NOT NULL);";
    FileConfiguration config = ConfigUtils.getConfig(plugin, "mysql");
    database = new MysqlDatabase(config.getString("user"), config.getString("password"), config.getString("address"), config.getLong("maxLifeTime", 1800000));
    plugin.getDebugger().debug("MySQL Database enabled");
    initializeTable(plugin);
  }

  private void initializeTable(PluginMain plugin) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      try(Connection connection = database.getConnection();
          Statement statement = connection.createStatement()) {

        statement.executeUpdate(createTableStatement);

        updateTable(statement, "ALTER TABLE " + getTableName() + " ADD COLUMN`name` varchar(32) NOT NULL");
        updateTable(statement, "ALTER TABLE " + getTableName() + " ADD COLUMN `UUID` char(36) NOT NULL PRIMARY KEY");

        plugin.getDebugger().debug("Initialized MySQL Table");
      } catch(SQLException exception) {
        throwException(exception);
      }
    });
  }

  private void updateTable(@NotNull Statement statement, String sql) {
    try {
      statement.executeUpdate(sql);
    } catch(SQLException ignored) {
      //already created
    }
  }

  public String getTableName() {
    return ConfigUtils.getConfig(plugin, "mysql").getString("table", "playerstats");
  }

  @Override
  public void addColumn(String columnName, String columnProperties) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      try(Connection connection = database.getConnection(); Statement statement = connection.createStatement()) {
        updateTable(statement, "ALTER TABLE " + getTableName() + " ADD COLUMN " + columnName + " " + columnProperties + ";");
        plugin.getDebugger().debug("MySQL Table | Added column {0} {1}", columnName, columnProperties);
      } catch(SQLException ignored) {
        //already created column
      }
    });
  }

  @Override
  public void dropColumn(String columnName) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
          database.executeUpdate("ALTER TABLE " + getTableName() + " DROP COLUMN " + columnName + ";");
          plugin.getDebugger().debug("MySQL Table | Dropped column {0}", columnName);
        }
    );
  }

  @Override
  public MysqlDatabase getMySQLDatabase() {
    return database;
  }

  @Override
  public void saveStatistic(IUser user, IStatisticType statisticType) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      database.executeUpdate("UPDATE " + getTableName() + " SET " + statisticType.getName() + "=" + user.getStatistic(statisticType) + " WHERE UUID='" + user.getUniqueId().toString() + "';");
      plugin.getDebugger().debug("MySQL Table | Saved {0} statistic to {1} for {2}", statisticType.getName(), user.getStatistic(statisticType), user.getPlayer().getName());
    });
  }

  @Override
  public void saveAllStatistic(IUser user) {
    if (!user.isInitialized()){
      plugin.getDebugger().debug("User been saving while is not is not initialized.");
    } else {
      try {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> database.executeUpdate(getUpdateQuery(user)));
      } catch (IllegalPluginAccessException ignored) {
        database.executeUpdate(getUpdateQuery(user));
      }
    }
  }

  @Override
  public void loadStatistics(IUser user) {
    Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
      String uuid = user.getUniqueId().toString();
      try(Connection connection = database.getConnection(); Statement statement = connection.createStatement()) {
        String playerName = user.getPlayer() == null ? Bukkit.getOfflinePlayer(uuid).getName() : user.getPlayer().getName();

        database.executeUpdate("UPDATE " + getTableName() + " SET name='" + playerName + "' WHERE UUID='" + uuid + "';");
        ResultSet resultSet = statement.executeQuery("SELECT * from " + getTableName() + " WHERE UUID='" + uuid + "'");
        if(resultSet.next()) {
          loadUserStats(user, resultSet);
        } else {
          createUserStats(user, uuid, statement, playerName);
        }
        user.setInitialized(true);
      } catch(SQLException exception) {
        throwException(exception);
      }
    }, 20L /* required to load stats that are saved on server switch */);
  }

  /**
   * Load the Stats from a known User in database
   *
   * @param user
   * @param resultSet
   * @throws SQLException
   */
  private void loadUserStats(IUser user, ResultSet resultSet) throws SQLException {
    //player already exists - get the stats
    for(IStatisticType statisticType : plugin.getStatsStorage().getStatistics().values()) {
      if(!statisticType.isPersistent()) {
        continue;
      }
      setUserStat(user, statisticType, resultSet.getInt(statisticType.getName()));
    }
    plugin.getDebugger().debug("Loaded User Stats for {0}", user.getPlayer().getName());
  }

  /**
   * Creates a new user entries as it doesn't exists
   *
   * @param user
   * @param uuid
   * @param statement
   * @param playerName
   * @throws SQLException
   */
  private void createUserStats(IUser user, String uuid, @NotNull Statement statement, String playerName) throws SQLException {
    plugin.getDebugger().debug("Created User Stats for {0}", user.getPlayer().getName());
    statement.executeUpdate("INSERT INTO " + getTableName() + " (UUID,name) VALUES ('" + uuid + "','" + playerName + "')");
    plugin.getStatsStorage().getStatistics().forEach((s, statisticType) -> setUserStat(user, statisticType, 0));
  }

  /**
   * Set the stats of a user, most likely to be used on user loading
   *
   * @param user
   * @param statisticType
   * @param value
   */
  private void setUserStat(IUser user, @NotNull IStatisticType statisticType, int value) {
    if(statisticType.isPersistent()) {
      user.setStatistic(statisticType, value);
    }
  }

  @NotNull
  @Override
  public Map<UUID, Integer> getStats(IStatisticType stat) {
    try(Connection connection = database.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT UUID, " + stat.getName() + " FROM " + getTableName() + " ORDER BY " + stat.getName())) {
      return getColumnData(stat, resultSet);
    } catch(SQLException exception) {
      throwException(exception);
      return Collections.emptyMap();
    }
  }

  /**
   * Throw a general exception
   *
   * @param exception
   */
  private void throwException(@NotNull SQLException exception) {
    plugin.getDebugger().debug(Level.WARNING, "SQLException occurred! Cause: {0} ({1})", exception.getSQLState(), exception.getErrorCode());
    plugin.getMessageUtils().errorOccurred();
    Bukkit.getConsoleSender().sendMessage("Check configuration of mysql.yml file or try to disable mysql option in config.yml");
    exception.printStackTrace();
  }

  /**
   * Get Column data from mysql
   *
   * @param statistic
   * @param resultSet
   * @return
   * @throws SQLException
   */
  private @NotNull Map<UUID, Integer> getColumnData(IStatisticType statistic, @NotNull ResultSet resultSet) throws SQLException {
    Map<UUID, Integer> column = new LinkedHashMap<>();
    while(resultSet.next()) {
      String uuid = resultSet.getString("UUID");

      if(uuid.isEmpty()) {
        continue;
      }

      try {
        int database = resultSet.getInt(statistic.getName());
        int value = getUpdatedColumnData(uuid, statistic, database);
        column.put(UUID.fromString(uuid), value);
      } catch(IllegalArgumentException exception) {
        plugin.getDebugger().debug(Level.WARNING, "Cannot load the UUID for {0}", uuid);
      }
    }
    plugin.getDebugger().debug("MySQL Table | Fetched column data {0}", column.toString());
    return column;
  }

  private int getUpdatedColumnData(String uuid, IStatisticType statisticType, int fromDatabase) {
    Player player = Bukkit.getPlayer(UUID.fromString(uuid));
    if(player != null && player.isOnline()) {
      return plugin.getStatsStorage().getUserStats(player, statisticType);
    }
    return fromDatabase;
  }

  @Override
  public void disable() {
    for(Player player : plugin.getServer().getOnlinePlayers()) {
      database.executeUpdate(getUpdateQuery(plugin.getUserManager().getUser(player)));
    }
    database.shutdownConnPool();
  }

  @Override
  public String getPlayerName(UUID uuid) {
    try(Connection connection = database.getConnection(); Statement statement = connection.createStatement()) {
      ResultSet resultSet = statement.executeQuery("SELECT `name` FROM " + getTableName() + " WHERE UUID='" + uuid.toString() + "'");
      String name = null;
      while(resultSet.next()) {
        name = resultSet.getString("name");
      }
      plugin.getDebugger().debug("MySQL playername of {0} is {1}", uuid, name);
      return name;
    } catch(SQLException | NullPointerException exception) {
      exception.printStackTrace();
      return null;
    }
  }

  /**
   * Build update query for all known statistics
   *
   * @param user
   * @return
   */
  private @NotNull String getUpdateQuery(@NotNull IUser user) {
    StringBuilder update = new StringBuilder(" SET ");
    plugin.getStatsStorage().getStatistics().forEach((statistic, statisticType) -> {
      if(statisticType.isPersistent()) {
        if(!update.toString().equalsIgnoreCase(" SET ")) {
          update.append(", ");
        }
        update.append(statisticType.getName()).append("=").append(user.getStatistic(statisticType));
      }
    });
    String executeStatement = "UPDATE " + getTableName() + update + " WHERE UUID='" + user.getUniqueId().toString() + "';";
    plugin.getDebugger().debug("MySQL Table | Executing Update Query {0} ", executeStatement);
    return executeStatement;
  }
}
