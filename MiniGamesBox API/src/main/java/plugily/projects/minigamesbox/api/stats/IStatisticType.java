package plugily.projects.minigamesbox.api.stats;

import java.util.Map;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IStatisticType {

  String getName();

  String getDatabaseParameters();

  boolean isPersistent();

  boolean isProtected();

  static Map<String, IStatisticType> getStatistics() {
    return null;
  }
}
