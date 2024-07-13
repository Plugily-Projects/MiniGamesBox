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

import com.cryptomorin.xseries.XEnchantment;
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
 * Created at 21.06.2022
 */
public class PatreonItem implements ClickableItem {

  private final SetupInventory setupInventory;

  public PatreonItem(SetupInventory setupInventory) {
    this.setupInventory = setupInventory;
  }

  @Override
  public ItemStack getItem() {
    ItemBuilder item = new ItemBuilder(XMaterial.GOLD_NUGGET.parseMaterial());
    item
        .name("&6&l► Support Development ◄")
        .lore(ChatColor.GRAY + "We are a open-source organisation and")
        .lore(ChatColor.GOLD + "need your attention and mindset of helping each other out!")
        .lore(ChatColor.GOLD + "While you can get with our patreon subscription the addon you can also")
        .lore(ChatColor.GRAY + "support us by donating on other platforms such as paypal!")
        .lore("&aControls")
        .lore("&eCLICK \n&7-> Link for donation options")
        .enchantment(XEnchantment.UNBREAKING.getEnchant())
        .colorizeItem();
    return item.build();
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    setupInventory.closeInventory(event.getWhoClicked());
    new MessageBuilder("&6Check our donation possibilities at").prefix().send(event.getWhoClicked());
    new MessageBuilder("&6 https://wiki.plugily.xyz/donate", false).send(event.getWhoClicked());
  }
}
