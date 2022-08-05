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

package plugily.projects.minigamesbox.classic.handlers.setup.items.category;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.utils.conversation.SimpleConversationBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;

import java.util.function.Consumer;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 26.06.2022
 */
public class ValueItem implements CategoryItemHandler {
  private final SetupInventory setupInventory;
  private final ItemStack item;
  private final String name;
  private final String description;
  private final String keyName;
  private final Consumer<InventoryClickEvent> clickConsumer;

  public ValueItem(SetupInventory setupInventory, ItemBuilder item, String name, String description, String keyName, Consumer<InventoryClickEvent> clickConsumer) {
    this.setupInventory = setupInventory;
    item
        .name("&7Change &a" + name.toUpperCase() + " &7value")
        .lore("&aInfo")
        .lore("&7" + description)
        .lore("&aStatus")
        .lore("&7" + getSetupInfo())
        .lore("&aControls")
        .lore("&eLEFT_CLICK \n&7-> Set the value by typing in chat")
        .colorizeItem();

    this.item = item.build();
    this.name = name;
    this.description = description;
    this.keyName = keyName;
    this.clickConsumer = clickConsumer;
  }

  public ValueItem(SetupInventory setupInventory, ItemBuilder item, String name, String description, String keyName) {
    this(setupInventory, item, name, description, keyName, emptyConsumer -> {
    });
  }


  @Override
  public ItemStack getItem() {
    return item;
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    switch(event.getClick()) {
      case LEFT:
        new SimpleConversationBuilder(setupInventory.getPlugin()).withPrompt(new StringPrompt() {
          @Override
          public @NotNull String getPromptText(ConversationContext context) {
            return new MessageBuilder("&ePlease type in chat " + name.toUpperCase() + " name! You can use color codes.").prefix().build();
          }

          @Override
          public Prompt acceptInput(ConversationContext context, String input) {
            String name = new MessageBuilder(input, false).build();
            context.getForWhom().sendRawMessage(new MessageBuilder("&e✔ Completed | &aName of " + name.toUpperCase() + " " + setupInventory.getArenaKey() + " set to " + name).build());
            setupInventory.setConfig(keyName, name);
            return Prompt.END_OF_CONVERSATION;
          }
        }).buildFor((Player) event.getWhoClicked());
        break;
      default:
        break;
    }
    event.getWhoClicked().closeInventory();
  }

  @Override
  public String getSetupInfo() {
    return setupInventory.isOptionDone(keyName);
  }

  @Override
  public boolean getSetupStatus() {
    return getSetupInfo().contains("✔");
  }
}
