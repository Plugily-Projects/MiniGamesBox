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


package plugily.projects.minigamesbox.classic.handlers.setup.components;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.Main;
import plugily.projects.minigamesbox.classic.arena.Arena;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.handlers.sign.ArenaSign;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.inventory.normal.FastInv;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class ArenaRegisterComponent implements SetupComponent {

  private SetupInventory setupInventory;

  @Override
  public void prepare(SetupInventory setupInventory) {
    this.setupInventory = setupInventory;
  }

  @Override
  public void injectComponents(FastInv gui) {
    FileConfiguration config = setupInventory.getConfig();
    Main plugin = setupInventory.getPlugin();
    ItemStack registeredItem;
    if(!setupInventory.getArena().isReady()) {
      registeredItem = new ItemBuilder(XMaterial.FIREWORK_ROCKET.parseItem())
          .name(plugin.getChatManager().colorRawMessage("&e&lRegister Arena - Finish Setup"))
          .lore(ChatColor.GRAY + "Click this when you're done with configuration.")
          .lore(ChatColor.GRAY + "It will validate and register arena.")
          .build();
    } else {
      registeredItem = new ItemBuilder(Material.BARRIER)
          .name(plugin.getChatManager().colorRawMessage("&a&lArena Registered - Congratulations"))
          .lore(ChatColor.GRAY + "This arena is already registered!")
          .lore(ChatColor.GRAY + "Good job, you went through whole setup!")
          .lore(ChatColor.GRAY + "You can play on this arena now!")
          .build();
    }
    gui.setItem(11, registeredItem, e -> {
      Arena arena = setupInventory.getArena();
      if(arena == null) {
        return;
      }
      if(arena.isReady()) {
        e.getWhoClicked().sendMessage(ChatColor.GREEN + "This arena was already validated and is ready to use!");
        return;
      }

      for(String s : new String[]{"lobbylocation", "startlocation", "endlocation"}) {
        String loc = config.getString("instances." + arena.getId() + "." + s);

        if(loc == null || loc.equals(LocationSerializer.locationToString(Bukkit.getWorlds().get(0).getSpawnLocation()))) {
          e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cArena validation failed! Please configure following spawns properly: " + s + " (cannot be world spawn location)"));
          return;
        }
      }

      e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&a&l✔ &aValidation succeeded! Registering new arena instance: " + arena.getId()));
      config.set("instances." + arena.getId() + ".isdone", true);
      ConfigUtils.saveConfig(plugin, config, "arenas");
      List<Sign> signsToUpdate = new ArrayList<>();
      plugin.getArenaRegistry().unregisterArena(arena);

      for(ArenaSign arenaSign : plugin.getSignManager().getArenaSigns()) {
        if(arenaSign.getArena().equals(setupInventory.getArena())) {
          signsToUpdate.add(arenaSign.getSign());
        }
      }
      arena = new Arena(arena.getId());
      arena.setReady(true);
      arena.setMinimumPlayers(config.getInt("instances." + arena.getId() + ".minimumplayers"));
      arena.setMaximumPlayers(config.getInt("instances." + arena.getId() + ".maximumplayers"));
      arena.setMapName(config.getString("instances." + arena.getId() + ".mapname"));
      arena.setLobbyLocation(LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".lobbylocation")));
      arena.setStartLocation(LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".startlocation")));
      arena.setEndLocation(LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".endlocation")));

      plugin.getArenaRegistry().registerArena(arena);
      arena.start();
      for(Sign s : signsToUpdate) {
        plugin.getSignManager().getArenaSigns().add(new ArenaSign(s, arena));
      }
    });
  }

}
