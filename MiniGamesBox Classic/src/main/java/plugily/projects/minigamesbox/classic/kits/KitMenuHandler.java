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

package plugily.projects.minigamesbox.classic.kits;

import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.api.event.player.PlugilyPlayerChooseKitEvent;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.kits.basekits.Kit;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class KitMenuHandler implements Listener {

  private final PluginMain plugin;
  private final String unlockedString;
  private final String lockedString;

  public KitMenuHandler(PluginMain plugin) {
    this.plugin = plugin;
    unlockedString = new MessageBuilder("KIT_KIT_MENU_LORE_UNLOCKED").asKey().build();
    lockedString = new MessageBuilder("KIT_KIT_MENU_LORE_LOCKED").asKey().build();
  }

  public void createMenu(Player player) {
    FastInv gui = new FastInv(plugin.getBukkitHelper().serializeInt(plugin.getKitRegistry().getKits().size()), new MessageBuilder("KIT_KIT_MENU_TITLE").asKey().build());
    for(Kit kit : plugin.getKitRegistry().getKits()) {
      ItemStack itemStack = kit.getItemStack();
      itemStack = new ItemBuilder(itemStack)
          .lore(kit.isUnlockedByPlayer(player) ? unlockedString : lockedString)
          .build();

      gui.addItem(itemStack, e -> {
        e.setCancelled(true);
        if(!(e.isLeftClick() || e.isRightClick()) || !(e.getWhoClicked() instanceof Player) || !ItemUtils.isItemStackNamed(e.getCurrentItem())) {
          return;
        }
        PluginArena arena = plugin.getArenaRegistry().getArena(player);
        if(arena == null) {
          return;
        }
        PlugilyPlayerChooseKitEvent event = new PlugilyPlayerChooseKitEvent(player, kit, arena);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
          return;
        }
        if(!kit.isUnlockedByPlayer(player)) {
          new MessageBuilder("KIT_NOT_UNLOCKED").asKey().value(kit.getName()).player(player).sendPlayer();
          return;
        }
        plugin.getUserManager().getUser(player).setKit(kit);
        new MessageBuilder("KIT_CHOOSE").asKey().value(kit.getName()).player(player).sendPlayer();
      });
    }
    gui.open(player);
  }

}
