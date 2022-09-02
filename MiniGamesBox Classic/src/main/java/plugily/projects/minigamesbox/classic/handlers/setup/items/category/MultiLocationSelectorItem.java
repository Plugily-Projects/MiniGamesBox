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
import org.bukkit.configuration.ConfigurationSection;
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
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.inventory.common.RefreshableFastInv;

import java.util.function.Consumer;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 31.12.2021
 */
public class MultiLocationSelectorItem implements CategoryItemHandler {

  private final SetupInventory setupInventory;
  private final ItemStack item;

  private final String name;
  private final String description;
  private final String keyName;

  private final int minimumValue;
  private final Consumer<InventoryClickEvent> clickConsumer;
  private final Consumer<PlugilyPlayerInteractEvent> interactConsumer;
  private final boolean rightClick;
  private final boolean leftClick;
  private final boolean physical;

  public MultiLocationSelectorItem(SetupInventory setupInventory, ItemBuilder item, String name, String description, String keyName, int minimumValue) {
    this(setupInventory, item, name, description, keyName, minimumValue, emptyConsumer -> {
    }, emptyConsumer -> {
    });
  }

  public MultiLocationSelectorItem(SetupInventory setupInventory, ItemBuilder item, String name, String description, String keyName, int minimumValue, Consumer<InventoryClickEvent> clickConsumer, Consumer<PlugilyPlayerInteractEvent> interactConsumer) {
    this(setupInventory, item, name, description, keyName, minimumValue, clickConsumer, interactConsumer, true, true, true);
  }

  public MultiLocationSelectorItem(SetupInventory setupInventory, ItemBuilder item, String name, String description, String keyName, int minimumValue, Consumer<InventoryClickEvent> clickConsumer, Consumer<PlugilyPlayerInteractEvent> interactConsumer, boolean leftClick, boolean rightClick, boolean physical) {
    this.setupInventory = setupInventory;
    this.name = name;
    this.description = description;
    this.keyName = keyName;
    this.minimumValue = minimumValue;
    item
        .name("&7Add &a" + name.toUpperCase() + " &7location selection")
        .lore("&aInfo")
        .lore("&7" + description)
        .lore("&aStatus")
        .lore("&7" + getSetupInfo())
        .lore("&aControls")
        .lore("&eLEFT_CLICK \n&7-> Add the location you selected with the selector")
        .lore("&eSHIFT_LEFT_CLICK \n&7-> Get the selector item into your inventory")
        .lore("&eRIGHT_CLICK \n&7-> Remove a location near your position")
        .lore("&eSHIFT_RIGHT_CLICK \n&7-> Remove all locations")
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
        addLocation(event.getWhoClicked());
        break;
      case SHIFT_LEFT:
        setupInventory.getPlugin().getCuboidSelector().giveSelectorWand((Player) event.getWhoClicked());
        break;
      case RIGHT:
        removeLocation(event.getWhoClicked(), false);
        break;
      case SHIFT_RIGHT:
        removeLocation(event.getWhoClicked(), true);
        break;
      default:
        break;
    }
    clickConsumer.accept(event);
    setupInventory.closeInventory(event.getWhoClicked());
    InventoryHolder holder = event.getInventory().getHolder();
    if(holder instanceof RefreshableFastInv) {
      ((RefreshableFastInv) holder).refresh();
    }
  }

  private void addLocation(HumanEntity player) {
    CuboidSelector.Selection selection = setupInventory.getPlugin().getCuboidSelector().getSelection((Player) player);
    if(selection == null || selection.getFirstPos() == null || selection.getSecondPos() == null) {
      new MessageBuilder("&cPlease select both corners before adding an " + name.toUpperCase() + " location!").prefix().send(player);
      return;
    }

    ConfigurationSection configurationSection = getRawLocations();
    int value = (configurationSection != null ? configurationSection.getKeys(false).size() : 0) + 1;
    LocationSerializer.saveLoc(setupInventory.getPlugin(), setupInventory.getConfig(), "arenas", "instances." + setupInventory.getArenaKey() + "." + keyName + "." + value + ".1", selection.getFirstPos());
    LocationSerializer.saveLoc(setupInventory.getPlugin(), setupInventory.getConfig(), "arenas", "instances." + setupInventory.getArenaKey() + "." + keyName + "." + value + ".2", selection.getSecondPos());
    String progress = value >= minimumValue ? "&e✔ Completed | " : "&c✘ Not completed | ";
    new MessageBuilder(progress + "&a" + name.toUpperCase() + " spawn added! &8(&7" + value + "/" + minimumValue + "&8)").prefix().send(player);
    if(value == minimumValue) {
      new MessageBuilder("&eInfo | &aYou can add more than " + minimumValue + name.toUpperCase() + " spawns! " + minimumValue + " is just a minimum!").prefix().send(player);
    }
  }

  private void removeLocation(HumanEntity player, boolean deleteAll) {
    if(deleteAll) {
      new MessageBuilder("&e✔ Removed | &a" + name.toUpperCase() + " location for arena " + setupInventory.getArenaKey() + "! (All locations)").prefix().send(player);
      setupInventory.getConfig().set("instances." + setupInventory.getArenaKey() + "." + keyName, null);
      return;
    }
    ConfigurationSection configurationSection = getRawLocations();
    if(configurationSection != null) {
      for(String key : configurationSection.getKeys(false)) {
        Location location1 = LocationSerializer.getLocation(configurationSection.getString(key + ".1"));
        Location location2 = LocationSerializer.getLocation(configurationSection.getString(key + ".2"));

        double distance1 = player.getLocation().distanceSquared(location1);
        double distance2 = player.getLocation().distanceSquared(location2);
        if(distance1 <= 2 || distance2 <= 2) {
          setupInventory.setConfig(keyName + "." + key, null);
          //considerable to add arena method to remove location
          new MessageBuilder("&e✔ Removed | &a" + name.toUpperCase() + " location for arena " + setupInventory.getArenaKey() + "! (" + location1 + ")").prefix().send(player);
          return;
        }
      }
    }
    new MessageBuilder("&cAround your position no " + name.toUpperCase() + " Location found!").prefix().send(player);
  }

  @Nullable
  private ConfigurationSection getRawLocations() {
    return setupInventory.getConfig().getConfigurationSection("instances." + setupInventory.getArenaKey() + "." + keyName);
  }


  @Override
  public String getSetupInfo() {
    return setupInventory.isSectionOptionDone(keyName, minimumValue);
  }

  @Override
  public boolean getSetupStatus() {
    return getSetupInfo().contains("✔");
  }
}
