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
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.LocationItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.MaterialMultiLocationItem;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.06.2022
 */
public class PluginLocationCategory implements SetupCategoryHandler {

  private SetupInventory setupInventory;

  private List<CategoryItemHandler> itemList = new ArrayList<>();

  @Override
  public void init(PluginSetupCategoryManager pluginSetupCategoryManager) {
    this.setupInventory = pluginSetupCategoryManager.getPluginSetupInventory();
  }

  @Override
  public void addItems(NormalFastInv gui) {
    LocationItem lobby = new LocationItem(setupInventory, new ItemBuilder(XMaterial.LAPIS_BLOCK.parseMaterial()), "Lobby", "Location where players will be teleported after they join the game", "lobbylocation");
    itemList.add(lobby);
    gui.setItem((getInventoryLine() * 9) + 1, lobby);
    LocationItem starting = new LocationItem(setupInventory, new ItemBuilder(XMaterial.EMERALD_BLOCK.parseMaterial()), "Starting", "Location where players will be teleported when the game starts", "startlocation");
    itemList.add(starting);
    gui.setItem((getInventoryLine() * 9) + 2, starting);
    LocationItem spectator = new LocationItem(setupInventory, new ItemBuilder(XMaterial.BAKED_POTATO.parseMaterial()), "Spectator", "Location where players will be teleported when they get into spectator", "spectatorlocation");
    itemList.add(spectator);
    gui.setItem((getInventoryLine() * 9) + 3, spectator);
    LocationItem end = new LocationItem(setupInventory, new ItemBuilder(XMaterial.REDSTONE_BLOCK.parseMaterial()), "Ending", "Location where players will be teleported after the game", "endlocation");
    itemList.add(end);
    gui.setItem((getInventoryLine() * 9) + 6, end);
    MaterialMultiLocationItem sign = new MaterialMultiLocationItem(setupInventory, new ItemBuilder(XMaterial.OAK_SIGN.parseMaterial()), "Game Sign", "Sign registered as Game Sign with join function", "signs", XMaterial.OAK_SIGN.parseMaterial(), true, 0);
    itemList.add(sign);
    gui.setItem((getInventoryLine() * 9) + 7, sign);
  }

  @Override
  public int getInventoryLine() {
    return 0;
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
