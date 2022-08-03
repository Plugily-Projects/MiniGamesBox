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

package plugily.projects.minigamesbox.classic.handlers.setup.categories;

import com.cryptomorin.xseries.XMaterial;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.CategoryItemHandler;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.ValueItem;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.06.2022
 */
public class PluginValueCategory implements SetupCategoryHandler {

  private SetupInventory setupInventory;
  private List<CategoryItemHandler> itemList = new ArrayList<>();

  @Override
  public void init(PluginSetupCategoryManager pluginSetupCategoryManager) {
    this.setupInventory = pluginSetupCategoryManager.getPluginSetupInventory();
  }

  @Override
  public void addItems(NormalFastInv gui) {
    ValueItem mapName = new ValueItem(setupInventory, new ItemBuilder(XMaterial.NAME_TAG.parseMaterial()), "Map", "Set another map name... Or stay with it?", "mapname");
    itemList.add(mapName);
    gui.setItem((getInventoryLine() * 9) + 1, mapName);
  }

  @Override
  public int getInventoryLine() {
    return 2;
  }

  @Override
  public boolean isDone() {
    return itemList.stream().allMatch(CategoryItemHandler::getSetupStatus);
  }

  public List<CategoryItemHandler> getItemList() {
    return itemList;
  }

  public SetupInventory getSetupInventory() {
    return setupInventory;
  }
}

