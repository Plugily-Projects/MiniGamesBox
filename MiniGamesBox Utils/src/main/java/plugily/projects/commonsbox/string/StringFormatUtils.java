package plugily.projects.commonsbox.string;

/**
 * @author Plajer
 * <p>
 * Created at 09.03.2019
 */
public class StringFormatUtils {

  private StringFormatUtils() {
  }

  /**
   * Format seconds to mm:ss, ex 04:02 - 4 minutes and 2 seconds
   *
   * @param secsIn seconds to format
   * @return String with formatted time
   */
  public static String formatIntoMMSS(int secsIn) {
    int minutes = secsIn / 60;
    int seconds = secsIn % 60;
    return ((minutes < 10 ? "0" : "") + minutes
        + ":" + (seconds < 10 ? "0" : "") + seconds);
  }

  /**
   * Returns progress bar in a string.
   * Whole code can be found https://www.spigotmc.org/threads/progress-bars-and-percentages.276020/
   *
   * @param current               current percentage
   * @param max                   maximum percentage
   * @param totalBars             maximum bars amount
   * @param symbol                symbol of the bar (ex. '|')
   * @param completedCharacter    character of completed bar
   * @param notCompletedCharacter character of not completed bar
   * @return String with requested progress
   */
  public static String getProgressBar(int current, int max, int totalBars, String symbol, String completedCharacter, String notCompletedCharacter) {
    float percent = (float) current / max;
    int progressBars = (int) (totalBars * percent);
    int leftOver = (totalBars - progressBars);

    StringBuilder sb = new StringBuilder();
    sb.append(completedCharacter);
    for(int i = 0; i < progressBars; i++) {
      sb.append(symbol);
    }
    sb.append(notCompletedCharacter);
    for(int i = 0; i < leftOver; i++) {
      sb.append(symbol);
    }
    return sb.toString();
  }

}
