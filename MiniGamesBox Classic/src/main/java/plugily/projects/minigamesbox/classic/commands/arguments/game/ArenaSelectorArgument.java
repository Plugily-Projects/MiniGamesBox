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
package plugily.projects.minigamesbox.classic.commands.arguments.game;

import com.cryptomorin.xseries.XMaterial;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import plugily.projects.minigamesbox.classic.arena.Arena;
import plugily.projects.minigamesbox.classic.arena.ArenaManager;
import plugily.projects.minigamesbox.classic.commands.arguments.ArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.ChatManager;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class ArenaSelectorArgument implements Listener {

  private final ChatManager chatManager;
  private final Map<Integer, Arena> arenas = new HashMap<>();

  public ArenaSelectorArgument(ArgumentsRegistry registry) {
    this.chatManager = registry.getPlugin().getChatManager();

    registry.getPlugin().getServer().getPluginManager().registerEvents(this, registry.getPlugin());
    registry.mapArgument(registry.getPlugin().getPluginNamePrefixLong(), new LabeledCommandArgument("arenas", registry.getPlugin().getPluginNamePrefixLong() + ".arenas", CommandArgument.ExecutorType.PLAYER,
        new LabelData(registry.getPlugin().getPluginNamePrefix() + " arenas", registry.getPlugin().getPluginNamePrefix() + " arenas", "&7Select an arena\n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".arenas")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(registry.getPlugin().getArenaRegistry().getArenas().size() == 0) {
          player.sendMessage(chatManager.colorMessage(Messages.COMMANDS_ADMIN_LIST_NO_ARENAS));
          return;
        }
        int slot = 0;
        arenas.clear();

        Inventory inventory = ComplementAccessor.getComplement().createInventory(player, registry.getPlugin().getBukkitHelper().serializeInt(registry.getPlugin().getArenaRegistry().getArenas().size()), chatManager.colorMessage(Messages.ARENA_SELECTOR_INV_TITLE));

        for(Arena arena : registry.getPlugin().getArenaRegistry().getArenas()) {
          arenas.put(slot, arena);
          ItemStack itemStack = XMaterial.matchXMaterial(registry.getPlugin().getConfig().getString("Arena-Selector.State-Item." + arena.getArenaState().getFormattedName(), "YELLOW_WOOL").toUpperCase()).orElse(XMaterial.YELLOW_WOOL).parseItem();

          if(itemStack == null)
            continue;

          ItemMeta itemMeta = itemStack.getItemMeta();
          if(itemMeta != null) {
            ComplementAccessor.getComplement().setDisplayName(itemMeta, formatItem(LanguageManager.getLanguageMessage("Arena-Selector.Item.Name"), arena));

            java.util.List<String> lore = LanguageManager.getLanguageList(Messages.ARENA_SELECTOR_ITEM_LORE.getAccessor());
            for(int e = 0; e < lore.size(); e++) {
              lore.set(e, formatItem(lore.get(e), arena));
            }

            ComplementAccessor.getComplement().setLore(itemMeta, lore);
            itemStack.setItemMeta(itemMeta);
          }
          inventory.addItem(itemStack);
          slot++;
        }
        player.openInventory(inventory);
      }
    });

  }

  private String formatItem(String string, Arena arena) {
    String formatted = string;
    formatted = StringUtils.replace(formatted, "%mapname%", arena.getMapName());
    int maxPlayers = arena.getMaximumPlayers();
    if(arena.getPlayers().size() >= maxPlayers) {
      formatted = StringUtils.replace(formatted, "%state%", chatManager.colorMessage(Messages.SIGNS_GAME_STATES_FULL_GAME));
    } else {
      formatted = StringUtils.replace(formatted, "%state%", arena.getArenaState().getPlaceholder());
    }
    formatted = StringUtils.replace(formatted, "%playersize%", Integer.toString(arena.getPlayers().size()));
    formatted = StringUtils.replace(formatted, "%maxplayers%", Integer.toString(maxPlayers));
    formatted = chatManager.colorRawMessage(formatted);
    return formatted;
  }

  @EventHandler
  public void onArenaSelectorMenuClick(InventoryClickEvent e) {
    if(!ComplementAccessor.getComplement().getTitle(e.getView()).equals(chatManager.colorMessage(Messages.ARENA_SELECTOR_INV_TITLE))) {
      return;
    }

    ItemStack current = e.getCurrentItem();
    if(current == null || !current.hasItemMeta()) {
      return;
    }

    Player player = (Player) e.getWhoClicked();
    player.closeInventory();

    Arena arena = arenas.get(e.getRawSlot());
    if(arena != null) {
      ArenaManager.joinAttempt(player, arena);
    } else {
      player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage(Messages.COMMANDS_NO_ARENA_LIKE_THAT));
    }
  }

}