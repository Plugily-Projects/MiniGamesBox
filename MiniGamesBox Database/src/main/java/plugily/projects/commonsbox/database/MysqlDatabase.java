package plugily.projects.commonsbox.database;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Plajer
 * <p>
 * Created at 09.03.2019
 * @version 2.0.0
 */
public class MysqlDatabase {

  private HikariDataSource hikariDataSource;
  private final Logger databaseLogger = Logger.getLogger("MinigamesBox Database");

  public MysqlDatabase(String user, String password, String jdbcUrl, long maxLifeTime) {
    databaseLogger.log(Level.INFO, "Configuring MySQL connection!");
    configureConnPool(user, password, jdbcUrl, maxLifeTime);

    try(Connection connection = getConnection()) {
      if(connection == null) {
        databaseLogger.log(Level.SEVERE, "Failed to connect to database!");
      }
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  public MysqlDatabase(String user, String password, String host, String database, int port, long maxLifeTime) {
    databaseLogger.log(Level.INFO, "Configuring MySQL connection!");
    configureConnPool(user, password, host, database, port, maxLifeTime);

    try(Connection connection = getConnection()) {
      if(connection == null) {
        databaseLogger.log(Level.SEVERE, "Failed to connect to database!");
      }
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }

  private void configureConnPool(String user, String password, String jdbcUrl, long maxLifeTime) {
    try {
      databaseLogger.info("Creating HikariCP Configuration...");
      HikariDataSource config = new HikariDataSource();
      config.setJdbcUrl(jdbcUrl);
      config.setMaxLifetime(maxLifeTime);
      config.addDataSourceProperty("user", user);
      config.addDataSourceProperty("password", password);
      hikariDataSource = config;
      databaseLogger.info("Setting up MySQL Connection pool...");
      databaseLogger.info("Connection pool successfully configured. ");
    } catch(Exception e) {
      e.printStackTrace();
      databaseLogger.warning("Cannot connect to MySQL database!");
      databaseLogger.warning("Check configuration of your database settings!");
    }
  }

  private void configureConnPool(String user, String password, String host, String database, int port, long maxLifeTime) {
    try {
      databaseLogger.info("Creating HikariCP Configuration...");
      HikariDataSource config = new HikariDataSource();
      config.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
      config.addDataSourceProperty("serverName", host);
      config.addDataSourceProperty("portNumber", port);
      config.addDataSourceProperty("databaseName", database);
      config.addDataSourceProperty("user", user);
      config.addDataSourceProperty("password", password);
      config.setMaxLifetime(maxLifeTime);
      hikariDataSource = config;
      databaseLogger.info("Setting up MySQL Connection pool...");
      databaseLogger.info("Connection pool successfully configured. ");
    } catch(Exception e) {
      e.printStackTrace();
      databaseLogger.warning("Cannot connect to MySQL database!");
      databaseLogger.warning("Check configuration of your database settings!");
    }
  }

  public void executeUpdate(String query) {
    try(Connection connection = getConnection()) {
      if(connection != null) {
        try(Statement statement = connection.createStatement()) {
          statement.executeUpdate(query);
        }
      }
    } catch(SQLException e) {
      databaseLogger.warning("Failed to execute update: " + query);
    }
  }


  public ResultSet executeQuery(String query) {
    throw new UnsupportedOperationException("Queries should be created with own auto-closeable connection!");
  }

  public void shutdownConnPool() {
    try {
      databaseLogger.info("Shutting down connection pool. Trying to close all connections.");
      if(!hikariDataSource.isClosed()) {
        hikariDataSource.close();
        databaseLogger.info("Pool successfully shutdown. ");
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public Connection getConnection() {
    try {
      return hikariDataSource.getConnection();
    } catch(Exception e) {
      e.printStackTrace();
    }
    return null;
  }

}
