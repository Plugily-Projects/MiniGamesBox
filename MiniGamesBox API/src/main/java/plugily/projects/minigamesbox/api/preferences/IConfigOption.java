package plugily.projects.minigamesbox.api.preferences;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IConfigOption {
  String getPath();

  /**
   * @return default value of option if absent in config
   */
  boolean getValue();

  /**
   * @return whether option is protected and cannot be unregistered
   */
  boolean isProtected();
}
