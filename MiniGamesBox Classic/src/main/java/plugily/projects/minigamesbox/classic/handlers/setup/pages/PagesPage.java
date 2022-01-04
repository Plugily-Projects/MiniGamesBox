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

package plugily.projects.minigamesbox.classic.handlers.setup.pages;

import com.cryptomorin.xseries.XMaterial;
import plugily.projects.minigamesbox.classic.handlers.setup.PluginSetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupUtilities;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 04.01.2022
 */
public class PagesPage extends NormalFastInv implements SetupPage {

  private final PluginSetupInventory setupInventory;

  public PagesPage(int size, String title, PluginSetupInventory pluginSetupInventory) {
    super(size, title);
    this.setupInventory = pluginSetupInventory;
    prepare();
  }

  @Override
  public void prepare() {
    injectItems();
    setDefaultItem(ClickableItem.of(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()));
    refresh();
  }

  @Override
  public void injectItems() {
    setItem(1, new ItemBuilder(XMaterial.ORANGE_STAINED_GLASS_PANE.parseMaterial()).build(), event -> setupInventory.open(SetupUtilities.InventoryStage.PAGED_LOCATIONS));
    setItem(3, new ItemBuilder(XMaterial.BLUE_STAINED_GLASS_PANE.parseMaterial()).build(), event -> setupInventory.open(SetupUtilities.InventoryStage.PAGED_VALUES));
    setItem(5, new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseMaterial()).build(), event -> setupInventory.open(SetupUtilities.InventoryStage.PAGED_COUNTABLE));
    setItem(7, new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE.parseMaterial()).build(), event -> setupInventory.open(SetupUtilities.InventoryStage.PAGED_BOOLEAN));
    setItem(45, new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE.parseMaterial()).build(), event -> setupInventory.open(SetupUtilities.InventoryStage.SETUP_GUI));
  }


}
