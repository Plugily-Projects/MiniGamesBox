/*
 * MiniGamesBox - Library box with massive content that could be seen as minigames core.
 * Copyright (C)  2021  Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package plugily.projects.minigamesbox.classic.commands.completion;

import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class CompletableArgument {

  private final String mainCommand;
  private final String argument;
  private final List<String> completions;

  public CompletableArgument(String mainCommand, String argument, List<String> completions) {
    this.mainCommand = mainCommand;
    this.argument = argument;
    this.completions = completions;
  }

  /**
   * @return main command of the argument
   */
  public String getMainCommand() {
    return mainCommand;
  }

  /**
   * @return argument name
   */
  public String getArgument() {
    return argument;
  }

  /**
   * @return all possible completions for this command argument
   */
  public List<String> getCompletions() {
    return completions;
  }
}
