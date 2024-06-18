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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.categories.PluginSetupCategoryManager;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.06.2022
 */
public class RegisterItem implements ClickableItem {
  private final SetupInventory setupInventory;

  private final PluginSetupCategoryManager pluginSetupCategoryManager;
  private final RegisterStatus registerStatus;

  public RegisterItem(SetupInventory setupInventory, PluginSetupCategoryManager pluginSetupCategoryManager) {
    this.setupInventory = setupInventory;
    this.pluginSetupCategoryManager = pluginSetupCategoryManager;
    if(pluginSetupCategoryManager.canRegister()) {
      IPluginArena arena = setupInventory.getPlugin().getArenaRegistry().getArena(setupInventory.getArenaKey());
      if(arena != null && arena.isReady()) {
        registerStatus = RegisterStatus.ARENA_READY;
      } else {
        registerStatus = RegisterStatus.ARENA_REGISTER;
      }
    } else {
      registerStatus = RegisterStatus.ARENA_SETUP;
    }
  }

  @Override
  public ItemStack getItem() {
    ItemBuilder item;
    switch(registerStatus) {
      case ARENA_READY:
        item = new ItemBuilder(XMaterial.POTATO.parseMaterial())
            .name(new MessageBuilder("&a&lArena Setup finished - Congratulation").build())
            .lore(ChatColor.GRAY + "This arena is already registered!")
            .lore(ChatColor.GRAY + "You can play on this arena now!")
            .lore("&aControls")
            .lore("&eCLICK \n&7-> Reload arena");
        break;
      case ARENA_REGISTER:
        item = new ItemBuilder(XMaterial.FIREWORK_ROCKET.parseMaterial())
            .name(new MessageBuilder("&e&lRegister Arena - Finish Setup").build())
            .lore(ChatColor.GRAY + "Click this when you're done with configuration.")
            .lore(ChatColor.GRAY + "It will validate and register your arena.")
            .lore(ChatColor.GRAY + "Good job, you went through whole setup!")
            .lore("&aControls")
            .lore("&eCLICK \n&7-> Register arena")
            .enchantment(Enchantment.DURABILITY);
        break;
      case ARENA_SETUP:
      default:
        item = new ItemBuilder(XMaterial.BARRIER.parseMaterial())
            .name(new MessageBuilder("&c&lArena Setup not finished").build())
            .lore(ChatColor.GRAY + "Go ahead with the setup!")
            .lore(ChatColor.GRAY + "Feel free to watch the tutorials that you can find on the gui!")
            .lore(ChatColor.GRAY + "Support: discord.plugily.xyz - #general-questions");

        break;
    }

    return item.colorizeItem().build();
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    switch(registerStatus) {
      case ARENA_READY:
      case ARENA_REGISTER:
        IPluginArena arena = setupInventory.getPlugin().getArenaRegistry().getArena(setupInventory.getArenaKey());
        if(arena != null) {
          arena.setReady(true);
        }
        setupInventory.closeInventory(event.getWhoClicked());
        break;
      case ARENA_SETUP:
      default:
        break;
    }
  }

  private enum RegisterStatus {
    ARENA_READY, ARENA_REGISTER, ARENA_SETUP
  }

}