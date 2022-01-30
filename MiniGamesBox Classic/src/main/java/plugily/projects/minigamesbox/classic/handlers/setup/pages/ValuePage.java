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
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.classic.handlers.setup.PluginSetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupUtilities;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.conversation.SimpleConversationBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 04.01.2022
 */
public class ValuePage extends NormalFastInv implements SetupPage {

  private final PluginSetupInventory setupInventory;

  public ValuePage(int size, String title, PluginSetupInventory pluginSetupInventory) {
    super(size, title);
    this.setupInventory = pluginSetupInventory;
    prepare();
  }

  @Override
  public void prepare() {
    injectItems();
    setForceRefresh(true);
    setupInventory.getPlugin().getSetupUtilities().setDefaultItems(setupInventory, this, XMaterial.BLUE_STAINED_GLASS_PANE.parseItem(), XMaterial.ORANGE_STAINED_GLASS_PANE.parseItem(), event -> setupInventory.open(SetupUtilities.InventoryStage.PAGED_LOCATIONS), XMaterial.WHITE_STAINED_GLASS_PANE.parseItem(), event -> setupInventory.open(SetupUtilities.InventoryStage.PAGED_COUNTABLE));
    refresh();
  }

  @Override
  public void injectItems() {
    setItem(43, ClickableItem.of(new ItemBuilder(Material.NAME_TAG)
        .name(setupInventory.getPlugin().getChatManager().colorRawMessage("&e&lChange Map Name"))
        .lore(ChatColor.GRAY + "Click to set arena map name")
        .lore("", setupInventory.getPlugin().getChatManager().colorRawMessage("&a&lCurrently: &e" + setupInventory.getPlugin().getSetupUtilities().getConfig().getString("instances." + setupInventory.getArena().getId() + ".mapname")))
        .build(), event -> {
      event.getWhoClicked().closeInventory();
      new SimpleConversationBuilder(setupInventory.getPlugin()).withPrompt(new StringPrompt() {
        @Override
        public @NotNull String getPromptText(ConversationContext context) {
          return setupInventory.getPlugin().getChatManager().colorRawMessage(setupInventory.getPlugin().getChatManager().getPrefix() + "&ePlease type in chat arena name! You can use color codes.");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
          String name = setupInventory.getPlugin().getChatManager().colorRawMessage(input);
          setupInventory.getPlayer().sendRawMessage(setupInventory.getPlugin().getChatManager().colorRawMessage("&e✔ Completed | &aName of arena " + setupInventory.getArena().getId() + " set to " + name));
          setupInventory.getArena().setMapName(name);
          setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".mapname", setupInventory.getArena().getMapName());
          ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");

          setupInventory.open(SetupUtilities.InventoryStage.PAGED_LOCATIONS);
          return Prompt.END_OF_CONVERSATION;
        }
      }).buildFor(setupInventory.getPlayer());
    }));
  }
}
