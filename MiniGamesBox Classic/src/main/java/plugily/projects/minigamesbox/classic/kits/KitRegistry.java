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

package plugily.projects.minigamesbox.classic.kits;

import org.bukkit.configuration.file.FileConfiguration;
import plugily.projects.minigamesbox.classic.Main;
import plugily.projects.minigamesbox.classic.kits.basekits.Kit;
import plugily.projects.minigamesbox.classic.kits.free.ExampleKit;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;

import java.util.List;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class KitRegistry {

  private final List<Kit> kits = new java.util.ArrayList<>();
  private Kit defaultKit;
  private final Main plugin;
  private static final Class<?>[] classKitNames = new Class[]{ExampleKit.class};


  public KitRegistry(Main plugin) {
    this.plugin = plugin;
    setupGameKits();
  }

  /**
   * Method for registering new kit
   *
   * @param kit Kit to register
   */
  public void registerKit(Kit kit) {
    kits.add(kit);
  }

  /**
   * Return default game kit
   *
   * @return default game kit
   */
  public Kit getDefaultKit() {
    return defaultKit;
  }

  /**
   * Sets default game kit
   *
   * @param defaultKit default kit to set, must be FreeKit
   */
  public void setDefaultKit(Kit defaultKit) {
    this.defaultKit = defaultKit;
  }

  /**
   * Returns all available kits
   *
   * @return list of all registered kits
   */
  public List<Kit> getKits() {
    return kits;
  }

  //todo default kits - kit loading - possibility to edit kits with files - patreon will be ingame gui - kits.yml
  private void setupGameKits() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "kits");
    for(Class<?> kitClass : classKitNames) {
      if(config.getBoolean("Enabled-Game-Kits." + kitClass.getSimpleName().replace("Kit", ""))) {
        try {
          kitClass.getDeclaredConstructor().newInstance();
        } catch(Exception e) {
          plugin.getLogger().log(Level.SEVERE, "Fatal error while registering existing game kit! Report this error to the developer!");
          plugin.getLogger().log(Level.SEVERE, "Cause: " + e.getMessage() + " (kitClass " + kitClass.getName() + ")");
        }
      }
    }
    setDefaultKit(new ExampleKit());
  }

}
