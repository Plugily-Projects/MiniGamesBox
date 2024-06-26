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

package plugily.projects.minigamesbox.classic.arena.managers;

import plugily.projects.minigamesbox.api.arena.managers.IPluginMapRestorerManager;
import plugily.projects.minigamesbox.classic.arena.PluginArena;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 02.12.2021
 */
public class PluginMapRestorerManager implements IPluginMapRestorerManager {

  public final PluginArena arena;

  public PluginMapRestorerManager(PluginArena arena) {
    this.arena = arena;
  }

  @Override
  public void fullyRestoreArena() {
    arena.getPlugin().getDebugger().debug("Arena {0} Restoring Arena", arena.getId());
    arena.loadArenaOptions();
    arena.getScoreboardManager().stopAllScoreboards();
    arena.getPlayers().clear();
  }

}
