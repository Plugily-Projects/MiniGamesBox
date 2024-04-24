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

package plugily.projects.minigamesbox.classic.kits.ability;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;

public class KitAbilityHandler implements Listener {

  private final PluginMain plugin;

  public KitAbilityHandler(PluginMain plugin) {
    this.plugin = plugin;
    if(!plugin.getConfigPreferences().getOption("KITS")) {
      return;
    }
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onKitInventoryClick(InventoryClickEvent event) {
    if(!(event.getWhoClicked() instanceof Player)) {
      return;
    }
    IUser user = plugin.getUserManager().getUser((Player) event.getWhoClicked());
    if(!plugin.getArenaRegistry().isInArena((Player) event.getWhoClicked())) {
      return;
    }
    for(KitAbility kitAbility : plugin.getKitAbilityManager().getKitAbilities().values()) {
      if(user.getKit().hasAbility(kitAbility)) {
        kitAbility.getClickConsumer().accept(event);
      }
    }
  }

  @EventHandler
  public void onKitInteractClick(PlugilyPlayerInteractEvent event) {
    if(!plugin.getArenaRegistry().isInArena(event.getPlayer())) {
      return;
    }
    if(!event.hasItem()) {
      return;
    }
    for(KitAbility kitAbility : plugin.getKitAbilityManager().getKitAbilities().values()) {
      if(plugin.getUserManager().getUser(event.getPlayer()).getKit().hasAbility(kitAbility)) {
        kitAbility.getInteractConsumer().accept(event);
      }
    }

  }

}
