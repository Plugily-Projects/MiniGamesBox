package plugily.projects.minigamesbox.api.user;

import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.api.stats.IStatisticType;
import plugily.projects.minigamesbox.api.user.data.UserDatabase;

import java.util.List;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IUserManager {
  IUser getUser(Player player);

  List<IUser> getUsers(IPluginArena arena);

  void saveStatistic(IUser user, IStatisticType stat);

  void addExperience(Player player, int i);

  void addStat(Player player, IStatisticType stat);

  void addStat(IUser user, IStatisticType stat);

  void updateLevelStat(IUser user, IPluginArena arena);

  void saveAllStatistic(IUser user);

  void loadStatistics(IUser user);

  void removeUser(IUser user);

  UserDatabase getDatabase();
}
