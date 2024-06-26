package plugily.projects.minigamesbox.api.stats;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IStatsStorage {
  Map<UUID, Integer> getStats(IStatisticType stat);

  int getUserStats(Player player, IStatisticType statisticType);

  String getStatisticName(String key);

  IStatisticType getStatisticType(String key);

  void registerStatistic(String key, IStatisticType statisticType);

  void unregisterStatistic(String name);

  Map<String, IStatisticType> getStatistics();
}
