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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.items.HandlerItem;
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
public class LocationItem implements CategoryItemHandler {

  private final SetupInventory setupInventory;
  private final ItemStack item;

  private final String name;
  private final String description;
  private final String keyName;
  private final Consumer<InventoryClickEvent> clickConsumer;
  private final Consumer<PlugilyPlayerInteractEvent> interactConsumer;
  private final boolean rightClick;
  private final boolean leftClick;
  private final boolean physical;

  public LocationItem(SetupInventory setupInventory, ItemBuilder item, String name, String description, String keyName) {
    this(setupInventory, item, name, description, keyName, emptyConsumer -> {
    }, emptyConsumer -> {
    });
  }

  public LocationItem(SetupInventory setupInventory, ItemBuilder item, String name, String description, String keyName, Consumer<InventoryClickEvent> clickConsumer, Consumer<PlugilyPlayerInteractEvent> interactConsumer) {
    this(setupInventory, item, name, description, keyName, clickConsumer, interactConsumer, true, true, true);
  }

  public LocationItem(SetupInventory setupInventory, ItemBuilder item, String name, String description, String keyName, Consumer<InventoryClickEvent> clickConsumer, Consumer<PlugilyPlayerInteractEvent> interactConsumer, boolean leftClick, boolean rightClick, boolean physical) {
    this.setupInventory = setupInventory;
    this.name = name;
    this.description = description;
    this.keyName = keyName;
    item
        .name("&7Set &a" + name.toUpperCase() + " &7location")
        .lore("&aInfo")
        .lore("&7" + description)
        .lore("&aStatus:")
        .lore("&7" + getSetupInfo())
        .lore("&aControls")
        .lore("&eLEFT_CLICK &7- Set the location at the position you are standing")
        .lore("&eSHIFT_LEFT_CLICK &7- Get the setup item into your inventory")
        .lore("&eRIGHT_CLICK &7- Teleport to current location")
        .lore("&eSHIFT_RIGHT_CLICK &7- Remove the location near your position")
        .colorizeItem();
    this.item = item.build();
    this.clickConsumer = clickConsumer;
    this.interactConsumer = interactConsumer;
    this.leftClick = leftClick;
    this.rightClick = rightClick;
    this.physical = physical;
  }


  @Override
  public ItemStack getItem() {
    return item;
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    switch(event.getClick()) {
      case LEFT:
        addLocation(event.getWhoClicked(), event.getWhoClicked().getLocation());
        break;
      case SHIFT_LEFT:
        ItemStack itemStack =
            new ItemBuilder(item)
                .amount(1)
                .name("&7Set &a" + name.toUpperCase() + " &7location")
                .lore("&aInfo")
                .lore("&7" + description)
                .lore("&aStatus:")
                .lore("&7Check in the arena editor!")
                .lore("&aControls")
                .lore("&eDROP &7- Remove/Deactivate the item")
                .lore(physical ? "&ePHYSICAL &7- Set a location on physical event (e.g. pressure plate)" : "&cPHYSICAL - DEACTIVATED")
                .lore(leftClick ? "&eLEFT_CLICK_AIR &7- Set the location at the position you are standing" : "&cLEFT_CLICK_AIR - DEACTIVATED")
                .lore(leftClick ? "&eLEFT_CLICK_BLOCK &7- Set the location at the position you clicked" : "&cLEFT_CLICK_BLOCK - DEACTIVATED")
                .lore(rightClick ? "&eRIGHT_CLICK_AIR &7- Teleport to current location" : "&cRIGHT_CLICK_AIR - DEACTIVATED")
                .lore(rightClick ? "&eRIGHT_CLICK_BLOCK &7- Remove the location near your position" : "&cRIGHT_CLICK_BLOCK - DEACTIVATED")
                .colorizeItem()
                .build();
        HandlerItem handlerItem = new HandlerItem(itemStack);
        handlerItem.addDropHandler(dropEvent -> {
          handlerItem.remove();
          dropEvent.getPlayer().getInventory().remove(dropEvent.getItemDrop().getItemStack());
          dropEvent.getItemDrop().remove();
          dropEvent.getPlayer().updateInventory();
          new MessageBuilder("&aRemoved/Deactivated the " + name.toUpperCase() + " Location item!").prefix().send(dropEvent.getPlayer());
        });
        handlerItem.addConsumeHandler(consumeEvent -> consumeEvent.setCancelled(true));
        handlerItem.addInteractHandler(interactEvent -> {
          interactEvent.setCancelled(true);
          switch(interactEvent.getAction()) {
            case PHYSICAL:
            case LEFT_CLICK_AIR:
              addLocation(interactEvent.getPlayer(), interactEvent.getPlayer().getLocation());
              new MessageBuilder("&cPlease keep in mind to use blocks instead of player location for precise coordinates!").prefix().send(interactEvent.getPlayer());
              break;
            case RIGHT_CLICK_BLOCK:
              removeLocation(interactEvent.getPlayer());
              break;
            case RIGHT_CLICK_AIR:
              teleport(interactEvent.getPlayer());
              break;
            case LEFT_CLICK_BLOCK:
              addLocation(interactEvent.getPlayer(), interactEvent.getClickedBlock().getLocation().clone().add(0, 1, 0));
              break;
          }
          interactConsumer.accept(interactEvent);
        });
        handlerItem.setLeftClick(leftClick);
        handlerItem.setPhysical(physical);
        handlerItem.setRightClick(rightClick);
        event.getWhoClicked().getInventory().addItem(handlerItem.getItemStack());
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

  private void addLocation(HumanEntity player, Location location) {
    LocationSerializer.saveLoc(setupInventory.getPlugin(), setupInventory.getConfig(), "arenas", "instances." + setupInventory.getArenaKey() + "." + keyName, location);
    new MessageBuilder("&e✔ Completed | &a" + name.toUpperCase() + " location for arena " + setupInventory.getArenaKey() + " set at your location!").prefix().send(player);
  }

  private void removeLocation(HumanEntity player) {
    if(getRawLocation() != null) {
      Location location = LocationSerializer.getLocation(getRawLocation());
      if(location != null) {
        double distance = player.getLocation().distanceSquared(location);
        if(distance <= 3) {
          setupInventory.getConfig().set("instances." + setupInventory.getArenaKey() + "." + keyName, null);
          //considerable to add arena method to remove location
          new MessageBuilder("&e✔ Removed | &a" + name.toUpperCase() + " location for arena " + setupInventory.getArenaKey() + "!").prefix().send(player);
          ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getConfig(), "arenas");
          return;
        }
      }
    }
    new MessageBuilder("&cAround your position no " + name.toUpperCase() + " Location found!").prefix().send(player);
  }

  @Nullable
  private String getRawLocation() {
    return setupInventory.getConfig().getString("instances." + setupInventory.getArenaKey() + "." + keyName);
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
