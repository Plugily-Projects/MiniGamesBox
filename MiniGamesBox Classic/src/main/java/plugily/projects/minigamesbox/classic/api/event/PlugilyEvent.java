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

package plugily.projects.minigamesbox.classic.api.event;

import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.minigamesbox.api.events.IPlugilyEvent;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;

/**
 * Represents Plugily Projects game related events.
 */
public abstract class PlugilyEvent extends Event implements IPlugilyEvent {

  protected PluginArena arena;
  protected String pluginName;

  public PlugilyEvent(PluginArena eventArena) {
    arena = eventArena;
    if(arena != null) {
      pluginName = eventArena.getPlugin().getName();
    } else {
      pluginName = JavaPlugin.getPlugin(PluginMain.class).getName();
    }
  }

  @Override
  public PluginArena getArena() {
    return arena;
  }

  @Override
  public String getPluginName() {
    return pluginName;
  }
}
