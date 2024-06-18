package plugily.projects.minigamesbox.api.preferences;

import java.util.List;
import java.util.Map;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IConfigPreferences {

  /**
   * Returns whether option value is true or false
   *
   * @param name option to get value from
   * @return true or false based on user configuration
   */
  boolean getOption(String name);

  /**
   * Register a new config option
   *
   * @param name   The name of the Option
   * @param option Contains the path and the default value
   */
  void registerOption(String name, IConfigOption option);

  /**
   * Remove config options that are not protected
   *
   * @param name The name of the Option
   */
  void unregisterOption(String name);

  Map<String, IConfigOption> getOptions();

  List<ICommandShorter> getCommandShorts();

  void addCommandShorter(ICommandShorter commandShorter);
}
