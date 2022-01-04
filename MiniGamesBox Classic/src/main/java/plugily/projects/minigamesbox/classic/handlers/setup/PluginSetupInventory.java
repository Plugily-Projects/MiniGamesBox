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

package plugily.projects.minigamesbox.classic.handlers.setup;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.setup.pages.ArenaListPage;
import plugily.projects.minigamesbox.classic.handlers.setup.pages.BooleanPage;
import plugily.projects.minigamesbox.classic.handlers.setup.pages.CountablePage;
import plugily.projects.minigamesbox.classic.handlers.setup.pages.HomePage;
import plugily.projects.minigamesbox.classic.handlers.setup.pages.LocationPage;
import plugily.projects.minigamesbox.classic.handlers.setup.pages.PagesPage;
import plugily.projects.minigamesbox.classic.handlers.setup.pages.ValuePage;

import javax.annotation.Nullable;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 29.12.2021
 */
public class PluginSetupInventory {


  private SetupUtilities.InventoryStage inventoryStage;
  private PluginArena arena;
  private final Player player;
  private final PluginMain plugin;


  public PluginSetupInventory(PluginMain plugin, @Nullable PluginArena arena, Player player) {
    this.plugin = plugin;
    setArena(player, arena);
    this.player = player;
    this.inventoryStage = SetupUtilities.InventoryStage.SETUP_GUI;
    open();
  }

  public PluginSetupInventory(PluginMain plugin, @Nullable PluginArena arena, Player player, SetupUtilities.InventoryStage inventoryStage) {
    this.plugin = plugin;
    setArena(player, arena);
    this.player = player;
    this.inventoryStage = inventoryStage;
    open();
  }

  public void setArena(Player player, PluginArena arena) {
    if(arena == null) {
      plugin.getSetupUtilities().getArena(player);
    } else {
      this.arena = arena;
    }
  }

  public void open() {
    switch(inventoryStage) {
      case SETUP_GUI:
        new HomePage(54, plugin.getPluginMessagePrefix() + "Setup Menu", this).open(player);
        break;
      case ARENA_LIST:
        new ArenaListPage(54, plugin.getPluginMessagePrefix() + "Setup Menu | Arenas", this).open(player);
        break;
      case PAGED_GUI:
        new PagesPage(54, plugin.getPluginMessagePrefix() + "Arena Editor Menu", this).open(player);
        break;
      case PAGED_VALUES:
        new ValuePage(54, plugin.getPluginMessagePrefix() + "Arena Editor", this).open(player);
        break;
      case PAGED_BOOLEAN:
        new BooleanPage(54, plugin.getPluginMessagePrefix() + "Arena Editor", this).open(player);
        break;
      case PAGED_COUNTABLE:
        new CountablePage(54, plugin.getPluginMessagePrefix() + "Arena Editor", this).open(player);
        break;
      case PAGED_LOCATIONS:
        new LocationPage(54, plugin.getPluginMessagePrefix() + "Arena Editor", this).open(player);
        break;
      default:
        break;
    }
    plugin.getSetupUtilities().sendProTip(player);
  }

  public void open(SetupUtilities.InventoryStage inventoryStage) {
    setInventoryStage(inventoryStage);
    open();
  }

  public boolean addAdditionalArenaValidateValues(InventoryClickEvent event, PluginArena arena, PluginMain plugin, FileConfiguration config) {
    return true;
  }

  public void addAdditionalArenaSetValues(PluginArena arena, FileConfiguration config) {

  }


  public void setInventoryStage(SetupUtilities.InventoryStage inventoryStage) {
    this.inventoryStage = inventoryStage;
  }


  public Player getPlayer() {
    return player;
  }

  public PluginArena getArena() {
    return arena;
  }

  public void setArena(PluginArena arena) {
    this.arena = arena;
  }

  public PluginMain getPlugin() {
    return plugin;
  }

  public SetupUtilities.InventoryStage getInventoryStage() {
    return inventoryStage;
  }
}
