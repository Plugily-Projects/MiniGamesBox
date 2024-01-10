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

package plugily.projects.minigamesbox.classic.kits.ability;

import plugily.projects.minigamesbox.classic.PluginMain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KitAbilityManager {

  private final Map<String, KitAbility> kitAbilities = new HashMap<>();
  private final PluginMain plugin;


  public KitAbilityManager(PluginMain plugin) {
    this.plugin = plugin;
    if(!plugin.getConfigPreferences().getOption("KITS")) {
      this.plugin.getDebugger().performance("Kit", "Kits are disabled, kits abilities will not be loaded!");
      return;
    }
    loadKitAbilities();
    this.plugin.getDebugger().performance("Kit", "Loaded {0} kit abilities:", kitAbilities.size());
    kitAbilities.forEach((k, v) -> {
      this.plugin.getDebugger().performance("Kit", " - {0}", k);
    });
  }

  private void loadKitAbilities() {
    kitAbilities.putAll(KitAbility.getKitAbilities());
  }

  /**
   * Returns whether option value is true or false
   *
   * @param name ability to get value from
   * @return ability
   */
  public KitAbility getKitAbility(String name) {
    KitAbility KitAbility = kitAbilities.get(name);

    if(KitAbility == null) {
      throw new IllegalStateException("Kitability with name " + name + " does not exist");
    }

    return KitAbility;
  }


  /**
   * Register a new kitAbility
   *
   * @param name   The name of ability
   * @param kitAbility Ability
   */
  public void registerKitAbility(String name, KitAbility kitAbility) {
    if(kitAbilities.containsKey(name)) {
      throw new IllegalStateException("Kit Ability " + name + " was already registered");
    }
    kitAbilities.put(name, kitAbility);
  }

  /**
   * Remove config kitAbilities that are not protected
   *
   * @param name The name of the ability
   */
  public void unregisterOption(String name) {
    KitAbility option = kitAbilities.get(name);
    if(option == null) {
      return;
    }
    kitAbilities.remove(name);
  }

  public Map<String, KitAbility> getKitAbilities() {
    return Collections.unmodifiableMap(kitAbilities);
  }
}
