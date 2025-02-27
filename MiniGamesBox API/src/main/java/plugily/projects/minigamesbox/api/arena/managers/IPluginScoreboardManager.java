package plugily.projects.minigamesbox.api.arena.managers;

import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.user.IUser;

import java.util.List;


/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IPluginScoreboardManager {
  /**
   * Creates arena scoreboard for target user
   *
   * @param user user that represents game player
   * @see IUser
   */
  void createScoreboard(IUser user);

  void updateScoreboards();

  /**
   * Removes scoreboard of user
   *
   * @param user user that represents game player
   * @see IUser
   */
  void removeScoreboard(IUser user);

  /**
   * Forces all scoreboards to deactivate.
   */
  void stopAllScoreboards();

  /**
   * Get Lines from the language.yml according to arena state
   */
  List<String> getScoreboardLines();

  /**
   * Update lines with defined placeholders
   */
  List<String> formatScoreboardLines(List<String> lines, Player player);
}
