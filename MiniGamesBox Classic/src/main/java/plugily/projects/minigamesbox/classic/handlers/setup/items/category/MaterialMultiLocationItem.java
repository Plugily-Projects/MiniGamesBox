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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.items.HandlerItem;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.inventory.common.RefreshableFastInv;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 31.12.2021
 */
public class MaterialMultiLocationItem implements CategoryItemHandler {

  private final SetupInventory setupInventory;
  private final ItemStack item;

  private final String name;
  private final String description;
  private final String keyName;
  private final Set<Material> checkMaterials;
  private final boolean removeBungee;

  private final int minimumValue;
  private final Consumer<InventoryClickEvent> clickConsumer;
  private final Consumer<PlugilyPlayerInteractEvent> interactConsumer;
  private final boolean rightClick;
  private final boolean leftClick;
  private final boolean physical;

  public MaterialMultiLocationItem(SetupInventory setupInventory, ItemBuilder item, String name, String description, String keyName, Set<Material> checkMaterials, boolean removeBungee, int minimumValue) {
    this(setupInventory, item, name, description, keyName, checkMaterials, removeBungee, minimumValue, emptyConsumer -> {
    }, emptyConsumer -> {
    });
  }

  public MaterialMultiLocationItem(SetupInventory setupInventory, ItemBuilder item, String name, String description, String keyName, Set<Material> checkMaterials, boolean removeBungee, int minimumValue, Consumer<InventoryClickEvent> clickConsumer, Consumer<PlugilyPlayerInteractEvent> interactConsumer) {
    this(setupInventory, item, name, description, keyName, checkMaterials, removeBungee, minimumValue, clickConsumer, interactConsumer, true, true, false);
  }

