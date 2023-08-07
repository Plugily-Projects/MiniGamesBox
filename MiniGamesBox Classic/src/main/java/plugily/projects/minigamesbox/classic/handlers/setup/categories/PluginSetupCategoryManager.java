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

package plugily.projects.minigamesbox.classic.handlers.setup.categories;

import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.items.template.AddonItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.template.ArenaDataItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.template.CategoryItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.template.MenuItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.template.PatreonItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.template.RegisterIndicatorItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.template.RegisterItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.template.TranslateItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.06.2022
 */
public class PluginSetupCategoryManager {

  private final Map<SetupCategory, SetupCategoryHandler> categoryHandler = new EnumMap<>(SetupCategory.class);

  private final SetupInventory setupInventory;

  public PluginSetupCategoryManager(SetupInventory setupInventory) {
    this.setupInventory = setupInventory;
    categoryHandler.put(SetupCategory.LOCATIONS, new PluginLocationCategory());
    categoryHandler.put(SetupCategory.COUNTABLE, new PluginCountableCategory());
    categoryHandler.put(SetupCategory.VALUES, new PluginValueCategory());
    categoryHandler.put(SetupCategory.SWITCH, new PluginSwitchCategory());
    categoryHandler.put(SetupCategory.SPECIFIC, new PluginSpecificCategory());
    init();
  }

  public void init() {
    for(SetupCategoryHandler handler : categoryHandler.values()) {
      handler.init(this);
    }
  }

  public void addAllItems(NormalFastInv gui) {
    categoryHandler.forEach((setupCategory, setupCategoryHandler) -> {
      setupCategoryHandler.addItems(gui);
      if(setupCategoryHandler.getInventoryLine() < 5) {
        gui.setItem(setupCategoryHandler.getInventoryLine() * 9, new CategoryItem(setupInventory, setupCategory));
        gui.setItem((setupCategoryHandler.getInventoryLine() * 9) + 8, new RegisterIndicatorItem(setupInventory, setupCategory, setupCategoryHandler));
      }
      gui.setItem(45, new MenuItem(setupInventory));
      gui.setItem(47, new TranslateItem(setupInventory));
      gui.setItem(49, new ArenaDataItem(setupInventory));
      gui.setItem(51, new PatreonItem(setupInventory));
      gui.setItem(52, new AddonItem(setupInventory));
      gui.setItem(53, new RegisterItem(setupInventory, this));
    });
  }

  public SetupInventory getPluginSetupInventory() {
    return setupInventory;
  }

  public boolean canRegister() {
    return categoryHandler.entrySet().stream().allMatch(setupCategory -> setupCategory.getValue().isDone());
  }

  public Map<SetupCategory, SetupCategoryHandler> getCategoryHandler() {
    return categoryHandler;
  }
}
