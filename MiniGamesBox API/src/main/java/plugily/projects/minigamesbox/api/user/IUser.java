package plugily.projects.minigamesbox.api.user;

import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.api.kit.IKit;
import plugily.projects.minigamesbox.api.stats.IStatisticType;

import java.util.UUID;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IUser {
  UUID getUniqueId();

  IKit getKit();

  void setKit(IKit kit);

  IPluginArena getArena();

  Player getPlayer();

  boolean isSpectator();

  void setSpectator(boolean spectator);

  boolean isPermanentSpectator();

  void setPermanentSpectator(boolean permanentSpectator);

  int getStatistic(String statistic);

  int getStatistic(IStatisticType statisticType);

  void setStatistic(IStatisticType statisticType, int value);

  void setStatistic(String statistic, int value);

  void adjustStatistic(IStatisticType statisticType, int value);

  void adjustStatistic(String statistic, int value);

  void resetNonePersistentStatistics();

  boolean checkCanCastCooldownAndMessage(String cooldown);

  void setCooldown(String key, double seconds);

  double getCooldown(String key);

  boolean isInitialized();

  void setInitialized(boolean initialized);
}
