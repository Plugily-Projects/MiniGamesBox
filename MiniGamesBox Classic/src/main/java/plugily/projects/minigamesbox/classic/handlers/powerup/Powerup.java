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


import com.cryptomorin.xseries.XMaterial;
import plugily.projects.minigamesbox.classic.handlers.reward.Reward;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 08.10.2021
 */
public class Powerup implements BasePowerup {

  private final String key;
  private final String name;
  private final String description;
  private final List<String> effects;
  private final PotionType potionType;
  private final XMaterial material;
  private final Consumer<PowerupPickupHandler> onPickup;
  private final Set<Reward> rewards;

  public Powerup(String id, String name, String description, XMaterial material, List<String> effects, PotionType potionType, Set<Reward> rewards, Consumer<PowerupPickupHandler> pickup) {
    this.key = id;
    this.name = name;
    this.description = description;
    this.material = material;
    this.effects = effects;
    this.potionType = potionType;
    this.rewards = rewards;
    onPickup = pickup;
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public XMaterial getMaterial() {
    return material;
  }

  @Override
  public List<String> getEffects() {
    return effects;
  }

  @Override
  public PotionType getPotionType() {
    return potionType;
  }

  @Override
  public Set<Reward> getRewards() {
    return rewards;
  }

  @Override
  public Consumer<PowerupPickupHandler> getOnPickup() {
    return onPickup;
  }
}
