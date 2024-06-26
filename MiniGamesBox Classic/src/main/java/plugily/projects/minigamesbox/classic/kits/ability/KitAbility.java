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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.minigamesbox.api.kit.ability.IKitAbility;
import plugily.projects.minigamesbox.api.user.IUser;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ArmorHelper;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class KitAbility implements IKitAbility {
  private static final PluginMain plugin = JavaPlugin.getPlugin(PluginMain.class);
  private static final Map<String, KitAbility> kitAbilities = new HashMap<>();

  static {
    kitAbilities.put("NO_ARMOUR", new KitAbility("NO_ARMOUR", inventoryClickEvent -> {
      if(!(inventoryClickEvent.getInventory().getType().equals(InventoryType.PLAYER) || inventoryClickEvent.getInventory().getType().equals(InventoryType.CRAFTING))) {
        return;
      }
      IUser user = plugin.getUserManager().getUser((Player) inventoryClickEvent.getWhoClicked());
      Bukkit.getScheduler().runTaskLater(plugin, () -> {
        for(ItemStack stack : inventoryClickEvent.getWhoClicked().getInventory().getArmorContents()) {
          if(stack == null || !ArmorHelper.getArmorTypes().contains(stack.getType())) {
            continue;
          }
          //we cannot cancel event using scheduler, we must remove all armor contents from inventory manually
          new MessageBuilder("KIT_CANNOT_WEAR_ARMOR").asKey().send(user.getPlayer());
          inventoryClickEvent.getWhoClicked().getInventory().setHelmet(new ItemStack(Material.AIR, 1));
          inventoryClickEvent.getWhoClicked().getInventory().setChestplate(new ItemStack(Material.AIR, 1));
          inventoryClickEvent.getWhoClicked().getInventory().setLeggings(new ItemStack(Material.AIR, 1));
          inventoryClickEvent.getWhoClicked().getInventory().setBoots(new ItemStack(Material.AIR, 1));
          return;
        }
      }, 1);
    }, playerInteractHandler -> {
      if(ArmorHelper.getArmorTypes().contains(playerInteractHandler.getItem().getType())) {
        playerInteractHandler.setCancelled(true);
        new MessageBuilder("KIT_CANNOT_WEAR_ARMOR").asKey().player(playerInteractHandler.getPlayer()).sendPlayer();
      }
    }));
  }

  private final String name;
  private final Consumer<InventoryClickEvent> clickConsumer;
  private final Consumer<PlugilyPlayerInteractEvent> interactConsumer;

  public KitAbility(String name, Consumer<InventoryClickEvent> inventoryClickHandler, Consumer<PlugilyPlayerInteractEvent> playerInteractHandler) {
    this.name = name;
    this.clickConsumer = inventoryClickHandler;
    this.interactConsumer = playerInteractHandler;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Consumer<InventoryClickEvent> getClickConsumer() {
    return clickConsumer;
  }

  public Consumer<PlugilyPlayerInteractEvent> getInteractConsumer() {
    return interactConsumer;
  }

  public static Map<String, KitAbility> getKitAbilities() {
    return Collections.unmodifiableMap(kitAbilities);
  }
}
