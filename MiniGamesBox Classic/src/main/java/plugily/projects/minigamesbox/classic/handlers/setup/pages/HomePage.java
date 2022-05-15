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
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.PluginSetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupUtilities;
import plugily.projects.minigamesbox.classic.handlers.setup.items.EmptyItem;
import plugily.projects.minigamesbox.classic.utils.conversation.SimpleConversationBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 04.01.2022
 */
public class HomePage extends NormalFastInv implements SetupPage {

  private final PluginSetupInventory setupInventory;

  public HomePage(int size, String title, PluginSetupInventory pluginSetupInventory) {
    super(size, title);
    this.setupInventory = pluginSetupInventory;
    prepare();
  }

  @Override
  public void prepare() {
    injectItems();
    setForceRefresh(true);
    refresh();
  }

  @Override
  public void injectItems() {
    setItem(19, ClickableItem.of(new ItemBuilder(XMaterial.REDSTONE_BLOCK.parseItem())
        .name(new MessageBuilder("&cArenas List").build())
        .lore(ChatColor.GRAY + "Edit, delete or copy arenas")
        .build(), event -> setupInventory.open(SetupUtilities.InventoryStage.ARENA_LIST)
    ));

    setItem(22, ClickableItem.of(new ItemBuilder(XMaterial.OAK_SIGN.parseItem())
            .name(new MessageBuilder("&cCreate Arena").build())
            .lore(ChatColor.GRAY + "Create a fully new arena")
            .build(), event -> {
          event.getWhoClicked().closeInventory();
          new SimpleConversationBuilder(setupInventory.getPlugin()).withPrompt(new StringPrompt() {
            @Override
            public @NotNull String getPromptText(ConversationContext context) {
              return new MessageBuilder("&ePlease type in chat arena name to create new arena! You can use color codes. &cType in 'CANCEL' to cancel!").prefix().build();
            }

            @Override
            public Prompt acceptInput(ConversationContext context, String input) {
              String name = new MessageBuilder(input).build();
              PluginArena arena = setupInventory.getPlugin().getSetupUtilities().createInstanceInConfig(name, event.getWhoClicked().getWorld().getName(), setupInventory.getPlayer());
              if(arena == null) {
                return Prompt.END_OF_CONVERSATION;
              }
              setupInventory.setArena(setupInventory.getPlayer(), arena);
              setupInventory.open(SetupUtilities.InventoryStage.PAGED_LOCATIONS);
              return Prompt.END_OF_CONVERSATION;
            }
          }).buildFor((Player) event.getWhoClicked());
        }
    ));


    setItem(25, ClickableItem.of(new ItemBuilder(XMaterial.SLIME_BLOCK.parseItem())
            .name(new MessageBuilder("&cContinue Arena Setup").build())
            .lore(ChatColor.GRAY + "Continue a previous started arena editor")
            .build(), event -> {
          if(setupInventory.getArena() == null) {
            new MessageBuilder("You need to create or edit a arena first").prefix().player((Player) event.getWhoClicked()).sendPlayer();
            return;
          }
          setupInventory.open(SetupUtilities.InventoryStage.PAGED_GUI);
        }
    ));


    setItem(39, ClickableItem.of(new ItemBuilder(XMaterial.GOLD_INGOT.parseItem())
        .name(new MessageBuilder("&6&l► Patreon Addon ◄ &8(AD)").build())
        .lore(ChatColor.GRAY + "Enhance gameplay with paid addon!")
        .lore(ChatColor.GOLD + "Selection of features of the addon:")
        .lore(ChatColor.GOLD + "Custom Kits, Achievements, Replay Ability")
        .lore(ChatColor.GRAY + "Click to get link for patron program!")
        .build(), event -> {
      event.getWhoClicked().closeInventory();
      new MessageBuilder(" &6Check patron program here: https://wiki.plugily.xyz/" + setupInventory.getPlugin().getPluginNamePrefixLong().toLowerCase() + "/addon/overview", false).prefix().send(event.getWhoClicked());
    }));

    setItem(41, ClickableItem.of(new ItemBuilder(XMaterial.FILLED_MAP.parseItem())
        .name(new MessageBuilder("&e&lView Setup Video").build())
        .lore(ChatColor.GRAY + "Having problems with setup or wanna")
        .lore(ChatColor.GRAY + "know some useful tips? Click to get video link!")
        .build(), event -> {
      event.getWhoClicked().closeInventory();
      new MessageBuilder(" &6Check out this video: " + setupInventory.getPlugin().getSetupUtilities().VIDEO_LINK + SetupUtilities.InventoryStage.SETUP_GUI.getTutorial(), false).prefix().send(event.getWhoClicked());
    }));

    setDefaultItem(new EmptyItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()));
  }

}
