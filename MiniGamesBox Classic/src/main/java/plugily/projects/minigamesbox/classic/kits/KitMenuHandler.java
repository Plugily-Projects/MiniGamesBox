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

package plugily.projects.minigamesbox.classic.kits;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.api.events.player.PlugilyPlayerChooseKitEvent;
import plugily.projects.minigamesbox.api.kit.IKit;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.inventory.common.item.SimpleClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class KitMenuHandler implements Listener {

  private final PluginMain plugin;
  private String unlockedString = "";
  private String lockedString = "";

  public KitMenuHandler(PluginMain plugin) {
    this.plugin = plugin;
    if(!plugin.getConfigPreferences().getOption("KITS")) {
      return;
    }
    unlockedString = new MessageBuilder("KIT_KIT_MENU_LORE_UNLOCKED").asKey().build();
    lockedString = new MessageBuilder("KIT_KIT_MENU_LORE_LOCKED").asKey().build();
  }

  public void createMenu(Player player) {
    if(!plugin.getConfigPreferences().getOption("KITS")) {
      plugin.getDebugger().debug("Kits are disabled, can not create menu");
      return;
    }
    NormalFastInv gui = new NormalFastInv(plugin.getBukkitHelper().serializeInt(plugin.getKitRegistry().getKits().size()), new MessageBuilder("KIT_KIT_MENU_TITLE").asKey().build());
    for(IKit kit : plugin.getKitRegistry().getKits()) {
      ItemStack itemStack = new ItemStack(kit.getItemStack());
      itemStack = new ItemBuilder(itemStack)
          .name(kit.getName())
          .lore(kit.getDescription())
          .lore("")
          .lore(kit.isUnlockedByPlayer(player) ? unlockedString : lockedString)
          .colorizeItem()
          .build();

      gui.addItem(new SimpleClickableItem(itemStack, event -> {
        event.setCancelled(true);
        if(!(event.isLeftClick() || event.isRightClick()) || !(event.getWhoClicked() instanceof Player)) {
          return;
        }
        IPluginArena arena = plugin.getArenaRegistry().getArena(player);
        if(arena == null) {
          return;
        }
        PlugilyPlayerChooseKitEvent chooseKitEvent = new PlugilyPlayerChooseKitEvent(player, kit, arena);
        Bukkit.getPluginManager().callEvent(chooseKitEvent);
        if(chooseKitEvent.isCancelled()) {
          return;
        }
        if(!kit.isUnlockedByPlayer(player)) {
          new MessageBuilder("KIT_NOT_UNLOCKED").asKey().value(kit.getName()).player(player).sendPlayer();
          return;
        }
        plugin.getUserManager().getUser(player).setKit(kit);
        new MessageBuilder("KIT_CHOOSE").asKey().value(kit.getName()).player(player).sendPlayer();
      }));
    }
    gui.refresh();
    gui.open(player);
  }

}
