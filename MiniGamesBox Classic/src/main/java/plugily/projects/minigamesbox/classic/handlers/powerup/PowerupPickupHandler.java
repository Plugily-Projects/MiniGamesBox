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

package plugily.projects.minigamesbox.classic.handlers.powerup;

import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.api.handlers.powerup.BasePowerup;
import plugily.projects.minigamesbox.api.handlers.powerup.IPowerupPickupHandler;
import plugily.projects.minigamesbox.classic.arena.PluginArena;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 08.10.2021
 */
public class PowerupPickupHandler implements IPowerupPickupHandler {

  private final BasePowerup powerup;
  private final PluginArena arena;
  private final Player player;

  public PowerupPickupHandler(BasePowerup powerup, PluginArena arena, Player player) {
    this.powerup = powerup;
    this.arena = arena;
    this.player = player;
  }

  @Override
  public BasePowerup getPowerup() {
    return powerup;
  }

  @Override
  public IPluginArena getArena() {
    return arena;
  }

  @Override
  public Player getPlayer() {
    return player;
  }

}
