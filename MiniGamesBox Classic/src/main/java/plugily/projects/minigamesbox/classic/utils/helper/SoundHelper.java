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

package plugily.projects.minigamesbox.classic.utils.helper;

import com.cryptomorin.xseries.XSound;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.arena.PluginArena;

public class SoundHelper {
  public static void playArenaCountdown(PluginArena arena) {
    if(arena.getTimer() > 3) {
      return;
    }
    for(Player player : arena.getPlayers()) {
      switch(arena.getTimer()) {
        case 3:
        case 2:
        case 1:
          XSound.BLOCK_NOTE_BLOCK_HAT.play(player);
          break;
        case 0:
          XSound.BLOCK_NOTE_BLOCK_FLUTE.play(player);
          break;
      }
    }
  }
}
