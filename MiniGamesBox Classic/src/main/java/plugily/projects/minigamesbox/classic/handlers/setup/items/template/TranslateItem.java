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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 27.06.2022
 */
public class TranslateItem implements ClickableItem {
  private final SetupInventory setupInventory;

  public TranslateItem(SetupInventory setupInventory) {
    this.setupInventory = setupInventory;
  }

  @Override
  public ItemStack getItem() {
    ItemBuilder item = new ItemBuilder(XMaterial.MELON_SLICE.parseMaterial());
    item
        .name("&b&l► Translate our plugins ◄ &8")
        .lore(ChatColor.GRAY + "Help your country by translating")
        .lore(ChatColor.GRAY + "the plugin for all!")
        .lore(ChatColor.AQUA + "We use PoEditor for our language translations")
        .lore(ChatColor.AQUA + "You can just edit your language.yml or be part")
        .lore(ChatColor.AQUA + "of translator pool of plugily by")
        .lore(ChatColor.AQUA + "translating the plugin on PoEditor")
        .lore("&aControls")
        .lore("&eCLICK \n&7-> Translation program link")
        .colorizeItem();
    return item.build();
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    setupInventory.closeInventory(event.getWhoClicked());
    new MessageBuilder("&bCheck translation program at").prefix().send(event.getWhoClicked());
    new MessageBuilder("&b https://wiki.plugily.xyz/translate#" + setupInventory.getPlugin().getPluginNamePrefixLong().toLowerCase(), false).send(event.getWhoClicked());
  }
}