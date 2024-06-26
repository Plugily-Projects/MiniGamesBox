package plugily.projects.minigamesbox.api.utils.misc;

import java.util.Set;
import java.util.logging.Level;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IDebugger {
  void setEnabled(boolean enable);

  void deepDebug(boolean enable);

  void monitorPerformance(String task);

  void sendConsoleMsg(String msg);

  void debug(String msg);

  /**
   * Prints debug message with selected log level.
   * Messages of level INFO or TASK won't be posted if
   * debugger is enabled, warnings and errors will be.
   *
   * @param level level of debugged message
   * @param msg   debugged message
   */
  void debug(Level level, String msg);

  void debug(String msg, Object... params);

  /**
   * Prints debug message with selected log level and replaces parameters.
   * Messages of level INFO or TASK won't be posted if
   * debugger is enabled, warnings and errors will be.
   *
   * @param level level of debugged message
   * @param msg   debugged message
   */
  void debug(Level level, String msg, Object... params);

  /**
   * Prints performance debug message with selected log level and replaces parameters.
   *
   * @param msg debugged message
   */
  void performance(String monitorName, String msg, Object... params);

  Set<String> getListenedPerformance();
}
