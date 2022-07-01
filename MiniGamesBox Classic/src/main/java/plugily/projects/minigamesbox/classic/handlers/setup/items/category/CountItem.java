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
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.conversation.SimpleConversationBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.common.RefreshableFastInv;

import java.util.function.Consumer;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 29.12.2021
 */
public class CountItem implements CategoryItemHandler {
  private final SetupInventory setupInventory;
  private final ItemStack item;
  private int count;
  private final String name;
  private final String description;
  private final String keyName;
  private final Consumer<InventoryClickEvent> clickConsumer;

  /**
   * Constructor
   *
   * @param setupInventory
   * @param item           the display item
   * @param count          the count the item should have
   * @param name
   * @param description
   * @param keyName
   * @param clickConsumer  the consumer to be called when the item is clicked
   */
  public CountItem(SetupInventory setupInventory, ItemBuilder item, int count, String name, String description, String keyName, Consumer<InventoryClickEvent> clickConsumer) {
    this.setupInventory = setupInventory;
    this.name = name;
    this.description = description;
    this.keyName = keyName;
    item
        .name("&7Set &a" + this.name.toUpperCase() + " &7amount")
        .lore("&aInfo")
        .lore("&7" + this.description)
        .lore("&aStatus:")
        .lore("&7" + getSetupInfo())
        .lore("&aControls")
        .lore("&eLEFT_CLICK &7- Increase the amount")
        .lore("&eSHIFT_LEFT_CLICK &7- Input number on chat")
        .lore("&eRIGHT_CLICK &7- Decrease the amount")
        .colorizeItem();
    this.item = item.build();
    this.clickConsumer = clickConsumer;
    this.count = count >= 0 ? count : 1;
  }

  /**
   * Constructor
   *
   * @param setupInventory
   * @param item           the display item
   * @param clickConsumer  the consumer to be called when the item is clicked
   * @param name
   * @param description
   * @param keyName
   */
  public CountItem(SetupInventory setupInventory, ItemBuilder item, Consumer<InventoryClickEvent> clickConsumer, String name, String description, String keyName) {
    this(setupInventory, item, item.build().getAmount(), name, description, keyName, clickConsumer);
  }

  public CountItem(SetupInventory setupInventory, ItemBuilder item, String name, String description, String keyName) {
    this(setupInventory, item, setupInventory.getMinimumValue(keyName), name, description, keyName, emptyConsumer -> {
    });
  }


  @Override
  public ItemStack getItem() {
    item.setAmount(Math.min(count, 64));
    return item;
  }


  @Override
  public void onClick(InventoryClickEvent event) {
    switch(event.getClick()) {
      case LEFT:
        count++;
        break;
      case SHIFT_LEFT:
        event.getWhoClicked().closeInventory();
        new SimpleConversationBuilder(setupInventory.getPlugin()).withPrompt(new NumericPrompt() {
          @Override
          protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull Number number) {
            int count = number.intValue();
            updateArenaFile(count);
            conversationContext.getForWhom().sendRawMessage(new MessageBuilder("&e✔ Completed | &aCount for " + name.toUpperCase() + " on " + setupInventory.getArenaKey() + " set to " + count).build());
            //considerable to open setup inventory again?
            return Prompt.END_OF_CONVERSATION;
          }

          @Override
          public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
            return new MessageBuilder("&ePlease type in chat count of " + name.toUpperCase() + "! Only integers are allowed!").prefix().build();
          }
        }).buildFor((Player) event.getWhoClicked());
        break;
      case RIGHT:
        count--;
        break;
      default:
        break;
    }
    if(count < 1) {
      event.getWhoClicked().sendMessage("§c§l✖ §cWarning | Please do not set amount lower than 1! For higher values set the number easily with chat!");
      count = 1;
    }
    updateCount(event);
    clickConsumer.accept(event);
    InventoryHolder holder = event.getInventory().getHolder();
    if(holder instanceof RefreshableFastInv) {
      ((RefreshableFastInv) holder).refresh();
    }
  }

  private void updateCount(InventoryClickEvent event) {
    ItemStack currentItem = event.getCurrentItem();
    if(currentItem != null) {
      updateArenaFile(currentItem.getAmount());
    }
  }

  private void updateArenaFile(int amount) {
    setupInventory.getConfig().set("instances." + setupInventory.getArenaKey() + "." + keyName, amount);
    ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getConfig(), "arenas");
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