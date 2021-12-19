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

package plugily.projects.minigamesbox.classic.handlers.placeholder;

import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.arena.PluginArena;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class Placeholder {


  private final String id;
  private final PlaceholderType placeholderType;

  public Placeholder(String id) {
    this.id = id;
    this.placeholderType = PlaceholderType.GLOBAL;
  }

  public Placeholder(String id, PlaceholderType placeholderType) {
    this.id = id;
    this.placeholderType = placeholderType;
  }

  public String getId() {
    return id;
  }

  public PlaceholderType getPlaceholderType() {
    return placeholderType;
  }

  public String getValue(Player player) {
    // EMPTY
    throw new UnsupportedOperationException("Method must be overridden");
  }

  public String getValue(Player player, PluginArena arena) {
    // EMPTY
    throw new UnsupportedOperationException("Method must be overridden");
  }

  public enum PlaceholderType {
    GLOBAL, ARENA
  }

}
