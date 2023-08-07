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

package plugily.projects.minigamesbox.classic.handlers.setup.items.template;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventoryUtils;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.06.2022
 */
public class ArenaDataItem implements ClickableItem {
  private final SetupInventory setupInventory;

  public ArenaDataItem(SetupInventory setupInventory) {
    this.setupInventory = setupInventory;
  }

  @Override
  public ItemStack getItem() {
    ItemBuilder item = new ItemBuilder(XMaterial.BREAD.parseMaterial())
        .name("&a&l► Arena Information ◄")
        .lore(ChatColor.GRAY + "Saves the current progress")
        .lore("&aControls")
        .lore("&eLEFT_CLICK \n&7-> Main tutorial video")
        .lore("&eRIGHT_CLICK \n&7-> Reload arena data");
    ConfigurationSection section = setupInventory.getConfig().getConfigurationSection("instances." + setupInventory.getArenaKey());
    if(section != null) {
      item
          .lore("Values on file: " + section.getKeys(true));
    }
    return item.colorizeItem().build();
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    setupInventory.closeInventory(event.getWhoClicked());
    switch(event.getClick()) {
      case LEFT:
        new MessageBuilder("&aCheck tutorial video at").prefix().send(event.getWhoClicked());
        new MessageBuilder("&7" + setupInventory.getTutorialSite(), false).send(event.getWhoClicked());
        break;
      case RIGHT:
        setupInventory.open(SetupInventoryUtils.SetupInventoryStage.ARENA_EDITOR);
        new MessageBuilder("&aarenas.yml file reloaded ;)").prefix().send(event.getWhoClicked());
        break;
      default:
        break;
    }

  }
}
