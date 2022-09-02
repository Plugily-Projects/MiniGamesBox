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
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.utils.conversation.SimpleConversationBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.common.RefreshableFastInv;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 26.06.2022
 */
public class SwitchItem implements CategoryItemHandler {
  private final SetupInventory setupInventory;
  private final ItemStack item;
  private final String name;
  private final String description;
  private final String keyName;

  private final List<String> switches;
  private final Consumer<InventoryClickEvent> clickConsumer;

  public SwitchItem(SetupInventory setupInventory, ItemBuilder item, String name, String description, String keyName, List<String> switches, Consumer<InventoryClickEvent> clickConsumer) {
    this.setupInventory = setupInventory;
    this.switches = switches;
    setLore(item);
    item
        .name("&7Switch &a" + name.toUpperCase() + " &7value")
        .colorizeItem();
    this.item = item.build();
    this.name = name;
    this.description = description;
    this.keyName = keyName;
    this.clickConsumer = clickConsumer;
  }

  public SwitchItem(SetupInventory setupInventory, ItemBuilder item, String name, String description, String keyName, List<String> switches) {
    this(setupInventory, item, name, description, keyName, switches, emptyConsumer -> {
    });
  }

  private void setLore(ItemBuilder itemBuilder) {
    itemBuilder.lore("&aInfo")
        .lore("&7" + description)
        .lore("&aStatus")
        .lore("&7" + getSetupInfo())
        .lore("&aControls")
        .lore("&eLEFT_CLICK \n&7-> Set the value by typing in chat")
        .lore("&eRIGHT_CLICK \n&7-> Switch between the values");
  }

  @Override
  public ItemStack getItem() {
    ItemBuilder itemBuilder = new ItemBuilder(item).removeLore();
    setLore(itemBuilder);
    return itemBuilder.colorizeItem().build();
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    switch(event.getClick()) {
      case LEFT:
        new SimpleConversationBuilder(setupInventory.getPlugin()).withPrompt(new StringPrompt() {
          @Override
          public @NotNull String getPromptText(ConversationContext context) {
            return new MessageBuilder("&ePlease type in chat one of the following words: " + switches.toString().toLowerCase() + " !").prefix().build();
          }

          @Override
          public Prompt acceptInput(ConversationContext context, String input) {
            if(!switches.contains(input)) {
              context.getForWhom().sendRawMessage(new MessageBuilder("&e✖ Only a value of the list is allowed, try again by clicking the item again").build());
              return Prompt.END_OF_CONVERSATION;
            }
            context.getForWhom().sendRawMessage(new MessageBuilder("&e✔ Completed | &aSet " + name.toUpperCase() + " " + setupInventory.getArenaKey() + " to " + input).build());
            setupInventory.setConfig(keyName, input);
            return Prompt.END_OF_CONVERSATION;
          }
        }).buildFor((Player) event.getWhoClicked());
        setupInventory.closeInventory(event.getWhoClicked());
        break;
      case RIGHT:
        String option = setupInventory.getConfig().getString("instances." + setupInventory.getArenaKey() + "." + keyName, switches.get(0));
        int position = switches.indexOf(option);
        String newOption = switches.get(switches.size() - 1 <= position ? 0 : position + 1);
        event.getWhoClicked().sendMessage(new MessageBuilder("&e✔ Completed | &aSet " + name.toUpperCase() + " " + setupInventory.getArenaKey() + " to " + newOption).build());
        setupInventory.setConfig(keyName, newOption);
        InventoryHolder holder = event.getInventory().getHolder();
        if(holder instanceof RefreshableFastInv) {
          ((RefreshableFastInv) holder).refresh();
        }
        break;
      default:
        break;
    }
    clickConsumer.accept(event);
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
