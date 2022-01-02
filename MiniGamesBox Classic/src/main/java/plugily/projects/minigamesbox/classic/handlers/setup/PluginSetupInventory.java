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


import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.setup.items.CountItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.LocationItem;
import plugily.projects.minigamesbox.classic.handlers.sign.ArenaSign;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.conversation.SimpleConversationBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.common.item.ItemMap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 29.12.2021
 */
public class PluginSetupInventory {


  private PluginArena arena;
  private final Player player;
  private final PluginMain plugin;


  public PluginSetupInventory(PluginMain plugin, PluginArena arena, Player player) {
    this.plugin = plugin;
    this.arena = arena;
    this.player = player;
  }

  public void init() {
    addLocationItems();
    addCountableItems();
    addValueItems();
    addBooleanItems();
    plugin.getSetupUtilities().addSetupInventory(player, arena);
    plugin.getSetupUtilities().getPagedGUI().refresh();
    plugin.getSetupUtilities().getSetupGUI().refresh();
  }

  public void addValueItems() {
    plugin.getSetupUtilities().getPagedItemMapOf(SetupUtilities.InventoryStage.PAGED_VALUES).setItem(43, ClickableItem.of(new ItemBuilder(Material.NAME_TAG)
        .name(plugin.getChatManager().colorRawMessage("&e&lChange Map Name"))
        .lore(ChatColor.GRAY + "Click to set arena map name")
        .lore("", plugin.getChatManager().colorRawMessage("&a&lCurrently: &e" + plugin.getSetupUtilities().getConfig().getString("instances." + arena.getId() + ".mapname")))
        .build(), event -> {
      event.getWhoClicked().closeInventory();
      new SimpleConversationBuilder(plugin).withPrompt(new StringPrompt() {
        @Override
        public @NotNull String getPromptText(ConversationContext context) {
          return plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&ePlease type in chat arena name! You can use color codes.");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
          String name = plugin.getChatManager().colorRawMessage(input);
          player.sendRawMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aName of arena " + arena.getId() + " set to " + name));
          arena.setMapName(name);
          plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".mapname", arena.getMapName());
          ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");

          openPagedGui();
          return Prompt.END_OF_CONVERSATION;
        }
      }).buildFor(player);
    }));
  }

  public void addCountableItems() {
    plugin.getSetupUtilities().getPagedItemMapOf(SetupUtilities.InventoryStage.PAGED_COUNTABLE).setItem(1, new CountItem(
        new ItemBuilder(Material.COAL).amount(plugin.getSetupUtilities().getMinimumValueHigherThanZero("minimumplayers", this))
            .name(plugin.getChatManager().colorRawMessage("&e&lSet Minimum Players Amount"))
            .lore(ChatColor.GRAY + "LEFT click to decrease")
            .lore(ChatColor.GRAY + "RIGHT click to increase")
            .lore(ChatColor.DARK_GRAY + "(how many players are needed")
            .lore(ChatColor.DARK_GRAY + "for game to start lobby countdown)")
            .lore("", plugin.getSetupUtilities().isOptionDone("minimumplayers", this))
            .build(), event -> {
      ItemStack itemStack = event.getInventory().getItem(event.getSlot()), currentItem = event.getCurrentItem();
      if(itemStack == null || currentItem == null) {
        return;
      }
      plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".minimumplayers", currentItem.getAmount());
      arena.setMinimumPlayers(currentItem.getAmount());
      ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
      refreshPagedGui();
    }));

    plugin.getSetupUtilities().getPagedItemMapOf(SetupUtilities.InventoryStage.PAGED_COUNTABLE).setItem(10, new CountItem(new ItemBuilder(Material.REDSTONE)
        .amount(plugin.getSetupUtilities().getMinimumValueHigherThanZero("maximumplayers", this))
        .name(plugin.getChatManager().colorRawMessage("&e&lSet Maximum Players Amount"))
        .lore(ChatColor.GRAY + "LEFT click to decrease")
        .lore(ChatColor.GRAY + "RIGHT click to increase")
        .lore(ChatColor.DARK_GRAY + "(how many players arena can hold)")
        .lore("", plugin.getSetupUtilities().isOptionDone("maximumplayers", this))
        .build(), event -> {
      ItemStack currentItem = event.getCurrentItem();
      if(currentItem == null) {
        return;
      }
      plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".maximumplayers", currentItem.getAmount());
      arena.setMaximumPlayers(currentItem.getAmount());
      ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
      refreshPagedGui();
    }));
  }

  public void addBooleanItems() {
    ItemStack registeredItem;
    if(!arena.isReady()) {
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
          .enchantment(Enchantment.DURABILITY)
          .build();
    }
    plugin.getSetupUtilities().getPagedItemMapOf(SetupUtilities.InventoryStage.PAGED_BOOLEAN).setItem(43, ClickableItem.of(registeredItem, event -> {
      if(arena.isReady()) {
        event.getWhoClicked().sendMessage(ChatColor.GREEN + "This arena was already validated and is ready to use!");
        return;
      }

      for(String s : new String[]{"lobbylocation", "startlocation", "endlocation"}) {
        String loc = plugin.getSetupUtilities().getConfig().getString("instances." + arena.getId() + "." + s);

        if(loc == null || loc.equals(LocationSerializer.locationToString(Bukkit.getWorlds().get(0).getSpawnLocation()))) {
          event.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cArena validation failed! Please configure following spawns properly: " + s + " (cannot be world spawn location)"));
          return;
        }
      }

      if(!addAdditionalArenaValidateValues(event, arena, plugin, plugin.getSetupUtilities().getConfig())) {
        return;
      }

      event.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&a&l✔ &aValidation succeeded! Registering new arena instance: " + arena.getId()));
      plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".isdone", true);
      ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
      List<Sign> signsToUpdate = new ArrayList<>();
      plugin.getArenaRegistry().unregisterArena(arena);

      for(ArenaSign arenaSign : plugin.getSignManager().getArenaSigns()) {
        if(arenaSign.getArena().equals(arena)) {
          signsToUpdate.add(arenaSign.getSign());
        }
      }
      arena = new PluginArena(arena.getId());
      arena.setReady(true);
      arena.setMinimumPlayers(plugin.getSetupUtilities().getConfig().getInt("instances." + arena.getId() + ".minimumplayers"));
      arena.setMaximumPlayers(plugin.getSetupUtilities().getConfig().getInt("instances." + arena.getId() + ".maximumplayers"));
      arena.setMapName(plugin.getSetupUtilities().getConfig().getString("instances." + arena.getId() + ".mapname"));
      arena.setLobbyLocation(LocationSerializer.getLocation(plugin.getSetupUtilities().getConfig().getString("instances." + arena.getId() + ".lobbylocation")));
      arena.setStartLocation(LocationSerializer.getLocation(plugin.getSetupUtilities().getConfig().getString("instances." + arena.getId() + ".startlocation")));
      arena.setEndLocation(LocationSerializer.getLocation(plugin.getSetupUtilities().getConfig().getString("instances." + arena.getId() + ".endlocation")));

      addAdditionalArenaSetValues(arena, plugin.getSetupUtilities().getConfig());

      plugin.getArenaRegistry().registerArena(arena);
      arena.start();
      for(Sign s : signsToUpdate) {
        plugin.getSignManager().getArenaSigns().add(new ArenaSign(s, arena));
      }
      player.closeInventory();
      plugin.getSetupUtilities().removeSetupInventory(player);
    }));
  }

  public boolean addAdditionalArenaValidateValues(InventoryClickEvent event, PluginArena arena, PluginMain plugin, FileConfiguration config) {
    return true;
  }

  public void addAdditionalArenaSetValues(PluginArena arena, FileConfiguration config) {

  }

  public void addLocationItems() {
    plugin.getSetupUtilities().getPagedItemMapOf(SetupUtilities.InventoryStage.PAGED_LOCATIONS).setItem(1, new LocationItem(new ItemBuilder(Material.REDSTONE_BLOCK)
        .name(plugin.getChatManager().colorRawMessage("&e&lSet Ending Location"))
        .lore(ChatColor.GRAY + "Click to set the ending location")
        .lore(ChatColor.GRAY + "on the place where you are standing.")
        .lore(ChatColor.DARK_GRAY + "(location where players will be")
        .lore(ChatColor.DARK_GRAY + "teleported after the game)")
        .lore("", plugin.getSetupUtilities().isOptionDoneBool("endlocation", this))
        .build(), event -> {
      String serializedLocation = event.getWhoClicked().getLocation().getWorld().getName() + "," + event.getWhoClicked().getLocation().getX() + "," + event.getWhoClicked().getLocation().getY() + ","
          + event.getWhoClicked().getLocation().getZ() + "," + event.getWhoClicked().getLocation().getYaw() + ",0.0";

      event.getWhoClicked().closeInventory();
      plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".endlocation", serializedLocation);
      arena.setEndLocation(event.getWhoClicked().getLocation());
      event.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aEnding location for arena " + arena.getId() + " set at your location!"));
      ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
      refreshPagedGui();
    }, event -> {
      switch(event.getAction()) {
        case LEFT_CLICK_AIR:
          String serializedLocation = event.getPlayer().getLocation().getWorld().getName() + "," + event.getPlayer().getLocation().getX() + "," + event.getPlayer().getLocation().getY() + ","
              + event.getPlayer().getLocation().getZ() + "," + event.getPlayer().getLocation().getYaw() + ",0.0";

          plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".endlocation", serializedLocation);
          arena.setEndLocation(event.getPlayer().getLocation());
          event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aEnding location for arena " + arena.getId() + " set at your location!"));
          event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease keep in mind to use blocks instead of player location for precise coordinates!"));
          ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
          refreshPagedGui();
          break;
        case RIGHT_CLICK_BLOCK:
        case RIGHT_CLICK_AIR:
          plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".endlocation", null);
          arena.setEndLocation(null);
          arena.setReady(false);
          event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Removed | &aEnding location for arena " + arena.getId() + "!"));
          ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
          refreshPagedGui();
          break;
        case LEFT_CLICK_BLOCK:
          String serializedBlockLocation = event.getClickedBlock().getLocation().getWorld().getName() + "," + event.getClickedBlock().getLocation().getX() + "," + event.getClickedBlock().getLocation().getY() + 1 + ","
              + event.getClickedBlock().getLocation().getZ() + "," + event.getClickedBlock().getLocation().getYaw() + ",0.0";

          plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".endlocation", serializedBlockLocation);
          arena.setEndLocation(event.getClickedBlock().getRelative(0, 1, 0).getLocation());
          event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aEnding location for arena " + arena.getId() + " set at your location!"));
          event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease keep in mind to use blocks instead of player location for precise coordinates!"));
          ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
          refreshPagedGui();
          break;
      }
    }, true, true, false));


    ItemStack bungeeItem;
    if(!plugin.getConfigPreferences().getOption("BUNGEEMODE")) {
      ItemBuilder itemBuilder = new ItemBuilder(XMaterial.OAK_SIGN.parseMaterial());
      itemBuilder.name(plugin.getChatManager().colorRawMessage("&e&lAdd Game Sign"));
      itemBuilder.lore(ChatColor.GRAY + "Target a sign and click this.");
      itemBuilder.lore(ChatColor.DARK_GRAY + "(this will set target sign as game sign)");
      bungeeItem = itemBuilder
          .build();
    } else {
      bungeeItem = new ItemBuilder(Material.BARRIER)
          .name(plugin.getChatManager().colorRawMessage("&c&lAdd Game Sign"))
          .lore(ChatColor.GRAY + "Option disabled with Bungee Cord module.")
          .lore(ChatColor.DARK_GRAY + "Bungee mode is meant to be one arena per server")
          .lore(ChatColor.DARK_GRAY + "If you wish to have multi arena, disable bungee in config!")
          .build();
    }
    plugin.getSetupUtilities().getPagedItemMapOf(SetupUtilities.InventoryStage.PAGED_LOCATIONS).setItem(3, new LocationItem(bungeeItem, event -> {
      if(plugin.getConfigPreferences().getOption("BUNGEEMODE")) {
        return;
      }
      event.getWhoClicked().closeInventory();

      Location location = event.getWhoClicked().getTargetBlock(null, 10).getLocation();
      Block block = location.getBlock();

      if(!(block.getState() instanceof Sign)) {
        event.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cPlease look at sign to add as a game sign!"));
        return;
      }

      if(location.distance(event.getWhoClicked().getWorld().getSpawnLocation()) <= Bukkit.getServer().getSpawnRadius()
          && event.getClick() != ClickType.SHIFT_RIGHT) {
        event.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✖ &cWarning | Server spawn protection is set to &6" + Bukkit.getServer().getSpawnRadius()
            + " &cand sign you want to place is in radius of this protection! &c&lNon opped players won't be able to interact with this sign and can't join the game so."));
        event.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&cYou can ignore this warning and add sign with Shift + Left Click, but for now &c&loperation is cancelled"));
        return;
      }

      plugin.getSignManager().getArenaSigns().add(new ArenaSign((Sign) block.getState(), arena));
      event.getWhoClicked().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("SIGNS_CREATED"));

      List<String> locs = plugin.getSetupUtilities().getConfig().getStringList("instances." + arena.getId() + ".signs");
      locs.add(location.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ() + ",0.0,0.0");
      plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".signs", locs);
      ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
      refreshPagedGui();
    }, event -> {
      switch(event.getAction()) {
        case LEFT_CLICK_BLOCK:
          Location location = event.getClickedBlock().getLocation();
          Block block = location.getBlock();
          if(!(block.getState() instanceof Sign)) {
            event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cPlease only use location where already is a sign to add it as a game sign!"));
            return;
          }

          if(location.distance(event.getClickedBlock().getWorld().getSpawnLocation()) <= Bukkit.getServer().getSpawnRadius()) {
            event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✖ &cWarning | Server spawn protection is set to &6" + Bukkit.getServer().getSpawnRadius()
                + " &cand sign you want to place is in radius of this protection! &c&lNon opped players won't be able to interact with this sign and can't join the game so."));
          }

          plugin.getSignManager().getArenaSigns().add(new ArenaSign((Sign) block.getState(), arena));
          event.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("SIGNS_CREATED"));

          List<String> locs = plugin.getSetupUtilities().getConfig().getStringList("instances." + arena.getId() + ".signs");
          locs.add(location.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ() + ",0.0,0.0");
          plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".signs", locs);
          ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
          plugin.getSignManager().updateSigns();
          refreshPagedGui();
          break;
        case RIGHT_CLICK_BLOCK:
          Location locationRemoval = event.getClickedBlock().getLocation();
          Block sign = locationRemoval.getBlock();
          if(!(sign.getState() instanceof Sign)) {
            event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cPlease only use location where already is a sign to add it as a game sign!"));
            return;
          }
          plugin.getSignManager().getArenaSigns().remove(new ArenaSign((Sign) sign.getState(), arena));
          event.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorRawMessage("&e✔ Removed | &c&lGame Sign got removed | You can now remove the sign!"));

          List<String> locations = plugin.getSetupUtilities().getConfig().getStringList("instances." + arena.getId() + ".signs");
          locations.remove(locationRemoval.getWorld().getName() + "," + sign.getX() + "," + sign.getY() + "," + sign.getZ() + ",0.0,0.0");
          plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".signs", locations);
          ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
          plugin.getSignManager().updateSigns();
          refreshPagedGui();
          break;
        case LEFT_CLICK_AIR:
        case RIGHT_CLICK_AIR:
          event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cYou can't use a location that is at your player location, please select the sign!"));
          break;
        default:
          break;
      }
    }, true, true, false));


    plugin.getSetupUtilities().getPagedItemMapOf(SetupUtilities.InventoryStage.PAGED_LOCATIONS).setItem(10, new LocationItem(new ItemBuilder(Material.LAPIS_BLOCK)
        .name(plugin.getChatManager().colorRawMessage("&e&lSet Lobby Location"))
        .lore(ChatColor.GRAY + "Click to set the lobby location")
        .lore(ChatColor.GRAY + "on the place where you are standing")
        .lore("", plugin.getSetupUtilities().isOptionDoneBool("lobbylocation", this))
        .build(), event -> {
      String serializedLocation = event.getWhoClicked().getLocation().getWorld().getName() + "," + event.getWhoClicked().getLocation().getX() + "," + event.getWhoClicked().getLocation().getY() + ","
          + event.getWhoClicked().getLocation().getZ() + "," + event.getWhoClicked().getLocation().getYaw() + ",0.0";

      event.getWhoClicked().closeInventory();
      plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".lobbylocation", serializedLocation);
      arena.setLobbyLocation(event.getWhoClicked().getLocation());
      event.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aLobby location for arena " + arena.getId() + " set at your location!"));
      ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
      refreshPagedGui();
    }, event -> {
      switch(event.getAction()) {
        case LEFT_CLICK_AIR:
          String serializedLocation = event.getPlayer().getLocation().getWorld().getName() + "," + event.getPlayer().getLocation().getX() + "," + event.getPlayer().getLocation().getY() + ","
              + event.getPlayer().getLocation().getZ() + "," + event.getPlayer().getLocation().getYaw() + ",0.0";

          plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".lobbylocation", serializedLocation);
          arena.setLobbyLocation(event.getPlayer().getLocation());
          event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aLobby location for arena " + arena.getId() + " set at your location!"));
          event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease keep in mind to use blocks instead of player location for precise coordinates!"));
          ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
          refreshPagedGui();
          break;
        case RIGHT_CLICK_BLOCK:
        case RIGHT_CLICK_AIR:
          plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".lobbylocation", null);
          arena.setLobbyLocation(null);
          arena.setReady(false);
          event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Removed | &aLobby location for arena " + arena.getId() + "!"));
          ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
          refreshPagedGui();
          break;
        case LEFT_CLICK_BLOCK:
          String serializedBlockLocation = event.getClickedBlock().getLocation().getWorld().getName() + "," + event.getClickedBlock().getLocation().getX() + "," + event.getClickedBlock().getLocation().getY() + 1 + ","
              + event.getClickedBlock().getLocation().getZ() + "," + event.getClickedBlock().getLocation().getYaw() + ",0.0";

          plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".lobbylocation", serializedBlockLocation);
          arena.setLobbyLocation(new Location(event.getClickedBlock().getWorld(), event.getClickedBlock().getX(), event.getClickedBlock().getY() + 1, event.getClickedBlock().getZ()));
          event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aLobby location for arena " + arena.getId() + " set at your location!"));
          event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease keep in mind to use blocks instead of player location for precise coordinates!"));
          ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
          refreshPagedGui();
          break;
      }
    }, true, true, false));


    plugin.getSetupUtilities().getPagedItemMapOf(SetupUtilities.InventoryStage.PAGED_LOCATIONS).setItem(19, new LocationItem(new ItemBuilder(Material.EMERALD_BLOCK)
        .name(plugin.getChatManager().colorRawMessage("&e&lSet Starting Location"))
        .lore(ChatColor.GRAY + "Click to SET the starting location")
        .lore(ChatColor.GRAY + "on the place where you are standing.")
        .lore(ChatColor.DARK_GRAY + "(locations where players will be")
        .lore(ChatColor.DARK_GRAY + "teleported when game starts)")
        .lore("", plugin.getSetupUtilities().isOptionDoneBool("startlocation", this))
        .build(), event -> {
      String serializedLocation = event.getWhoClicked().getLocation().getWorld().getName() + "," + event.getWhoClicked().getLocation().getX() + "," + event.getWhoClicked().getLocation().getY() + ","
          + event.getWhoClicked().getLocation().getZ() + "," + event.getWhoClicked().getLocation().getYaw() + ",0.0";

      event.getWhoClicked().closeInventory();
      plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".startlocation", serializedLocation);
      arena.setStartLocation(event.getWhoClicked().getLocation());
      event.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aStarting location for arena " + arena.getId() + " set at your location!"));
      ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
      refreshPagedGui();
    }, event -> {
      switch(event.getAction()) {
        case LEFT_CLICK_AIR:
          String serializedLocation = event.getPlayer().getLocation().getWorld().getName() + "," + event.getPlayer().getLocation().getX() + "," + event.getPlayer().getLocation().getY() + ","
              + event.getPlayer().getLocation().getZ() + "," + event.getPlayer().getLocation().getYaw() + ",0.0";

          plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".startlocation", serializedLocation);
          arena.setStartLocation(event.getPlayer().getLocation());
          event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aStarting location for arena " + arena.getId() + " set at your location!"));
          event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease keep in mind to use blocks instead of player location for precise coordinates!"));
          ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
          refreshPagedGui();
          break;
        case RIGHT_CLICK_BLOCK:
        case RIGHT_CLICK_AIR:
          plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".startlocation", null);
          arena.setStartLocation(null);
          arena.setReady(false);
          event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Removed | &aStarting location for arena " + arena.getId() + "!"));
          ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
          refreshPagedGui();
          break;
        case LEFT_CLICK_BLOCK:
          String serializedBlockLocation = event.getClickedBlock().getLocation().getWorld().getName() + "," + event.getClickedBlock().getLocation().getX() + "," + event.getClickedBlock().getLocation().getY() + 1 + ","
              + event.getClickedBlock().getLocation().getZ() + "," + event.getClickedBlock().getLocation().getYaw() + ",0.0";

          plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".startlocation", serializedBlockLocation);
          arena.setStartLocation(new Location(event.getClickedBlock().getWorld(), event.getClickedBlock().getX(), event.getClickedBlock().getY() + 1, event.getClickedBlock().getZ()));
          event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aStarting location for arena " + arena.getId() + " set at your location!"));
          event.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease keep in mind to use blocks instead of player location for precise coordinates!"));
          ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
          refreshPagedGui();
          break;
      }
    }, true, true, false));
  }

  public Player getPlayer() {
    return player;
  }

  public PluginArena getArena() {
    return arena;
  }

  public PluginMain getPlugin() {
    return plugin;
  }

  public void openSetupGui() {
    plugin.getSetupUtilities().getSetupGUI().refresh();
    plugin.getSetupUtilities().getSetupGUI().open(player);
    plugin.getSetupUtilities().getSetupGUI().refresh();
  }

  public void openPagedGui() {
    this.refreshPagedGui();
    plugin.getSetupUtilities().getPagedGUI().open(player);
    plugin.getSetupUtilities().sendProTip(player);
  }

  public void openPagedGuiStage(SetupUtilities.InventoryStage inventoryStage) {
    ItemMap itemMap = plugin.getSetupUtilities().getPagedItemMapOf(inventoryStage);
    int page = plugin.getSetupUtilities().getPageGUIPageOf(itemMap);
    plugin.getSetupUtilities().getPagedGUI().open(player);
    plugin.getSetupUtilities().getPagedGUI().setCurrentPage(page);
    refreshPagedGui();
    plugin.getSetupUtilities().sendProTip(player);
  }

  private void refreshPagedGui() {
    plugin.getSetupUtilities().getPagedGUI().refresh();
    plugin.getSetupUtilities().sendProTip(player);
  }

}
