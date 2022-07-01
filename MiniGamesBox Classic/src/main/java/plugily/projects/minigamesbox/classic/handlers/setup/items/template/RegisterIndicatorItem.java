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

package plugily.projects.minigamesbox.classic.handlers.setup.items.template;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.categories.SetupCategory;
import plugily.projects.minigamesbox.classic.handlers.setup.categories.SetupCategoryHandler;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 27.06.2022
 */
public class RegisterIndicatorItem implements ClickableItem {
  private final SetupInventory setupInventory;

  private final SetupCategory setupCategory;
  private final SetupCategoryHandler setupCategoryHandler;

  public RegisterIndicatorItem(SetupInventory setupInventory, SetupCategory setupCategory, SetupCategoryHandler setupCategoryHandler) {
    this.setupInventory = setupInventory;
    this.setupCategory = setupCategory;
    this.setupCategoryHandler = setupCategoryHandler;
  }

  @Override
  public ItemStack getItem() {
    ItemBuilder item;
    if(setupCategoryHandler.isDone()) {
      item = new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE.parseMaterial())
          .name("DONE | " + setupCategory.name().toUpperCase())
          .lore(ChatColor.GREEN + "GREEN Category fully set up!")
          .lore(ChatColor.RED + "RED Category needs set up!")
          .colorizeItem();
    } else {
      item = new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE.parseMaterial())
          .name("NEEDS SETUP | " + setupCategory.name().toUpperCase())
          .lore(ChatColor.GREEN + "GREEN - Category fully set up!")
          .lore(ChatColor.RED + "RED - Category needs set up!")
          .colorizeItem();
    }

    return item.build();
  }

  @Override
  public void onClick(InventoryClickEvent event) {

  }
}