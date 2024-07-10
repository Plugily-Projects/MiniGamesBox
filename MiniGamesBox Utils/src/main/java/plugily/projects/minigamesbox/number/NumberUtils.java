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

package plugily.projects.minigamesbox.number;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * @author Plajer
 * <p>
 * Created at 09.03.2019
 */
public class NumberUtils {

  public static Optional<Integer> parseInt(String s) {
    try {
      return Optional.of(Integer.parseInt(s));
    } catch(NumberFormatException ex) {
      return Optional.empty();
    }
  }

  public static Optional<Double> parseDouble(String s) {
    try {
      return Optional.of(Double.parseDouble(s));
    } catch(NumberFormatException ex) {
      return Optional.empty();
    }
  }

  @Deprecated
  public static boolean isInteger(String s) {
    try {
      Integer.parseInt(s);
      return true;
    } catch(NumberFormatException ex) {
      return false;
    }
  }

  @Deprecated
  public static boolean isDouble(String s) {
    try {
      Double.parseDouble(s);
      return true;
    } catch(NumberFormatException ex) {
      return false;
    }
  }

  /**
   * Rounds value to x places
   *
   * @param value  value to round
   * @param places places
   * @return rounded value
   * @throws IllegalArgumentException when places to round is lower than 0
   */
  public static double round(double value, int places) {
    if(places < 0) {
      throw new IllegalArgumentException();
    }

    BigDecimal bd = BigDecimal.valueOf(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

}
