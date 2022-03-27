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

package plugily.projects.minigamesbox.classic.handlers.setup.pages;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.PluginSetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupUtilities;
import plugily.projects.minigamesbox.classic.handlers.sign.ArenaSign;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 04.01.2022
 */
public class BooleanPage extends NormalFastInv implements SetupPage {

  private final PluginSetupInventory setupInventory;

  public BooleanPage(int size, String title, PluginSetupInventory pluginSetupInventory) {
    super(size, title);
    this.setupInventory = pluginSetupInventory;
    prepare();
  }

  @Override
  public void prepare() {
    injectItems();
    setForceRefresh(true);
    setupInventory.getPlugin().getSetupUtilities().setDefaultItems(setupInventory, this, XMaterial.GREEN_STAINED_GLASS_PANE.parseItem(), XMaterial.BLUE_STAINED_GLASS_PANE.parseItem(), event -> setupInventory.open(SetupUtilities.InventoryStage.PAGED_COUNTABLE), XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), event -> setupInventory.open(SetupUtilities.InventoryStage.PAGED_GUI));
    refresh();
  }

  @Override
  public void injectItems() {
    ItemStack registeredItem;
    if(!setupInventory.getArena().isReady()) {
      registeredItem = new ItemBuilder(XMaterial.FIREWORK_ROCKET.parseItem())
          .name(new MessageBuilder("&e&lRegister Arena - Finish Setup").build())
          .lore(ChatColor.GRAY + "Click this when you're done with configuration.")
          .lore(ChatColor.GRAY + "It will validate and register setupInventory.getArena().")
          .build();
    } else {
      registeredItem = new ItemBuilder(Material.BARRIER)
          .name(new MessageBuilder("&a&lArena Registered - Congratulations").build())
          .lore(ChatColor.GRAY + "This arena is already registered!")
          .lore(ChatColor.GRAY + "Good job, you went through whole setup!")
          .lore(ChatColor.GRAY + "You can play on this arena now!")
          .enchantment(Enchantment.DURABILITY)
          .build();
    }
    setItem(43, ClickableItem.of(registeredItem, event -> {
      if(setupInventory.getArena().isReady()) {
        event.getWhoClicked().sendMessage(ChatColor.GREEN + "This arena was already validated and is ready to use!");
        return;
      }

      for(String s : new String[]{"lobbylocation", "startlocation", "endlocation", "spectatorlocation"}) {
        String loc = setupInventory.getPlugin().getSetupUtilities().getConfig().getString("instances." + setupInventory.getArena().getId() + "." + s);

        if(loc == null || loc.equals(LocationSerializer.locationToString(Bukkit.getWorlds().get(0).getSpawnLocation()))) {
          new MessageBuilder("&c&l✘ &cArena validation failed! Please configure following spawns properly: " + s + " (cannot be world spawn location)").player((Player) event.getWhoClicked()).sendPlayer();
          return;
        }
      }

      if(!setupInventory.addAdditionalArenaValidateValues(event, setupInventory.getArena(), setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig())) {
        return;
      }

      new MessageBuilder("&a&l✔ &aValidation succeeded! Registering new arena instance: " + setupInventory.getArena().getId()).player((Player) event.getWhoClicked()).sendPlayer();
      setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".isdone", true);
      ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
      List<Sign> signsToUpdate = new ArrayList<>();
      setupInventory.getPlugin().getArenaRegistry().unregisterArena(setupInventory.getArena());

      for(ArenaSign arenaSign : setupInventory.getPlugin().getSignManager().getArenaSigns()) {
        if(arenaSign.getArena().equals(setupInventory.getArena())) {
          signsToUpdate.add(arenaSign.getSign());
        }
      }
      setupInventory.setArena(setupInventory.getPlugin().getArenaRegistry().getNewArena(setupInventory.getArena().getId()));
      setupInventory.getArena().setReady(true);
      setupInventory.getArena().setMinimumPlayers(setupInventory.getPlugin().getSetupUtilities().getConfig().getInt("instances." + setupInventory.getArena().getId() + ".minimumplayers"));
      setupInventory.getArena().setMaximumPlayers(setupInventory.getPlugin().getSetupUtilities().getConfig().getInt("instances." + setupInventory.getArena().getId() + ".maximumplayers"));
      setupInventory.getArena().setMapName(setupInventory.getPlugin().getSetupUtilities().getConfig().getString("instances." + setupInventory.getArena().getId() + ".mapname"));
      setupInventory.getArena().setLobbyLocation(LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString("instances." + setupInventory.getArena().getId() + ".lobbylocation")));
      setupInventory.getArena().setStartLocation(LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString("instances." + setupInventory.getArena().getId() + ".startlocation")));
      setupInventory.getArena().setEndLocation(LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString("instances." + setupInventory.getArena().getId() + ".endlocation")));
      setupInventory.getArena().setSpectatorLocation(LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString("instances." + setupInventory.getArena().getId() + ".spectatorlocation")));

      setupInventory.addAdditionalArenaSetValues(setupInventory.getArena(), setupInventory.getPlugin().getSetupUtilities().getConfig());

      setupInventory.getPlugin().getArenaRegistry().registerArena(setupInventory.getArena());
      setupInventory.getArena().start();
      for(Sign s : signsToUpdate) {
        setupInventory.getPlugin().getSignManager().getArenaSigns().add(new ArenaSign(s, setupInventory.getArena()));
      }
      setupInventory.getPlayer().closeInventory();
      setupInventory.getPlugin().getSetupUtilities().removeSetupInventory(setupInventory.getPlayer());
    }));
  }

  @Override
  protected void onClick(InventoryClickEvent event) {
    injectItems();
    refresh();
  }

}
