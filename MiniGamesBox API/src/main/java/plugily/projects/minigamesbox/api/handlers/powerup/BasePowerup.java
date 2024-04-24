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

package plugily.projects.minigamesbox.api.handlers.powerup;

import com.cryptomorin.xseries.XMaterial;
import plugily.projects.minigamesbox.api.handlers.reward.IReward;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 08.10.2021
 * The interface for power-ups
 */
public interface BasePowerup {
  /**
   * Get the key of the power-up
   *
   * @return the key
   */
  String getKey();

  /**
   * Get the name of the power-up
   *
   * @return the name
   */
  String getName();

  /**
   * Get the description of the power-up
   *
   * @return the description
   */
  String getDescription();

  /**
   * Get the display material of the power-up
   *
   * @return the material
   */
  XMaterial getMaterial();


  List<String> getEffects();

  PotionType getPotionType();

  /**
   * Get the pickup consumer for the power-up
   *
   * @return the pickup consumer
   */
  Consumer<IPowerupPickupHandler> getOnPickup();

  Set<IReward> getRewards();

  enum PotionType {
    PLAYER, ALL
  }
}

