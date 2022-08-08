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

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.inventories.ArenaEditorInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.inventories.ArenaListInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.inventories.HomeInventory;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.classic.utils.version.TextComponentBuilder;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.06.2022
 */
public class SetupInventory {
  //ToDo Recommend player setup settings, survival and fly active
  private SetupInventoryUtils.SetupInventoryStage inventoryStage;
  private String arenaKey = null;
  private final Player player;
  private final PluginMain plugin;

  public SetupInventory(PluginMain plugin, Player player) {
    this.plugin = plugin;
    this.player = player;
    this.inventoryStage = SetupInventoryUtils.SetupInventoryStage.HOME;
  }

  public SetupInventory(PluginMain plugin, Player player, String arenaKey) {
    this.plugin = plugin;
    this.player = player;
    this.arenaKey = arenaKey;
    this.inventoryStage = SetupInventoryUtils.SetupInventoryStage.ARENA_EDITOR;
  }

  public SetupInventory(PluginMain plugin, Player player, String arenaKey, SetupInventoryUtils.SetupInventoryStage inventoryStage) {
    this.plugin = plugin;
    this.player = player;
    this.arenaKey = arenaKey;
    this.inventoryStage = inventoryStage;
  }

  public void open() {
    switch(inventoryStage) {
      default:
      case HOME:
        new HomeInventory(54, plugin.getPluginMessagePrefix() + "Setup Menu", this).open(player);
        break;
      case ARENA_LIST:
        if(plugin.getArenaRegistry().getArenas().isEmpty()) {
          new MessageBuilder("&cThere are no arenas. Create one first!").prefix().send(player);
          return;
        }
        new ArenaListInventory(54, plugin.getPluginMessagePrefix() + "Setup Menu | Arenas", this).open(player);
        break;
      case ARENA_EDITOR:
        new ArenaEditorInventory(54, plugin.getPluginMessagePrefix() + "Arena Editor Menu", this).open(player);
        break;
    }
    sendProTip(player);
  }

  public void open(SetupInventoryUtils.SetupInventoryStage inventoryStage) {
    setInventoryStage(inventoryStage);
    open();
  }

  public void setInventoryStage(SetupInventoryUtils.SetupInventoryStage inventoryStage) {
    this.inventoryStage = inventoryStage;
  }

  public SetupInventoryUtils.SetupInventoryStage getInventoryStage() {
    return inventoryStage;
  }

  public void setArenaKey(String arenaKey) {
    SetupInventoryUtils.addSetupInventory(player, arenaKey);
    this.arenaKey = arenaKey;
  }

  public String getArenaKey() {
    String newKey = SetupInventoryUtils.getArenaKey(player);
    if(arenaKey == null && newKey != null) {
      return newKey;
    }
    return arenaKey;
  }

  public PluginMain getPlugin() {
    return plugin;
  }

  public FileConfiguration getConfig() {
    return ConfigUtils.getConfig(plugin, "arenas");
  }

  public void setConfig(String keyName, Object value) {
    FileConfiguration arenasFile = getConfig();
    arenasFile.set("instances." + getArenaKey() + "." + keyName, value);
    ConfigUtils.saveConfig(getPlugin(), arenasFile, "arenas");
  }


  public String isOptionDone(String path) {
    Object option = getConfig().get("instances." + getArenaKey() + "." + path);

    if(option != null) {
      return "&a&l✔ Completed &7(value: &8" + option + "&7)";
    }

    return "&c&l✘ Not Completed | Reason: No data";
  }

  public String isSectionOptionDone(String path, int minimum) {
    ConfigurationSection section = getConfig().getConfigurationSection("instances." + getArenaKey() + "." + path);
    if(minimum == 0) {
      return "&e&l✔ Optional";
    }
    if(section != null) {
      int keysSize = section.getKeys(false).size();

      if(keysSize < minimum) {
        return "&c&l✘ Not Completed | &cPlease add more locations";
      }

      return "&a&l✔ Completed &7(value: &8" + keysSize + "&7)";
    }

    return "&c&l✘ Not Completed | Reason: No data";
  }

  public String isLocationOptionDone(String path) {
    String option = getConfig().getString("instances." + getArenaKey() + "." + path);
    if(option != null) {
      Location location = LocationSerializer.getLocation(option);
      if(location != null) {
        return "&a&l✔ Completed &7(value: &8" + option + "&7)";
      }
    }
    return "&c&l✘ Not Completed | Reason: No data";
  }

