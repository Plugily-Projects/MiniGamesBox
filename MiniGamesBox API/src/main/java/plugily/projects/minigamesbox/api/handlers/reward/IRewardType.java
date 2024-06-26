package plugily.projects.minigamesbox.api.handlers.reward;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IRewardType {
  String getPath();

  ExecutorType getExecutorType();

  /**
   * @return whether option is protected and cannot be unregistered
   */
  boolean isProtected();

  enum ExecutorType {
    DEFAULT, NUMBER
  }
}
