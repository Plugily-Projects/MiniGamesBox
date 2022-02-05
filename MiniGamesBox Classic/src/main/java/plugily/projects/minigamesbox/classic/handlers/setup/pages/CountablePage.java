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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.PluginSetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupUtilities;
import plugily.projects.minigamesbox.classic.handlers.setup.items.CountItem;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 04.01.2022
 */
public class CountablePage extends NormalFastInv implements SetupPage {

  private final PluginSetupInventory setupInventory;

  public CountablePage(int size, String title, PluginSetupInventory pluginSetupInventory) {
    super(size, title);
    this.setupInventory = pluginSetupInventory;
    prepare();
  }

  @Override
  public void prepare() {
    injectItems();
    setForceRefresh(true);
    setupInventory.getPlugin().getSetupUtilities().setDefaultItems(setupInventory, this, XMaterial.WHITE_STAINED_GLASS_PANE.parseItem(), XMaterial.BLUE_STAINED_GLASS_PANE.parseItem(), event -> setupInventory.open(SetupUtilities.InventoryStage.PAGED_VALUES), XMaterial.GREEN_STAINED_GLASS_PANE.parseItem(), event -> setupInventory.open(SetupUtilities.InventoryStage.PAGED_BOOLEAN));
    refresh();
  }

  @Override
  public void injectItems() {
    setItem(1, new CountItem(
        new ItemBuilder(Material.COAL).amount(setupInventory.getPlugin().getSetupUtilities().getMinimumValueHigherThanZero("minimumplayers", setupInventory))
            .name(new MessageBuilder("&e&lSet Minimum Players Amount").build())
            .lore(ChatColor.GRAY + "LEFT click to decrease")
            .lore(ChatColor.GRAY + "RIGHT click to increase")
            .lore(ChatColor.DARK_GRAY + "(how many players are needed")
            .lore(ChatColor.DARK_GRAY + "for game to start lobby countdown)")
            .lore("", setupInventory.getPlugin().getSetupUtilities().isOptionDone("minimumplayers", setupInventory))
            .build(), event -> {
      ItemStack itemStack = event.getInventory().getItem(event.getSlot()), currentItem = event.getCurrentItem();
      if(itemStack == null || currentItem == null) {
        return;
      }
      setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".minimumplayers", currentItem.getAmount());
      setupInventory.getArena().setMinimumPlayers(currentItem.getAmount());
      ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
      refresh();
    }));

    setItem(10, new CountItem(new ItemBuilder(Material.REDSTONE)
        .amount(setupInventory.getPlugin().getSetupUtilities().getMinimumValueHigherThanZero("maximumplayers", setupInventory))
        .name(new MessageBuilder("&e&lSet Maximum Players Amount").build())
        .lore(ChatColor.GRAY + "LEFT click to decrease")
        .lore(ChatColor.GRAY + "RIGHT click to increase")
        .lore(ChatColor.DARK_GRAY + "(how many players arena can hold)")
        .lore("", setupInventory.getPlugin().getSetupUtilities().isOptionDone("maximumplayers", setupInventory))
        .build(), event -> {
      ItemStack currentItem = event.getCurrentItem();
      if(currentItem == null) {
        return;
      }
      setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".maximumplayers", currentItem.getAmount());
      setupInventory.getArena().setMaximumPlayers(currentItem.getAmount());
      ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
      refresh();
    }));
  }

  @Override
  protected void onClick(InventoryClickEvent event) {
    injectItems();
    refresh();
  }
}