  public int getMinimumValue(String path) {
    int amount = getConfig().getInt("instances." + getArenaKey() + "." + path, 1);
    return amount == 0 ? 1 : amount;
  }

  public void sendProTip(HumanEntity entity) {
    switch(plugin.getRandom().nextInt(35)) {
      case 0:
        new MessageBuilder("&e&lTIP: &7We also got premade setups, check them out on &8https://wiki.plugily.xyz/" + plugin.getPluginNamePrefixLong().toLowerCase() + "/setup/maps", false).send(entity);
        break;
      case 1:
        new MessageBuilder("&e&lTIP: &7Help us translating plugin to your language here: https://translate.plugily.xyz", false).send(entity);
        break;
      case 2:
        new MessageBuilder("&e&lTIP: &7PlaceholderApi plugin is supported with our plugin! Check here: https://wiki.plugily.xyz/" + plugin.getPluginNamePrefixLong().toLowerCase() + "/placeholders/placeholderapi", false).send(entity);
        break;
      case 3:
        new MessageBuilder("&e&lTIP: &7Achievements, custom kits and replay ability are things available in our paid addon for this minigame: https://patreon.com/plugily", false).send(entity);
        break;
      case 4:
        new MessageBuilder("&e&lTIP: &7We are open source! You can always help us by contributing! Check https://github.com/Plugily-Projects/", false).send(entity);
        break;
      case 5:
        new MessageBuilder("&e&lTIP: &7Need help? Check wiki &8https://wiki.plugily.xyz/" + plugin.getPluginNamePrefixLong().toLowerCase() + " &7or discord https://discord.plugily.xyz", false).send(entity);
        break;
      case 6:
        new MessageBuilder("&e&lTIP: &7If you like our plugins: You can support us on patreon https://patreon.com/plugily", false).send(entity);
        break;
      case 7:
        new MessageBuilder("&e&lTIP: &7Suggest new ideas for the plugin or vote on current ones! https://app.feedbacky.net/b/" + plugin.getPluginNamePrefixLong().toLowerCase(), false).send(entity);
        break;
      default:
        break;
    }
  }

  public PluginArena createInstanceInConfig(String id, Player player) {
    if(ConfigUtils.getConfig(plugin, "arenas").contains("instances." + id)) {
      player.sendRawMessage(ChatColor.DARK_RED + "Instance/Arena already exists! Use another ID or delete it first!");
      return null;
    }
    String path = "instances." + id + ".";
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    config.set(path + "isdone", false);
    ConfigUtils.saveConfig(plugin, config, "arenas");

    PluginArena arena = plugin.getArenaRegistry().getNewArena(id);

    arena.setReady(false);


    player.sendRawMessage(ChatColor.BOLD + "------------------------------------------");
    player.sendRawMessage(new MessageBuilder("      &eInstance &6" + id + " &ecreated!").build());
    player.sendRawMessage("");
    new TextComponentBuilder("&aEdit this arena via &7/" + plugin.getCommandAdminPrefix() + " setup edit " + id).player(player)
        .setHoverEvent(HoverEvent.Action.SHOW_TEXT, "/" + plugin.getCommandAdminPrefix() + " setup edit " + id)
        .setClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + plugin.getCommandAdminPrefix() + " setup edit " + id)
        .sendPlayer();
    player.sendRawMessage("");
    new TextComponentBuilder("&aEnter Setup Inventory via &7/" + plugin.getCommandAdminPrefix() + " setup").player(player)
        .setHoverEvent(HoverEvent.Action.SHOW_TEXT, "/" + plugin.getCommandAdminPrefix() + " setup")
        .setClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + plugin.getCommandAdminPrefix() + " setup")
        .sendPlayer();
    player.sendRawMessage("");
    player.sendRawMessage(ChatColor.GOLD + "Don't know where to start? Check out the tutorial video at");
    player.sendRawMessage(ChatColor.GRAY + TUTORIAL_SITE + getPlugin().getPluginNamePrefixLong());
    player.sendRawMessage(ChatColor.BOLD + "-------------------------------------------");
    return arena;
  }

  public final String TUTORIAL_SITE = "https://tutorial.plugily.xyz/";

  public String getTutorialSite() {
    return TUTORIAL_SITE;
  }
}