  public MaterialMultiLocationItem(SetupInventory setupInventory, ItemBuilder item, String name, String description, String keyName, Set<Material> checkMaterials, boolean removeBungee, int minimumValue, Consumer<InventoryClickEvent> clickConsumer, Consumer<PlugilyPlayerInteractEvent> interactConsumer, boolean leftClick, boolean rightClick, boolean physical) {
    this.setupInventory = setupInventory;
    this.name = name;
    this.description = description;
    this.keyName = keyName;
    this.checkMaterials = checkMaterials;
    this.removeBungee = removeBungee;
    this.minimumValue = minimumValue;
    item
        .name("&7Add &a" + name.toUpperCase() + " &7location")
        .lore("&aInfo")
        .lore("&7" + description)
        .lore("&aStatus");
    if(removeBungee) {
      item
          .lore("&cOption disabled with BungeeMode activated!")
          .lore("&7Bungee mode is meant to be one arena per server")
          .lore("&7If you wish to have multi arena, disable BungeeMode in config!")
          .colorizeItem();

    } else {
      item
          .lore("&7" + getSetupInfo())
          .lore("&aControls")
          .lore("&eLEFT_CLICK \n&7-> Add the " + name.toUpperCase() + " location at the position you are *looking*")
          .lore("&eSHIFT_LEFT_CLICK \n&7-> Get the setup item into your inventory")
          .lore("&eRIGHT_CLICK \n&7-> Remove a " + name.toUpperCase() + " location near your position")
          .lore("&eSHIFT_RIGHT_CLICK \n&7-> Remove all " + name.toUpperCase() + " locations")
          .colorizeItem();

    }
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
    if(removeBungee) {
      event.setCancelled(true);
      return;
    }
    switch(event.getClick()) {
      case LEFT:
        Block targetBlock = event.getWhoClicked().getTargetBlock(null, 7);
        if(!checkMaterials.contains(targetBlock.getType())) {
          new MessageBuilder("&c&l✘ &cPlease only look at a location where already is a " + checkMaterials + " to add it as a " + name.toUpperCase() + "!").prefix().send(event.getWhoClicked());
          return;
        }
        addLocation(event.getWhoClicked(), event.getWhoClicked().getLocation());
        break;
      case SHIFT_LEFT:
        ItemStack itemStack =
            new ItemBuilder(item.getType())
                .amount(1)
                .name("&7Add &a" + name.toUpperCase() + " &7location")
                .lore("&aInfo")
                .lore("&7" + description)
                .lore("&aStatus")
                .lore("&7Check in the arena editor!")
                .lore("&aControls")
                .lore("&eDROP \n&7-> Remove/Deactivate the item")
                //.lore(physical ? "&ePHYSICAL \n&7-> Not supported" : "&cPHYSICAL - DEACTIVATED")
                .lore(leftClick ? "&eLEFT_CLICK_AIR \n&7-> Not supported" : "&cLEFT_CLICK_AIR - DEACTIVATED")
                .lore(leftClick ? "&eLEFT_CLICK_BLOCK \n&7-> Remove a location at the position you clicked" : "&cLEFT_CLICK_BLOCK - DEACTIVATED")
                .lore(rightClick ? "&eRIGHT_CLICK_AIR \n&7-> Teleport through locations" : "&cRIGHT_CLICK_AIR - DEACTIVATED")
                .lore(rightClick ? "&eRIGHT_CLICK_BLOCK \n&7-> Add the location at the position you clicked" : "&cRIGHT_CLICK_BLOCK - DEACTIVATED")
                .colorizeItem()
                .build();
        HandlerItem handlerItem = new HandlerItem(itemStack);
        handlerItem.addDropHandler(dropEvent -> {
          handlerItem.remove();
          dropEvent.getPlayer().getInventory().remove(dropEvent.getItemDrop().getItemStack());
          dropEvent.getItemDrop().remove();
          dropEvent.getPlayer().updateInventory();
          new MessageBuilder("&aRemoved/&aDeactivated the " + name.toUpperCase() + " Location item!").prefix().send(dropEvent.getPlayer());
        });
        handlerItem.addConsumeHandler(consumeEvent -> consumeEvent.setCancelled(true));
        handlerItem.addInteractHandler(interactEvent -> {
          interactEvent.setCancelled(true);
          Location location = interactEvent.getClickedBlock().getLocation();
          Block block = location.getBlock();
          switch(interactEvent.getAction()) {
            case PHYSICAL:
            case LEFT_CLICK_AIR:
              new MessageBuilder("&c&l✘ &cYou can't use a location that is at your player location, please select the " + checkMaterials + "!").prefix().send(interactEvent.getPlayer());
              break;
            case LEFT_CLICK_BLOCK:
              if(!checkMaterials.contains(block.getType())) {
                new MessageBuilder("&c&l✘ &cPlease only use location where already is a " + checkMaterials + " to remove it as a " + name.toUpperCase() + "!").prefix().send(interactEvent.getPlayer());
                return;
              }

              removeLocation(interactEvent.getPlayer(), false);
              break;
            case RIGHT_CLICK_BLOCK:
              if(!checkMaterials.contains(block.getType())) {
                new MessageBuilder("&c&l✘ &cPlease only use location where already is a " + checkMaterials + " to add it as a " + name.toUpperCase() + "!").prefix().send(interactEvent.getPlayer());
                return;
              }

              if(location.distance(interactEvent.getClickedBlock().getWorld().getSpawnLocation()) <= Bukkit.getServer().getSpawnRadius()) {
                new MessageBuilder("&c&l✖ &cWarning | Server spawn protection is set to &6" + Bukkit.getServer().getSpawnRadius()
                    + " &cand location you want to place is in radius of this protection! &c&lNon opped players won't be able to interact with this " + checkMaterials + " and can't join the game! Reduce the spawn radius (server.properties) or change your location!").prefix().send(interactEvent.getPlayer());
              }
              addLocation(interactEvent.getPlayer(), block.getLocation());
              break;
            case RIGHT_CLICK_AIR:
              teleport(interactEvent.getPlayer());
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
        removeLocation(event.getWhoClicked(), false);
        break;
      case SHIFT_RIGHT:
        removeLocation(event.getWhoClicked(), true);
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
    if(!getLocationsList().isEmpty()) {
      Location location = getLocationsList().get(setupInventory.getPlugin().getRandom().nextInt(getLocationsList().size() - 1));
      VersionUtils.teleport(player, location);
      new MessageBuilder("&aTeleported to " + name.toUpperCase() + " Location of arena " + setupInventory.getArenaKey() + " (" + location + ")").prefix().send(player);
      return;
    }
    new MessageBuilder("&c" + name.toUpperCase() + " Location not found of arena " + setupInventory.getArenaKey()).prefix().send(player);
  }

  private void addLocation(HumanEntity player, Location location) {
    ConfigurationSection configurationSection = getRawLocations();
    int value = (configurationSection != null ? configurationSection.getKeys(false).size() : 0) + 1;
    LocationSerializer.saveLoc(setupInventory.getPlugin(), setupInventory.getConfig(), "arenas", "instances." + setupInventory.getArenaKey() + "." + keyName + "." + value, location);
    String progress = value >= minimumValue ? "&e✔ Completed | " : "&c✘ Not completed | ";
    new MessageBuilder(progress + "&a" + name.toUpperCase() + " location added! &8(&7" + minimumValue + "/2&8)").prefix().send(player);
    if(value == minimumValue) {
      new MessageBuilder("&eInfo | &aYou can add more than " + minimumValue + name.toUpperCase() + " location! " + minimumValue + " is just a minimum!").prefix().send(player);
    }
  }

  private void removeLocation(HumanEntity player, boolean deleteAll) {
    if(!getLocationsList().isEmpty()) {
      for(Location location : getLocationsList()) {
        double distance = player.getLocation().distanceSquared(location);
        if(deleteAll || distance <= 2) {
          setupInventory.getConfig().set("instances." + setupInventory.getArenaKey() + "." + keyName, null);
          setupInventory.setConfig(keyName, null);
          //considerable to add arena method to remove location
          new MessageBuilder("&e✔ Removed | &a" + name.toUpperCase() + " location for arena " + setupInventory.getArenaKey() + "! (" + location + ")").prefix().send(player);
          new MessageBuilder("You can now remove the " + checkMaterials.toString() + "!").prefix().send(player);
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

  private List<Location> getLocationsList() {
    List<Location> locations = new ArrayList<>();
    ConfigurationSection configurationSection = getRawLocations();
    if(configurationSection != null) {
      for(String key : configurationSection.getKeys(false)) {
        locations.add(LocationSerializer.getLocation(configurationSection.getString(key)));
      }
    }
    return locations;
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
