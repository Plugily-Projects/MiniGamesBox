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

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class KitAbilityHandler implements Listener {

  private final PluginMain plugin;
  private final List<Material> armorTypes = new ArrayList<>();

  public KitAbilityHandler(PluginMain plugin) {
    this.plugin = plugin;
    if(!plugin.getConfigPreferences().getOption("KITS")) {
      return;
    }
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    setupArmorTypes();
  }

  private void setupArmorTypes() {
    Stream.of(Material.LEATHER_BOOTS, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_HELMET,
            XMaterial.GOLDEN_BOOTS.parseMaterial(), XMaterial.GOLDEN_CHESTPLATE.parseMaterial(), XMaterial.GOLDEN_LEGGINGS.parseMaterial(),
            XMaterial.GOLDEN_HELMET.parseMaterial(), Material.DIAMOND_BOOTS, Material.DIAMOND_LEGGINGS, Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_HELMET, Material.IRON_CHESTPLATE, Material.IRON_BOOTS, Material.IRON_HELMET, Material.IRON_LEGGINGS,
            Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_HELMET)
        .forEach(armorTypes::add);
  }

  @EventHandler
  public void onArmor(InventoryClickEvent event) {
    if(!(event.getWhoClicked() instanceof Player)) {
      return;
    }
    User user = plugin.getUserManager().getUser((Player) event.getWhoClicked());
    if(!plugin.getArenaRegistry().isInArena((Player) event.getWhoClicked())) {
      return;
    }
    if(!user.getKit().hasKitAction(KitAbility.NO_ARMOUR)) {
      return;
    }
    if(!(event.getInventory().getType().equals(InventoryType.PLAYER) || event.getInventory().getType().equals(InventoryType.CRAFTING))) {
      return;
    }
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      for(ItemStack stack : event.getWhoClicked().getInventory().getArmorContents()) {
        if(stack == null || !armorTypes.contains(stack.getType())) {
          continue;
        }
        //we cannot cancel event using scheduler, we must remove all armor contents from inventory manually
        new MessageBuilder("KIT_CANNOT_WEAR_ARMOR").asKey().send(user.getPlayer());
        event.getWhoClicked().getInventory().setHelmet(new ItemStack(Material.AIR, 1));
        event.getWhoClicked().getInventory().setChestplate(new ItemStack(Material.AIR, 1));
        event.getWhoClicked().getInventory().setLeggings(new ItemStack(Material.AIR, 1));
        event.getWhoClicked().getInventory().setBoots(new ItemStack(Material.AIR, 1));
        return;
      }
    }, 1);
  }

  @EventHandler
  public void onArmorClick(PlugilyPlayerInteractEvent event) {
    if(!plugin.getArenaRegistry().isInArena(event.getPlayer())) {
      return;
    }
    if(!plugin.getUserManager().getUser(event.getPlayer()).getKit().hasKitAction(KitAbility.NO_ARMOUR) || !event.hasItem()) {
      return;
    }
    if(armorTypes.contains(event.getItem().getType())) {
      event.setCancelled(true);
      new MessageBuilder("KIT_CANNOT_WEAR_ARMOR").asKey().player(event.getPlayer()).sendPlayer();
    }
  }

}
