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

package plugily.projects.minigamesbox.classic.arena.states;

import plugily.projects.minigamesbox.classic.Main;
import plugily.projects.minigamesbox.classic.arena.Arena;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class InGameState implements ArenaStateHandler {

  private Main plugin;

  @Override
  public void init(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public void handleCall(Arena arena) {
    bossBarUpdate(arena);

    arena.setTimer(arena.getTimer() - 1);
  }

  private void bossBarUpdate(Arena arena) {
    if(arena.getArenaOption("BAR_TOGGLE_VALUE") > 5) {
      if(arena.getGameBar() != null) {
        //arena.getGameBar().setTitle(plugin.getChatManager().colorMessage(Messages.BOSSBAR_IN_GAME_WAVE).replace("%wave%", Integer.toString(arena.getWave())));
      }
      arena.changeArenaOptionBy("BAR_TOGGLE_VALUE", 1);
      if(arena.getArenaOption("BAR_TOGGLE_VALUE") > 10) {
        arena.setArenaOption("BAR_TOGGLE_VALUE", 0);
      }
    } else {
      if(arena.getGameBar() != null) {

        //arena.getGameBar().setTitle(plugin.getChatManager().colorMessage(Messages.BOSSBAR_IN_GAME_INFO).replace("%wave%", Integer.toString(arena.getWave())));
      }
      arena.changeArenaOptionBy("BAR_TOGGLE_VALUE", 1);
    }
  }

}
