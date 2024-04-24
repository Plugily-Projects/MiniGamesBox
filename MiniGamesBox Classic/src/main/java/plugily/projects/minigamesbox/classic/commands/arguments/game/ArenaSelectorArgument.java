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
package plugily.projects.minigamesbox.classic.commands.arguments.game;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.inventory.common.item.SimpleClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class ArenaSelectorArgument {

  public ArenaSelectorArgument(PluginArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getPluginNamePrefixLong(), new LabeledCommandArgument("arenas", registry.getPlugin().getPluginNamePrefixLong() + ".arenas", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/" + registry.getPlugin().getPluginNamePrefix() + " arenas", "/" + registry.getPlugin().getPluginNamePrefix() + " arenas", "&7Overview over all arenas in one GUI. Select one to join\n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".arenas")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(registry.getPlugin().getArenaRegistry().getArenas().isEmpty()) {
          new MessageBuilder("COMMANDS_ADMIN_LIST_NO_ARENAS").asKey().player(player).sendPlayer();
          return;
        }

        NormalFastInv arenaSelector = new NormalFastInv(registry.getPlugin().getArenaRegistry().getArenas().size(), new MessageBuilder("ARENA_SELECTOR_INVENTORY_TITLE").asKey().build());

        for(IPluginArena arena : registry.getPlugin().getArenaRegistry().getArenas()) {
          ItemStack itemStack = XMaterial.matchXMaterial(registry.getPlugin().getConfig().getString("Arena-Selector.State-Item." + arena.getArenaState().getFormattedName(), "YELLOW_WOOL").toUpperCase()).orElse(XMaterial.YELLOW_WOOL).parseItem();
          if(itemStack == null) {
            continue;
          }
          ItemMeta itemMeta = itemStack.getItemMeta();
          if(itemMeta == null) {
            continue;
          }
          ComplementAccessor.getComplement().setDisplayName(itemMeta, new MessageBuilder("ARENA_SELECTOR_ITEM_NAME").asKey().arena(arena).player(player).build());
          List<String> lore = new ArrayList<>();
          for(String description : registry.getPlugin().getLanguageManager().getLanguageList("Arena-Selector.Item.Lore")) {
            lore.add(new MessageBuilder(description).arena(arena).player(player).build());
          }
          ComplementAccessor.getComplement().setLore(itemMeta, lore);
          itemStack.setItemMeta(itemMeta);

          arenaSelector.addItem(new SimpleClickableItem(itemStack, event -> {
            registry.getPlugin().getArenaManager().joinAttempt(player, arena);
            player.closeInventory();
          }));
        }
        arenaSelector.open(player);
      }
    });
  }

}
