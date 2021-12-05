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
package plugily.projects.minigamesbox.classic.events.spectator;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import plugily.projects.commonsbox.number.NumberUtils;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.Arena;
import plugily.projects.minigamesbox.classic.utils.helper.ItemUtils;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.CBPlayerInteractEvent;
import plugily.projects.minigamesbox.inventory.normal.FastInv;

import java.util.Collections;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
public class SpectatorItemEvents implements Listener {

  private final PluginMain plugin;
  private final SpectatorSettingsMenu spectatorSettingsMenu;

  public SpectatorItemEvents(PluginMain plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    spectatorSettingsMenu = new SpectatorSettingsMenu(plugin, plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SETTINGS_MENU_INVENTORY_NAME"),
        plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SETTINGS_MENU_SPEED_NAME"));
  }

  @EventHandler
  public void onSpecialItem(CBPlayerInteractEvent e) {
    if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) {
      return;
    }
    Arena arena = plugin.getArenaRegistry().getArena(e.getPlayer());
    ItemStack stack = VersionUtils.getItemInHand(e.getPlayer());
    if(arena == null || !ItemUtils.isItemStackNamed(stack)) {
      return;
    }
    ItemStack key = plugin.getSpecialItemManager().getRelatedSpecialItem(stack).getItemStack();
    if(key == null) {
      return;
    }
    if(key == plugin.getSpecialItemManager().getSpecialItem("PLAYERS_LIST").getItemStack()) {
      e.setCancelled(true);
      openSpectatorMenu(e.getPlayer(), arena);
    } else if(key == plugin.getSpecialItemManager().getSpecialItem("SPECTATOR_OPTIONS").getItemStack()) {
      e.setCancelled(true);
      spectatorSettingsMenu.openSpectatorSettingsMenu(e.getPlayer());
    }
  }

  private void openSpectatorMenu(Player player, Arena arena) {
    FastInv gui = new FastInv(plugin.getBukkitHelper().serializeInt(arena.getPlayers().size()), plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_MENU_NAME"));

    ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();

    for(Player arenaPlayer : arena.getPlayers()) {
      if(plugin.getUserManager().getUser(arenaPlayer).isSpectator()) {
        continue;
      }
      ItemStack cloneSkull = skull.clone();
      SkullMeta meta = VersionUtils.setPlayerHead(arenaPlayer, (SkullMeta) cloneSkull.getItemMeta());
      ComplementAccessor.getComplement().setDisplayName(meta, arenaPlayer.getName());
      ComplementAccessor.getComplement().setLore(meta, Collections.singletonList(plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_TARGET_PLAYER_HEALTH")
          .replace("%health%", Double.toString(NumberUtils.round(arenaPlayer.getHealth(), 2)))));
      cloneSkull.setItemMeta(meta);
      gui.addItem(cloneSkull, e -> {
        e.getWhoClicked().sendMessage(plugin.getChatManager().formatMessage(arena, plugin.getChatManager().colorMessage("IN_GAME_SPECTATOR_SPECTATOR_TELEPORT"), arenaPlayer));
        e.getWhoClicked().closeInventory();
        e.getWhoClicked().teleport(arenaPlayer);
      });
    }
    gui.open(player);
  }
}
