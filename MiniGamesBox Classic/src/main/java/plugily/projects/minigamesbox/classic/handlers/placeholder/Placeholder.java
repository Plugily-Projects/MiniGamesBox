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

package plugily.projects.minigamesbox.classic.handlers.placeholder;

import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.arena.IPluginArena;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class Placeholder {


  private final String id;
  private final PlaceholderType placeholderType;
  private final PlaceholderExecutor placeholderExecutor;

  public Placeholder(String id, PlaceholderExecutor placeholderExecutor) {
    this.id = id;
    this.placeholderType = PlaceholderType.GLOBAL;
    this.placeholderExecutor = placeholderExecutor;
  }

  public Placeholder(String id, PlaceholderType placeholderType, PlaceholderExecutor placeholderExecutor) {
    this.id = id;
    this.placeholderType = placeholderType;
    this.placeholderExecutor = placeholderExecutor;
  }

  public String getId() {
    switch(placeholderType) {
      case ARENA:
        return "arena_" + id;
      case GLOBAL:
        return id;
      default:
        return id;
    }
  }

  public PlaceholderType getPlaceholderType() {
    return placeholderType;
  }

  public PlaceholderExecutor getPlaceholderExecutor() {
    return placeholderExecutor;
  }

  public String getValue() {
    // EMPTY
    return null;
  }

  public String getValue(Player player) {
    // EMPTY
    return null;
  }

  public String getValue(IPluginArena arena) {
    // EMPTY
    return null;
  }

  public String getValue(Player player, IPluginArena arena) {
    // EMPTY
    return null;
  }

  public enum PlaceholderType {
    GLOBAL, ARENA
  }

  public enum PlaceholderExecutor {
    INTERNAL, PLACEHOLDER_API, ALL
  }

}
