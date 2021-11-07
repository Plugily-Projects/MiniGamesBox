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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.Main;
import plugily.projects.minigamesbox.classic.api.event.player.PlugilyPlayerChooseKitEvent;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItem;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItemManager;
import plugily.projects.minigamesbox.classic.kits.basekits.Kit;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.CBPlayerInteractEvent;
import plugily.projects.minigamesbox.inventory.normal.FastInv;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class KitMenuHandler implements Listener {

  private final Main plugin;
  private final String unlockedString;
  private final String lockedString;
  private final SpecialItem kitItem;

  public KitMenuHandler(Main plugin) {
    this.plugin = plugin;
    this.kitItem = plugin.getSpecialItemManager().getSpecialItem(SpecialItemManager.SpecialItems.KIT_SELECTOR.getName());
    unlockedString = plugin.getChatManager().colorMessage(Messages.KITS_MENU_UNLOCKED_LORE);
    lockedString = plugin.getChatManager().colorMessage(Messages.KITS_MENU_LOCKED_LORE);
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public void createMenu(Player player) {
    FastInv gui = new FastInv(plugin.getBukkitHelper().serializeInt(KitRegistry.getKits().size()), plugin.getChatManager().colorMessage(Messages.KITS_OPEN_KIT_MENU));
    for(Kit kit : KitRegistry.getKits()) {
      ItemStack itemStack = kit.getItemStack();
      itemStack = new ItemBuilder(itemStack)
          .lore(kit.isUnlockedByPlayer(player) ? unlockedString : lockedString)
          .build();

      gui.addItem(itemStack, e -> {
        e.setCancelled(true);
        if(!(e.isLeftClick() || e.isRightClick()) || !(e.getWhoClicked() instanceof Player) || !ItemUtils.isItemStackNamed(e.getCurrentItem())) {
          return;
        }
        Arena arena = ArenaRegistry.getArena(player);
        if(arena == null) {
          return;
        }
        PlugilyPlayerChooseKitEvent event = new PlugilyPlayerChooseKitEvent(player, kit, arena);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
          return;
        }
        if(!kit.isUnlockedByPlayer(player)) {
          player.sendMessage(plugin.getChatManager().colorMessage(Messages.KITS_NOT_UNLOCKED_MESSAGE).replace("%KIT%", kit.getName()));
          return;
        }
        plugin.getUserManager().getUser(player).setKit(kit);
        player.sendMessage(plugin.getChatManager().colorMessage(Messages.KITS_CHOOSE_MESSAGE).replace("%KIT%", kit.getName()));
      });
    }
    gui.open(player);
  }

  @EventHandler
  public void onKitMenuItemClick(CBPlayerInteractEvent e) {
    if(!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      return;
    }

    if(!VersionUtils.getItemInHand(e.getPlayer()).equals(kitItem.getItemStack())) {
      return;
    }
    e.setCancelled(true);
    createMenu(e.getPlayer());
  }

}