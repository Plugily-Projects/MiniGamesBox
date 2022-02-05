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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.items.EmptyItem;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.common.item.ItemMap;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class SetupUtilities {

  private final PluginMain plugin;
  private final FileConfiguration config;
  private static final Random random = new Random();
  private Map<HumanEntity, PluginArena> setupInventories = new HashMap<>();
  public final String VIDEO_LINK = "https://tutorial.plugily.xyz/";

  public SetupUtilities(PluginMain plugin) {
    this.plugin = plugin;
    this.config = ConfigUtils.getConfig(plugin, "arenas");
  }

  public void addSetupInventory(HumanEntity humanEntity, PluginArena arena) {
    setupInventories.put(humanEntity, arena);
  }

  public PluginArena getArena(HumanEntity humanEntity) {
    return setupInventories.get(humanEntity);
  }

  public void removeSetupInventory(HumanEntity humanEntity) {
    setupInventories.remove(humanEntity);
  }

  public String isOptionDone(String path, PluginSetupInventory setupInventory) {
    String option = config.getString("instances." + setupInventory.getArena().getId() + "." + path);

    if(option != null) {
      return color("&a&l✔ Completed &7(value: &8" + option + "&7)");
    }

    return color("&c&l✘ Not Completed");
  }

  public String isOptionDoneSection(String path, int minimum, PluginSetupInventory setupInventory) {
    ConfigurationSection section = config.getConfigurationSection("instances." + setupInventory.getArena().getId() + "." + path);

    if(section != null) {
      int keysSize = section.getKeys(false).size();

      if(keysSize < minimum) {
        return color("&c&l✘ Not Completed | &cPlease add more locations");
      }

      return color("&a&l✔ Completed &7(value: &8" + keysSize + "&7)");
    }

    return color("&c&l✘ Not Completed");
  }

  public String isOptionDoneBool(String path, PluginSetupInventory setupInventory) {
    String option = config.getString("instances." + setupInventory.getArena().getId() + "." + path);

    if(option != null) {
      if(Bukkit.getServer().getWorlds().get(0).getSpawnLocation().equals(LocationSerializer.getLocation(option))) {
        return color("&c&l✘ Not Completed");
      }

      return color("&a&l✔ Completed");
    }

    return color("&c&l✘ Not Completed");
  }

  public int getMinimumValueHigherThanZero(String path, PluginSetupInventory setupInventory) {
    int amount = config.getInt("instances." + setupInventory.getArena().getId() + "." + path);
    return amount == 0 ? 1 : amount;
  }

  private String color(String msg) {
    return new MessageBuilder(msg).build();
  }


  public enum InventoryStage {
    SETUP_GUI("setup"), ARENA_LIST("arena_list"), PAGED_GUI("arena_editor"), PAGED_LOCATIONS("locations"), PAGED_VALUES("values"), PAGED_COUNTABLE("countable"), PAGED_BOOLEAN("boolean");

    private final String tutorial;

    InventoryStage(String tutorial) {
      this.tutorial = tutorial;
    }

    public String getTutorial() {
      return tutorial;
    }
  }


  public void sendProTip(HumanEntity entity) {
    switch(random.nextInt(35)) {
      case 0:
        new MessageBuilder("&e&lTIP: &7We also got premade setups, check them out on &8https://wiki.plugily.xyz/" + plugin.getPluginNamePrefixLong().toLowerCase() + "/setup/maps").send(entity);
        break;
      case 1:
        new MessageBuilder("&e&lTIP: &7Help us translating plugin to your language here: https://translate.plugily.xyz").send(entity);
        break;
      case 2:
        new MessageBuilder("&e&lTIP: &7PlaceholderApi plugin is supported with our plugin! Check here: https://wiki.plugily.xyz/" + plugin.getPluginNamePrefixLong().toLowerCase() + "/placeholders/placeholderapi").send(entity);
        break;
      case 3:
        new MessageBuilder("&e&lTIP: &7Achievements, custom kits and replay ability are things available in our paid addon for this minigame: https://patreon.com/plugily").send(entity);
        break;
      case 4:
        new MessageBuilder("&e&lTIP: &7We are open source! You can always help us by contributing! Check https://github.com/Plugily-Projects/").send(entity);
        break;
      case 5:
        new MessageBuilder("&e&lTIP: &7Need help? Check wiki &8https://wiki.plugily.xyz/" + plugin.getPluginNamePrefixLong().toLowerCase() + " &7or discord https://discord.plugily.xyz").send(entity);
        break;
      case 6:
        new MessageBuilder("&e&lTIP: &7If you like our plugins: You can support us on patreon https://patreon.com/plugily").send(entity);
        break;
      case 7:
        new MessageBuilder("&e&lTIP: &7Suggest new ideas for the plugin or vote on current ones! https://app.feedbacky.net/b/" + plugin.getPluginNamePrefixLong().toLowerCase()).send(entity);
        break;
      default:
        break;
    }
  }

  public void setDefaultItems(PluginSetupInventory pluginSetupInventory, NormalFastInv normalFastInv, ItemStack border, ItemStack left_corner, Consumer<InventoryClickEvent> left_corner_event, ItemStack right_corner, Consumer<InventoryClickEvent> right_corner_event) {
    normalFastInv.setItem(0, new EmptyItem(border));
    normalFastInv.setItem(8, new EmptyItem(border));

    normalFastInv.setItem(9, new EmptyItem(border));
    normalFastInv.setItem(17, new EmptyItem(border));

    normalFastInv.setItem(18, new EmptyItem(border));
    normalFastInv.setItem(26, new EmptyItem(border));

    normalFastInv.setItem(27, new EmptyItem(border));
    normalFastInv.setItem(35, new EmptyItem(border));

    normalFastInv.setItem(36, new EmptyItem(border));
    normalFastInv.setItem(44, new EmptyItem(border));

    normalFastInv.setItem(45, ClickableItem.of(left_corner, left_corner_event));

    normalFastInv.setItem(48, ClickableItem.of(new ItemBuilder(XMaterial.GOLD_INGOT.parseItem())
        .name(new MessageBuilder("&6&l► Patreon Addon ◄ &8(AD)").build())
        .lore(ChatColor.GRAY + "Enhance gameplay with paid addon!")
        .lore(ChatColor.GOLD + "Selection of features of the addon:")
        .lore(ChatColor.GOLD + "Custom Kits, Achievements, Replay Ability")
        .lore(ChatColor.GRAY + "Click to get link for patron program!")
        .enchantment(Enchantment.KNOCKBACK, 5)
        .build(), e -> {
      e.getWhoClicked().closeInventory();
      new MessageBuilder("&6Check patron program here: https://wiki.plugily.xyz/" + plugin.getPluginNamePrefixLong().toLowerCase() + "/addon/overview").prefix().send(e.getWhoClicked());
    }));
    if(pluginSetupInventory.getArena() != null) {
      normalFastInv.setItem(49, new EmptyItem(new ItemBuilder(XMaterial.ANVIL.parseMaterial()).name("&aCurrently editing: " + pluginSetupInventory.getArena().getId()).colorizeItem().build()));
    }
    normalFastInv.setItem(50, ClickableItem.of(new ItemBuilder(XMaterial.FILLED_MAP.parseItem())
        .name(new MessageBuilder("&e&lView Setup Video").build())
        .lore(ChatColor.GRAY + "Having problems with setup or wanna")
        .lore(ChatColor.GRAY + "know some useful tips? Click to get video link!")
        .build(), event -> {
      event.getWhoClicked().closeInventory();
      new MessageBuilder("&6Check out this video: " + VIDEO_LINK + pluginSetupInventory.getInventoryStage().getTutorial()).prefix().send(event.getWhoClicked());
    }));

    normalFastInv.setItem(53, ClickableItem.of(right_corner, right_corner_event));

    normalFastInv.setDefaultItem(new EmptyItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()));
    normalFastInv.refresh();
  }

  public void setDefaultItems(PluginSetupInventory pluginSetupInventory, ItemMap itemMap, XMaterial border, XMaterial left_corner, Consumer<InventoryClickEvent> left_corner_event, XMaterial right_corner, Consumer<InventoryClickEvent> right_corner_event) {
    itemMap.setItem(0, new EmptyItem(border.parseItem()));
    itemMap.setItem(8, new EmptyItem(border.parseItem()));

    itemMap.setItem(9, new EmptyItem(border.parseItem()));
    itemMap.setItem(17, new EmptyItem(border.parseItem()));

    itemMap.setItem(18, new EmptyItem(border.parseItem()));
    itemMap.setItem(26, new EmptyItem(border.parseItem()));

    itemMap.setItem(27, new EmptyItem(border.parseItem()));
    itemMap.setItem(35, new EmptyItem(border.parseItem()));

    itemMap.setItem(36, new EmptyItem(border.parseItem()));
    itemMap.setItem(44, new EmptyItem(border.parseItem()));

    itemMap.setItem(45, ClickableItem.of(left_corner.parseItem(), left_corner_event));

    itemMap.setItem(48, ClickableItem.of(new ItemBuilder(XMaterial.GOLD_INGOT.parseItem())
        .name(new MessageBuilder("&6&l► Patreon Addon ◄ &8(AD)").build())
        .lore(ChatColor.GRAY + "Enhance gameplay with paid addon!")
        .lore(ChatColor.GOLD + "Selection of features of the addon:")
        .lore(ChatColor.GOLD + "Custom Kits, Achievements, Replay Ability")
        .lore(ChatColor.GRAY + "Click to get link for patron program!")
        .enchantment(Enchantment.DURABILITY)
        .build(), event -> {
      event.getWhoClicked().closeInventory();
      new MessageBuilder("&6Check patron program here: https://wiki.plugily.xyz/" + plugin.getPluginNamePrefixLong().toLowerCase() + "/addon/overview").prefix().send(event.getWhoClicked());
    }));

    itemMap.setItem(50, ClickableItem.of(new ItemBuilder(XMaterial.FILLED_MAP.parseItem())
        .name(new MessageBuilder("&e&lView Setup Video").build())
        .lore(ChatColor.GRAY + "Having problems with setup or wanna")
        .lore(ChatColor.GRAY + "know some useful tips? Click to get video link!")
        .build(), event -> {
      event.getWhoClicked().closeInventory();
      new MessageBuilder("&6Check out this video: " + VIDEO_LINK + pluginSetupInventory.getInventoryStage().getTutorial()).prefix().send(event.getWhoClicked());
    }));

    itemMap.setItem(53, ClickableItem.of(right_corner.parseItem(), right_corner_event));

    itemMap.setDefaultItem(new EmptyItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()));
  }

  public PluginArena createInstanceInConfig(String id, String worldName, Player player) {
    if(ConfigUtils.getConfig(plugin, "arenas").contains("instances." + id)) {
      player.sendRawMessage(ChatColor.DARK_RED + "Instance/Arena already exists! Use another ID or delete it first!");
      return null;
    }
    String path = "instances." + id + ".";
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    config.set(path + "mapname", id);
    config.set(path + "isdone", false);
    config.set(path + "world", worldName);
    ConfigUtils.saveConfig(plugin, config, "arenas");

    PluginArena arena = plugin.getArenaRegistry().getNewArena(id);

    arena.setMapName(id);
    arena.setReady(false);

    plugin.getArenaRegistry().registerArena(arena);
    player.sendRawMessage(ChatColor.BOLD + "------------------------------------------");
    player.sendRawMessage(ChatColor.YELLOW + "      Instance " + id + " created!");
    player.sendRawMessage("");
    player.sendRawMessage(ChatColor.GREEN + "Edit this arena via /" + ChatColor.GOLD + plugin.getCommandAdminPrefix() + " setup edit " + id + ChatColor.GREEN + "!");
    player.sendRawMessage(ChatColor.GREEN + "Get full setup inventory via /" + ChatColor.GOLD + plugin.getCommandAdminPrefix() + " setup" + ChatColor.GREEN + "!");
    player.sendRawMessage(ChatColor.GOLD + "Don't know where to start? Check out tutorial video:");
    player.sendRawMessage(ChatColor.GOLD + VIDEO_LINK);
    player.sendRawMessage(ChatColor.BOLD + "-------------------------------------------");
    return arena;
  }

  public String getVideoLink() {
    return VIDEO_LINK;
  }

  public FileConfiguration getConfig() {
    return config;
  }
}
