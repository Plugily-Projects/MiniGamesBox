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
package plugily.projects.minigamesbox.classic.handlers.holiday;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.PluginMain;

import java.time.LocalDateTime;
/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */

/**
 * The interface for all holidays
 */
public interface Holiday {

  String getName();

  /**
   * Check if the date is the holiday
   *
   * @param dateTime the date
   * @return true if it is
   */
  boolean isHoliday(LocalDateTime dateTime);

  /**
   * Enable the holiday
   *
   * @param plugin the plugin
   */
  void enable(PluginMain plugin);

  /**
   * Apply creature effects
   *
   * @param creature the creature
   */
  default void applyCreatureEffects(Creature creature) {
    // EMPTY
  }

  /**
   * Apply death effects for the entity
   *
   * @param entity the entity
   */
  default void applyDeathEffects(Entity entity) {
    // EMPTY
  }

  /**
   * Apply kill effects to dead and alive entity
   *
   * @param dead  dead player
   * @param alive alive player
   */
  default void applyKillEffects(Entity dead, Entity alive) {
    // EMPTY
  }

  /**
   * Apply player effects for the player
   *
   * @param player the player
   */
  default void applyPlayerEffects(Player player) {
    // EMPTY
  }

}
