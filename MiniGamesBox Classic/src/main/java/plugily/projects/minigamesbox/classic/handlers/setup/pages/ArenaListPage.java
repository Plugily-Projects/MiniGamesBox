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
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
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
public class ArenaListPage extends NormalFastInv implements SetupPage {

  private final PluginSetupInventory setupInventory;

  public ArenaListPage(int size, String title, PluginSetupInventory pluginSetupInventory) {
    super(size, title);
    this.setupInventory = pluginSetupInventory;
    prepare();
  }

  @Override
  public void prepare() {
    injectItems();
    setDefaultItem(ClickableItem.of(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()));
    setForceRefresh(true);
    refresh();
  }

  @Override
  public void injectItems() {
    setItem(45, ClickableItem.of(new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE.parseMaterial()).name("Go to Setup Menu").build(), event -> setupInventory.open(SetupUtilities.InventoryStage.SETUP_GUI)));

    for(PluginArena arena : setupInventory.getPlugin().getArenaRegistry().getArenas()) {

      Material material = XMaterial.GREEN_WOOL.parseMaterial();
      if(!arena.isReady()) {
        material = XMaterial.RED_WOOL.parseMaterial();
      }

      addItem(ClickableItem.of(new ItemBuilder(material)
          .name(arena.getId() + " | " + arena.getMapName())
          .lore("&aLeft-Click to edit")
          .lore("&cRight-Click to delete")
          .lore("&eShift-Left to clone")
          .colorizeItem()
          .build(), event -> {
        switch(event.getClick()) {
          case LEFT:
            setupInventory.setArena(arena);
            setupInventory.open(SetupUtilities.InventoryStage.PAGED_GUI);
            break;
          case RIGHT:
            new SimpleConversationBuilder(setupInventory.getPlugin()).withPrompt(new StringPrompt() {
              @Override
              public @NotNull String getPromptText(ConversationContext context) {
                setupInventory.getPlayer().closeInventory();
                return new MessageBuilder("&ePlease type in chat 'delete' ! &cType in 'CANCEL' to cancel!").prefix().build();
              }

              @Override
              public Prompt acceptInput(ConversationContext context, String input) {
                if(!input.equalsIgnoreCase("delete")) {
                  setupInventory.getPlayer().sendRawMessage(new MessageBuilder("&cDelete operation canceled").prefix().build());
                  return Prompt.END_OF_CONVERSATION;
                }
                setupInventory.getPlugin().getArenaManager().stopGame(false, arena);
                FileConfiguration config = ConfigUtils.getConfig(setupInventory.getPlugin(), "arenas");
                config.set("instances." + arena.getId(), null);
                ConfigUtils.saveConfig(setupInventory.getPlugin(), config, "arenas");
                setupInventory.getPlugin().getArenaRegistry().unregisterArena(arena);
                setupInventory.getPlayer().sendRawMessage(new MessageBuilder("COMMANDS_REMOVED_GAME_INSTANCE").asKey().build());
                setupInventory.open(SetupUtilities.InventoryStage.ARENA_LIST);
                return Prompt.END_OF_CONVERSATION;
              }
            }).buildFor((Player) event.getWhoClicked());
            break;
          case SHIFT_LEFT:
            new MessageBuilder("&cThis function isn't ready yet!").prefix().send(event.getWhoClicked());
            break;
          default:
            break;
        }
      }));

    }
  }

}
