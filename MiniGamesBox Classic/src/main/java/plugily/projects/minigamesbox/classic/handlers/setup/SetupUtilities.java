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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.conversation.SimpleConversationBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.common.item.ItemMap;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;
import plugily.projects.minigamesbox.inventory.paged.PagedFastInv;

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
  private final PagedFastInv pagedGUI;
  private final NormalFastInv setupGUI;
  private Map<ItemMap, Integer> pagedGUIPages = new HashMap<>();
  private Map<InventoryStage, ItemMap> pagedGUIStages = new HashMap<>();
  private Map<HumanEntity, PluginArena> setupInventories = new HashMap<>();
  public final String VIDEO_LINK = "https://tutorial.plugily.xyz";


  public void addSetupInventory(HumanEntity humanEntity, PluginArena setupInventory) {
    setupInventories.put(humanEntity, setupInventory);
  }

  public boolean getPagedInventory(HumanEntity humanEntity) {
    if(setupInventories.get(humanEntity) == null) {
      return false;
    }
    plugin.getSetupInventory(setupInventories.get(humanEntity), (Player) humanEntity).openPagedGui();
    return true;
  }

  public void removeSetupInventory(HumanEntity humanEntity) {
    setupInventories.remove(humanEntity);
  }

  public SetupUtilities(PluginMain plugin) {
    this.plugin = plugin;
    this.config = ConfigUtils.getConfig(plugin, "arenas");
    this.setupGUI = new NormalFastInv(54, plugin.getPluginMessagePrefix() + "Setup Gui");
    this.pagedGUI = new PagedFastInv(54, plugin.getPluginMessagePrefix() + "Arena Editor");
    prepareSetupGui();
    preparePagedGui();
  }

  private void prepareSetupGui() {
    setupGUI.setItem(19, ClickableItem.of(new ItemBuilder(XMaterial.REDSTONE_BLOCK.parseItem())
            .name(plugin.getChatManager().colorRawMessage("&cArenas List"))
            .lore(ChatColor.GRAY + "Edit, delete or copy arenas")
            .build(), event -> {
          //todo
        }
    ));

    setupGUI.setItem(22, ClickableItem.of(new ItemBuilder(XMaterial.OAK_SIGN.parseItem())
            .name(plugin.getChatManager().colorRawMessage("&cCreate Arena"))
            .lore(ChatColor.GRAY + "Create a fully new arena")
            .build(), event -> {
          new SimpleConversationBuilder(plugin).withPrompt(new StringPrompt() {
            @Override
            public @NotNull String getPromptText(ConversationContext context) {
              return plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&ePlease type in chat arena name to create new arena! You can use color codes. &cType in 'CANCEL' to cancel!");
            }

            @Override
            public Prompt acceptInput(ConversationContext context, String input) {
              String name = plugin.getChatManager().colorRawMessage(input);
              PluginArena arena = createInstanceInConfig(name, event.getWhoClicked().getWorld().getName(), (Player) event.getWhoClicked());
              if(arena == null) {
                return Prompt.END_OF_CONVERSATION;
              }
              plugin.getSetupInventory(arena, (Player) event.getWhoClicked()).openPagedGuiStage(InventoryStage.PAGED_VALUES);
              return Prompt.END_OF_CONVERSATION;
            }
          }).buildFor((Player) event.getWhoClicked());
        }
    ));


    setupGUI.setItem(25, ClickableItem.of(new ItemBuilder(XMaterial.SLIME_BLOCK.parseItem())
            .name(plugin.getChatManager().colorRawMessage("&cContinue Arena Setup"))
            .lore(ChatColor.GRAY + "Create a fully new arena")
            .build(), event -> {
          if(!getPagedInventory(event.getWhoClicked())) {
            event.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "You need to create or edit a arena first"));
          }
        }
    ));


    setupGUI.setItem(39, ClickableItem.of(new ItemBuilder(XMaterial.GOLD_INGOT.parseItem())
        .name(plugin.getChatManager().colorRawMessage("&6&l► Patreon Addon ◄ &8(AD)"))
        .lore(ChatColor.GRAY + "Enhance gameplay with paid addon!")
        .lore(ChatColor.GOLD + "Selection of features of the addon:")
        .lore(ChatColor.GOLD + "Custom Kits, Achievements, Replay Ability")
        .lore(ChatColor.GRAY + "Click to get link for patron program!")
        .build(), event -> {
      event.getWhoClicked().closeInventory();
      event.getWhoClicked().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorRawMessage("&6Check patron program here: https://wiki.plugily.xyz/" + plugin.getPluginNamePrefixLong().toLowerCase() + "/addon/overview"));
    }));

    setupGUI.setItem(41, ClickableItem.of(new ItemBuilder(XMaterial.FILLED_MAP.parseItem())
        .name(plugin.getChatManager().colorRawMessage("&e&lView Setup Video"))
        .lore(ChatColor.GRAY + "Having problems with setup or wanna")
        .lore(ChatColor.GRAY + "know some useful tips? Click to get video link!")
        .build(), event -> {
      event.getWhoClicked().closeInventory();
      event.getWhoClicked().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorRawMessage("&6Check out this video: " + VIDEO_LINK + InventoryStage.SETUP_GUI.getTutorial()));
    }));

    setupGUI.setDefaultItem(ClickableItem.of(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()));
    setupGUI.refresh();
  }


  private void preparePagedGui() {
    ItemMap locationItems = pagedGUI.createNewPage();
    Consumer<InventoryClickEvent> locationItemsLeft = event -> {
      event.getWhoClicked().closeInventory();
      setupGUI.open(event.getWhoClicked());
    };
    Consumer<InventoryClickEvent> locationItemsRight = event -> {
      pagedGUI.setCurrentPage(getPagedGUI().getCurrentPage() + 1);
      pagedGUI.refresh();
    };
    setDefaultItems(locationItems, XMaterial.ORANGE_STAINED_GLASS_PANE, XMaterial.GRAY_STAINED_GLASS_PANE, locationItemsLeft, XMaterial.BLUE_STAINED_GLASS_PANE, locationItemsRight);

    pagedGUIPages.put(locationItems, 0);
    pagedGUIStages.put(InventoryStage.PAGED_LOCATIONS, locationItems);

    ItemMap valueItems = pagedGUI.createNewPage();
    Consumer<InventoryClickEvent> valueItemsLeft = event -> {
      pagedGUI.setCurrentPage(getPagedGUI().getCurrentPage() - 1);
      pagedGUI.refresh();
    };
    Consumer<InventoryClickEvent> valueItemsRight = event -> {
      pagedGUI.setCurrentPage(getPagedGUI().getCurrentPage() + 1);
      pagedGUI.refresh();
    };
    setDefaultItems(valueItems, XMaterial.BLUE_STAINED_GLASS_PANE, XMaterial.GRAY_STAINED_GLASS_PANE, valueItemsLeft, XMaterial.WHITE_STAINED_GLASS_PANE, valueItemsRight);

    pagedGUIPages.put(valueItems, 1);
    pagedGUIStages.put(InventoryStage.PAGED_VALUES, valueItems);


    ItemMap countableItems = pagedGUI.createNewPage();
    Consumer<InventoryClickEvent> countableItemsLeft = event -> {
      pagedGUI.setCurrentPage(getPagedGUI().getCurrentPage() - 1);
      pagedGUI.refresh();
    };
    Consumer<InventoryClickEvent> countableItemsRight = event -> {
      pagedGUI.setCurrentPage(getPagedGUI().getCurrentPage() + 1);
      pagedGUI.refresh();
    };
    setDefaultItems(countableItems, XMaterial.BLUE_STAINED_GLASS_PANE, XMaterial.GRAY_STAINED_GLASS_PANE, countableItemsLeft, XMaterial.WHITE_STAINED_GLASS_PANE, countableItemsRight);

    pagedGUIPages.put(countableItems, 2);
    pagedGUIStages.put(InventoryStage.PAGED_COUNTABLE, countableItems);


    ItemMap booleanItems = pagedGUI.createNewPage();
    Consumer<InventoryClickEvent> booleanItemsLeft = event -> {
      pagedGUI.setCurrentPage(getPagedGUI().getCurrentPage() - 1);
      pagedGUI.refresh();
    };
    Consumer<InventoryClickEvent> booleanItemsRight = event -> {
      event.getWhoClicked().closeInventory();
      setupGUI.open(event.getWhoClicked());
    };
    setDefaultItems(booleanItems, XMaterial.BLUE_STAINED_GLASS_PANE, XMaterial.GRAY_STAINED_GLASS_PANE, booleanItemsLeft, XMaterial.WHITE_STAINED_GLASS_PANE, booleanItemsRight);

    pagedGUIPages.put(booleanItems, 3);
    pagedGUIStages.put(InventoryStage.PAGED_BOOLEAN, booleanItems);
    pagedGUI.refresh();
  }


  public String isOptionDone(String path, PluginSetupInventory setupInventory) {
    String option = config.getString("instances." + setupInventory.getArena().getId() + "." + path);

    if(option != null) {
      return color("&a&l✔ Completed &7(value: &8" + option + "&7)");
    }

    return color("&c&l✘ Not Completed");
  }

  public String isOptionDoneSection(String path, int minimum, PluginSetupInventory setupInventory) {
    org.bukkit.configuration.ConfigurationSection section = config.getConfigurationSection("instances." + setupInventory.getArena().getId() + "." + path);

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
    return plugin.getChatManager().colorRawMessage(msg);
  }


  public PagedFastInv getPagedGUI() {
    return pagedGUI;
  }

  public NormalFastInv getSetupGUI() {
    return setupGUI;
  }

  public InventoryStage getStageOf(ItemMap itemMap) {
    for(Map.Entry<InventoryStage, ItemMap> pair : pagedGUIStages.entrySet()) {
      if(pair.getValue() == itemMap) {
        return pair.getKey();
      }
    }
    return InventoryStage.PAGED_GUI;
  }

  public ItemMap getPagedItemMapOf(InventoryStage inventoryStage) {
    return pagedGUIStages.get(inventoryStage);
  }

  public int getPageGUIPageOf(ItemMap itemMap) {
    return pagedGUIPages.get(itemMap);
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
    switch(random.nextInt(30)) {
      case 0:
        entity.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7We also got premade setups, check them out on &8https://wiki.plugily.xyz/" + plugin.getPluginNamePrefixLong().toLowerCase() + "/setup/maps"));
        break;
      case 1:
        entity.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Help us translating plugin to your language here: https://translate.plugily.xyz"));
        break;
      case 2:
        entity.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7PlaceholderApi plugin is supported with our plugin! Check here: https://wiki.plugily.xyz/" + plugin.getPluginNamePrefixLong().toLowerCase() + "/placeholders/placeholderapi"));
        break;
      case 3:
        entity.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Achievements, custom kits and replay ability are things available in our paid addon for this minigame: https://patreon.com/plugily"));
        break;
      case 4:
        entity.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7We are open source! You can always help us by contributing! Check https://github.com/Plugily-Projects/"));
        break;
      case 5:
        entity.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Need help? Check wiki &8https://wiki.plugily.xyz/" + plugin.getPluginNamePrefixLong().toLowerCase() + " &7or discord https://discord.plugily.xyz"));
        break;
      case 6:
        entity.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7If you like our plugins: You can support us on patreon https://patreon.com/plugily"));
        break;
      case 7:
        entity.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Suggest new ideas for the plugin or vote on current ones! https://app.feedbacky.net/b/" + plugin.getPluginNamePrefixLong().toLowerCase()));
        break;
      default:
        break;
    }
  }

  public void setDefaultItems(ItemMap itemMap, XMaterial border, XMaterial left_corner, Consumer<InventoryClickEvent> left_corner_event, XMaterial right_corner, Consumer<InventoryClickEvent> right_corner_event) {
    itemMap.setItem(0, ClickableItem.of(border.parseItem()));
    itemMap.setItem(8, ClickableItem.of(border.parseItem()));

    itemMap.setItem(9, ClickableItem.of(border.parseItem()));
    itemMap.setItem(17, ClickableItem.of(border.parseItem()));

    itemMap.setItem(18, ClickableItem.of(border.parseItem()));
    itemMap.setItem(26, ClickableItem.of(border.parseItem()));

    itemMap.setItem(27, ClickableItem.of(border.parseItem()));
    itemMap.setItem(35, ClickableItem.of(border.parseItem()));

    itemMap.setItem(36, ClickableItem.of(border.parseItem()));
    itemMap.setItem(44, ClickableItem.of(border.parseItem()));

    itemMap.setItem(45, ClickableItem.of(left_corner.parseItem(), left_corner_event));

    setupGUI.setItem(48, ClickableItem.of(new ItemBuilder(XMaterial.GOLD_INGOT.parseItem())
        .name(plugin.getChatManager().colorRawMessage("&6&l► Patreon Addon ◄ &8(AD)"))
        .lore(ChatColor.GRAY + "Enhance gameplay with paid addon!")
        .lore(ChatColor.GOLD + "Selection of features of the addon:")
        .lore(ChatColor.GOLD + "Custom Kits, Achievements, Replay Ability")
        .lore(ChatColor.GRAY + "Click to get link for patron program!")
        .enchantment(Enchantment.DURABILITY)
        .build(), e -> {
      e.getWhoClicked().closeInventory();
      e.getWhoClicked().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorRawMessage("&6Check patron program here: https://wiki.plugily.xyz/" + plugin.getPluginNamePrefixLong().toLowerCase() + "/addon/overview"));
    }));

    setupGUI.setItem(50, ClickableItem.of(new ItemBuilder(XMaterial.FILLED_MAP.parseItem())
        .name(plugin.getChatManager().colorRawMessage("&e&lView Setup Video"))
        .lore(ChatColor.GRAY + "Having problems with setup or wanna")
        .lore(ChatColor.GRAY + "know some useful tips? Click to get video link!")
        .build(), event -> {
      event.getWhoClicked().closeInventory();
      event.getWhoClicked().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorRawMessage("&6Check out this video: " + VIDEO_LINK + getStageOf(itemMap).getTutorial()));
    }));

    itemMap.setItem(53, ClickableItem.of(right_corner.parseItem(), right_corner_event));

    itemMap.setDefaultItem(ClickableItem.of(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()));
  }

  public PluginArena createInstanceInConfig(String id, String worldName, Player player) {
    if(ConfigUtils.getConfig(plugin, "arenas").contains("instances." + id)) {
      player.sendMessage(ChatColor.DARK_RED + "Instance/Arena already exists! Use another ID or delete it first!");
      return null;
    }
    String path = "instances." + id + ".";
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    config.set(path + "mapname", id);
    config.set(path + "isdone", false);
    config.set(path + "world", worldName);
    ConfigUtils.saveConfig(plugin, config, "arenas");

    PluginArena arena = new PluginArena(id);

    arena.setMapName(config.getString(path + "mapname"));
    arena.setReady(false);

    plugin.getArenaRegistry().registerArena(arena);
    player.sendMessage(ChatColor.BOLD + "------------------------------------------");
    player.sendMessage(ChatColor.YELLOW + "      Instance " + id + " created!");
    player.sendMessage("");
    player.sendMessage(ChatColor.GREEN + "Edit this arena via " + ChatColor.GOLD + plugin.getPluginNamePrefix() + " edit" + id + ChatColor.GREEN + "!");
    player.sendMessage(ChatColor.GREEN + "Get full setup inventory via " + ChatColor.GOLD + plugin.getPluginNamePrefix() + " setup" + ChatColor.GREEN + "!");
    player.sendMessage(ChatColor.GOLD + "Don't know where to start? Check out tutorial video:");
    player.sendMessage(ChatColor.GOLD + VIDEO_LINK);
    player.sendMessage(ChatColor.BOLD + "-------------------------------------------");
    return arena;
  }

  public String getVideoLink() {
    return VIDEO_LINK;
  }

  public FileConfiguration getConfig() {
    return config;
  }
}
