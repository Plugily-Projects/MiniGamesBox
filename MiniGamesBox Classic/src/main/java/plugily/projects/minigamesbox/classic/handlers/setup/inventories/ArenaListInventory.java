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

package plugily.projects.minigamesbox.classic.handlers.setup.inventories;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventoryUtils;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.conversation.SimpleConversationBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.06.2022
 */
public class ArenaListInventory extends NormalFastInv implements InventoryHandler {

  private final SetupInventory setupInventory;

  public ArenaListInventory(int size, String title, SetupInventory setupInventory) {
    super(size, title);
    this.setupInventory = setupInventory;
    prepare();
  }

  @Override
  public void prepare() {
    injectItems();
    setDefaultItem(ClickableItem.of(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()).name(" ").build()));
    setForceRefresh(true);
    refresh();
  }

  @Override
  public void injectItems() {
    setItem(45, ClickableItem.of(new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE.parseItem()).name("&cGo to Setup Menu").colorizeItem().build(), event -> setupInventory.open(SetupInventoryUtils.SetupInventoryStage.HOME)));

    for(IPluginArena arena : setupInventory.getPlugin().getArenaRegistry().getArenas()) {

      ItemStack material = XMaterial.GREEN_WOOL.parseItem();
      if(!arena.isReady()) {
        material = XMaterial.RED_WOOL.parseItem();
      }

      addItem(ClickableItem.of(new ItemBuilder(material)
          .name("ID | " + arena.getId())
          .lore("&aControls")
          .lore("&eLEFT_CLICK \n&7-> Edit arena")
          .lore("&eSHIFT_LEFT_CLICK \n&7-> Clone arena")
          .lore("&eSHIFT_RIGHT_CLICK \n&7-> Delete arena")
          .colorizeItem()
          .build(), event -> {
        event.setCancelled(true);
        switch(event.getClick()) {
          case LEFT:
            setupInventory.setArenaKey(arena.getId());
            setupInventory.open(SetupInventoryUtils.SetupInventoryStage.ARENA_EDITOR);
            break;
          case SHIFT_RIGHT:
            setupInventory.closeInventory(event.getWhoClicked());
            new SimpleConversationBuilder(setupInventory.getPlugin()).withPrompt(new StringPrompt() {
              @Override
              public @NotNull String getPromptText(ConversationContext context) {
                return new MessageBuilder("&ePlease type in chat 'delete' ! &cType in 'CANCEL' to cancel!").prefix().build();
              }

              @Override
              public Prompt acceptInput(ConversationContext context, String input) {
                if(!input.equalsIgnoreCase("delete")) {
                  context.getForWhom().sendRawMessage(new MessageBuilder("&cDelete operation canceled").prefix().build());
                  return Prompt.END_OF_CONVERSATION;
                }
                setupInventory.getPlugin().getArenaRegistry().unregisterArena(arena);

                FileConfiguration config = ConfigUtils.getConfig(setupInventory.getPlugin(), "arenas");
                config.set("instances." + arena.getId(), null);
                ConfigUtils.saveConfig(setupInventory.getPlugin(), config, "arenas");

                context.getForWhom().sendRawMessage(new MessageBuilder("COMMANDS_REMOVED_GAME_INSTANCE").asKey().build());
                setupInventory.open(SetupInventoryUtils.SetupInventoryStage.ARENA_LIST);
                return Prompt.END_OF_CONVERSATION;
              }
            }).buildFor((Player) event.getWhoClicked());
            break;
          case SHIFT_LEFT:
            setupInventory.closeInventory(event.getWhoClicked());
            new SimpleConversationBuilder(setupInventory.getPlugin()).withPrompt(new StringPrompt() {
              @Override
              public @NotNull String getPromptText(ConversationContext context) {
                return new MessageBuilder("&ePlease type in chat new arena name ! &cType in 'CANCEL' to cancel!").prefix().build();
              }

              @Override
              public Prompt acceptInput(ConversationContext context, String input) {
                String name = new MessageBuilder(input, false).build();
                if(name.contains(" ")) {
                  context.getForWhom().sendRawMessage(new MessageBuilder("&cThe arena key needs to be without spaces. You can give it a nice map name later ;)").prefix().build());
                  return Prompt.END_OF_CONVERSATION;
                }
                setupInventory.createInstanceInConfig(name, (Player) context.getForWhom());
                if(setupInventory.getPlugin().getArenaRegistry().getArena(name) == null) {
                  return Prompt.END_OF_CONVERSATION;
                }
                FileConfiguration config = ConfigUtils.getConfig(setupInventory.getPlugin(), "arenas");
                config.set("instances." + name, config.getConfigurationSection("instances." + arena.getId()));
                setupInventory.setArenaKey(name);
                ConfigUtils.saveConfig(setupInventory.getPlugin(), config, "arenas");
                setupInventory.open(SetupInventoryUtils.SetupInventoryStage.ARENA_EDITOR);
                return Prompt.END_OF_CONVERSATION;
              }
            }).buildFor((Player) event.getWhoClicked());
            break;
          default:
            break;
        }
      }));

    }
  }
}
