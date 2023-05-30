/*
 *  MiniGamesBox - Library box with massive content that could be seen as minigames core.
 *  Copyright (C) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.minigamesbox.string;

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
