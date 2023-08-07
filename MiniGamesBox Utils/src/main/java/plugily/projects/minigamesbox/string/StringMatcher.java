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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 09.03.2019
 */
public class StringMatcher {

  private StringMatcher() {
  }

  /**
   * Matches base string with possible other arguments
   *
   * @param base          base string
   * @param possibilities possible similar strings
   * @return list of matches
   */
  public static List<Match> match(String base, List<String> possibilities) {
    possibilities.sort((o1, o2) ->
        o1.length() == o2.length() ? 0 : Integer.compare(o2.length(), o1.length()));

    int baseLength = base.length();

    Match bestMatch = new Match(base, -1);
    List<Match> otherMatches = new ArrayList<>(possibilities.size());

    for(String poss : possibilities) {
      if(poss.isEmpty()) {
        continue;
      }

      int matches = 0;
      int pos = -1;
      int min = Math.min(baseLength, poss.length());

      for(int i = 0; i < min; i++) {
        if(base.charAt(i) == poss.charAt(i)) {
          if(pos != -1) {
            break;
          }

          pos = i;
        }
      }

      if(pos != -1) {
        int last = poss.length() - 1;

        for(int i = 0; i < min; i++) {
          if(base.charAt(i) == poss.charAt(Math.min(i + pos, last))) {
            matches++;
          }
        }
      }

      if(matches > bestMatch.length) {
        bestMatch = new Match(poss, matches);
      }

      if(matches > 0 && matches >= bestMatch.length
          && !poss.equalsIgnoreCase(bestMatch.match)) {
        otherMatches.add(new Match(poss, matches));
      }
    }

    otherMatches.add(bestMatch);

    Collections.sort(otherMatches);
    return otherMatches;
  }

  public static class Match implements Comparable<Match> {
    protected final String match;
    protected final int length;

    protected Match(String s, int i) {
      this.match = s;
      this.length = i;
    }

    public String getMatch() {
      return match;
    }

    @Override
    public int compareTo(Match other) {
      return Integer.compare(other.length, length);
    }
  }

}
