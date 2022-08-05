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

package plugily.projects.minigamesbox.classic.handlers.setup.items.category;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.utils.dimensional.CuboidSelector;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.inventory.common.RefreshableFastInv;

import java.util.function.Consumer;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 31.12.2021
 */
public class LocationSelectorItem implements CategoryItemHandler {

  private final SetupInventory setupInventory;
  private final ItemStack item;

  private final String name;
  private final String description;
  private final String keyName;
  private final Consumer<InventoryClickEvent> clickConsumer;
  private final Consumer<PlugilyPlayerInteractEvent> interactConsumer;

  public LocationSelectorItem(SetupInventory setupInventory, ItemBuilder item, String name, String description, String keyName) {
    this(setupInventory, item, name, description, keyName, emptyConsumer -> {
    }, emptyConsumer -> {
    });
  }

  public LocationSelectorItem(SetupInventory setupInventory, ItemBuilder item, String name, String description, String keyName, Consumer<InventoryClickEvent> clickConsumer, Consumer<PlugilyPlayerInteractEvent> interactConsumer) {
    this.setupInventory = setupInventory;
    this.name = name;
    this.description = description;
    this.keyName = keyName;
    item
        .name("&7Set &a" + name.toUpperCase() + " &7location selection")
        .lore("&aInfo")
        .lore("&7" + description)
        .lore("&aStatus")
        .lore("&7" + getSetupInfo())
        .lore("&aControls")
        .lore("&eLEFT_CLICK \n&7-> Set the locations of the selector item")
        .lore("&eSHIFT_LEFT_CLICK \n&7-> Get the selector item into your inventory")
        .lore("&eRIGHT_CLICK \n&7-> Teleport to location 1")
        .lore("&eSHIFT_RIGHT_CLICK \n&7-> Remove the location near your position")
        .colorizeItem();
    this.item = item.build();
    this.clickConsumer = clickConsumer;
    this.interactConsumer = interactConsumer;
  }


  @Override
  public ItemStack getItem() {
    return item;
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    switch(event.getClick()) {
      case LEFT:
        addLocation(event.getWhoClicked());
        break;
      case SHIFT_LEFT:
        setupInventory.getPlugin().getCuboidSelector().giveSelectorWand((Player) event.getWhoClicked());
        break;
      case RIGHT:
        teleport(event.getWhoClicked());
        break;
      case SHIFT_RIGHT:
        removeLocation(event.getWhoClicked());
        break;
      default:
        break;
    }
    clickConsumer.accept(event);
    event.getWhoClicked().closeInventory();
    InventoryHolder holder = event.getInventory().getHolder();
    if(holder instanceof RefreshableFastInv) {
      ((RefreshableFastInv) holder).refresh();
    }
  }

  private void teleport(HumanEntity player) {
    if(getRawLocation() != null) {
      Location location = LocationSerializer.getLocation(getRawLocation());
      if(location != null) {
        VersionUtils.teleport(player, location);
        new MessageBuilder("&aTeleported to " + name.toUpperCase() + " Location of arena " + setupInventory.getArenaKey()).prefix().send(player);
        return;
      }
    }
    new MessageBuilder("&c" + name.toUpperCase() + " Location not found of arena " + setupInventory.getArenaKey()).prefix().send(player);
  }

  private void addLocation(HumanEntity player) {
    CuboidSelector.Selection selection = setupInventory.getPlugin().getCuboidSelector().getSelection((Player) player);
    if(selection == null || selection.getFirstPos() == null || selection.getSecondPos() == null) {
      new MessageBuilder("&cPlease select both corners before adding an " + name.toUpperCase() + " location!").prefix().send(player);
      return;
    }

    LocationSerializer.saveLoc(setupInventory.getPlugin(), setupInventory.getConfig(), "arenas", "instances." + setupInventory.getArenaKey() + "." + keyName + ".1", selection.getFirstPos());
    LocationSerializer.saveLoc(setupInventory.getPlugin(), setupInventory.getConfig(), "arenas", "instances." + setupInventory.getArenaKey() + "." + keyName + ".2", selection.getSecondPos());
    new MessageBuilder("&e✔ Completed | &a" + name.toUpperCase() + " location for arena " + setupInventory.getArenaKey() + " set at your location!").prefix().send(player);
  }

  private void removeLocation(HumanEntity player) {
    if(getRawLocation() != null) {
      Location location = LocationSerializer.getLocation(getRawLocation());
      if(location != null) {
        double distance = player.getLocation().distanceSquared(location);
        if(distance <= 3) {
          setupInventory.setConfig(keyName, null);
          //considerable to add arena method to remove location
          new MessageBuilder("&e✔ Removed | &a" + name.toUpperCase() + " location for arena " + setupInventory.getArenaKey() + "!").prefix().send(player);
          return;
        }
      }
    }
    new MessageBuilder("&cAround your position no " + name.toUpperCase() + " Location found!").prefix().send(player);
  }

  @Nullable
  private String getRawLocation() {
    return setupInventory.getConfig().getString("instances." + setupInventory.getArenaKey() + "." + keyName + ".1");
  }

  @Override
  public String getSetupInfo() {
    return setupInventory.isLocationOptionDone(keyName);
  }

  @Override
  public boolean getSetupStatus() {
    return getSetupInfo().contains("✔");
  }
}
