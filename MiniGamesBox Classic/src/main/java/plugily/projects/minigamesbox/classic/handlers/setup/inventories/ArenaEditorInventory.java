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

package plugily.projects.minigamesbox.classic.handlers.setup.inventories;

import com.cryptomorin.xseries.XMaterial;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.categories.PluginSetupCategoryManager;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.06.2022
 */
public class ArenaEditorInventory extends NormalFastInv implements InventoryHandler {

  private final SetupInventory setupInventory;
  private final PluginSetupCategoryManager pluginSetupCategoryManager;

  public ArenaEditorInventory(int size, String title, SetupInventory setupInventory) {
    super(size, title);
    this.setupInventory = setupInventory;
    this.pluginSetupCategoryManager = setupInventory.getPlugin().getSetupCategoryManager(setupInventory);
    prepare();
  }

  @Override
  public void prepare() {
    injectItems();
    addCloseHandler(event -> {
      if(pluginSetupCategoryManager.canRegister()) {
        IPluginArena arena = setupInventory.getPlugin().getArenaRegistry().getArena(setupInventory.getArenaKey());
        if(arena != null && arena.isReady()) {
          setupInventory.setConfig("isdone", true);
          setupInventory.getPlugin().getArenaRegistry().registerArena(setupInventory.getArenaKey());
          new MessageBuilder("&aArena " + arena.getId() + " reloaded ;)").prefix().send(event.getPlayer());
        }
      }
    });
    setDefaultItem(ClickableItem.of(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()).name(" ").build()));
    setForceRefresh(true);
    refresh();
  }

  @Override
  public void injectItems() {
    pluginSetupCategoryManager.addAllItems(this);
  }

  public PluginSetupCategoryManager getSetupCategoryManager() {
    return pluginSetupCategoryManager;
  }
}
