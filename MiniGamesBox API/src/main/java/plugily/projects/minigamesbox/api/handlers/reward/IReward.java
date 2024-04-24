package plugily.projects.minigamesbox.api.handlers.reward;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IReward {
  RewardExecutor getExecutor();

  String getExecutableCode();

  double getChance();

  int getNumberExecute();

  IRewardType getType();

  enum RewardExecutor {
    CONSOLE, PLAYER, SCRIPT
  }
}
