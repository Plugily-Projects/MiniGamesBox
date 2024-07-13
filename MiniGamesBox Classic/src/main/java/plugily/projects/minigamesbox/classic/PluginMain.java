/*
 *  MiniGamesBox - Library box with massive content that could be seen as minigames core.
 *  Copyright (C) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.minigamesbox.classic;

import me.tigerhix.lib.scoreboard.ScoreboardLib;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.TestOnly;
import plugily.projects.minigamesbox.api.IPluginMain;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.api.arena.IPluginArenaRegistry;
import plugily.projects.minigamesbox.api.handlers.language.ILanguageManager;
import plugily.projects.minigamesbox.api.kit.IKitRegistry;
import plugily.projects.minigamesbox.api.preferences.IConfigPreferences;
import plugily.projects.minigamesbox.api.user.IUserManager;
import plugily.projects.minigamesbox.api.utils.misc.IDebugger;
import plugily.projects.minigamesbox.classic.api.StatsStorage;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.PluginArenaManager;
import plugily.projects.minigamesbox.classic.arena.PluginArenaRegistry;
import plugily.projects.minigamesbox.classic.arena.PluginArenaUtils;
import plugily.projects.minigamesbox.classic.arena.managers.BungeeManager;
import plugily.projects.minigamesbox.classic.arena.options.ArenaOptionManager;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.events.*;
import plugily.projects.minigamesbox.classic.events.bungee.BungeeEvents;
import plugily.projects.minigamesbox.classic.events.spectator.SpectatorEvents;
import plugily.projects.minigamesbox.classic.events.spectator.SpectatorItemsManager;
import plugily.projects.minigamesbox.classic.handlers.holiday.HolidayManager;
import plugily.projects.minigamesbox.classic.handlers.hologram.LeaderboardRegistry;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItemEvent;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItemManager;
import plugily.projects.minigamesbox.classic.handlers.language.LanguageManager;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.language.MessageManager;
import plugily.projects.minigamesbox.classic.handlers.language.TitleBuilder;
import plugily.projects.minigamesbox.classic.handlers.party.PartyHandler;
import plugily.projects.minigamesbox.classic.handlers.party.PartySupportInitializer;
import plugily.projects.minigamesbox.classic.handlers.permissions.PermissionsManager;
import plugily.projects.minigamesbox.classic.handlers.placeholder.PlaceholderManager;
import plugily.projects.minigamesbox.classic.handlers.powerup.PowerupRegistry;
import plugily.projects.minigamesbox.classic.handlers.reward.RewardsFactory;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.categories.PluginSetupCategoryManager;
import plugily.projects.minigamesbox.classic.handlers.sign.SignManager;
import plugily.projects.minigamesbox.classic.kits.KitMenuHandler;
import plugily.projects.minigamesbox.classic.kits.KitRegistry;
import plugily.projects.minigamesbox.classic.kits.ability.KitAbilityHandler;
import plugily.projects.minigamesbox.classic.kits.ability.KitAbilityManager;
import plugily.projects.minigamesbox.classic.preferences.ConfigPreferences;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.user.UserManager;
import plugily.projects.minigamesbox.classic.utils.actionbar.ActionBarManager;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.dimensional.CuboidSelector;
import plugily.projects.minigamesbox.classic.utils.engine.JavaScriptEngine;
import plugily.projects.minigamesbox.classic.utils.helper.BukkitHelper;
import plugily.projects.minigamesbox.classic.utils.hologram.HologramManager;
import plugily.projects.minigamesbox.classic.utils.items.ItemManager;
import plugily.projects.minigamesbox.classic.utils.misc.Debugger;
import plugily.projects.minigamesbox.classic.utils.misc.MessageUtils;
import plugily.projects.minigamesbox.classic.utils.misc.MiscUtils;
import plugily.projects.minigamesbox.classic.utils.services.ServiceRegistry;
import plugily.projects.minigamesbox.classic.utils.services.UpdateChecker;
import plugily.projects.minigamesbox.classic.utils.services.exception.ExceptionLogHandler;
import plugily.projects.minigamesbox.classic.utils.services.locale.Locale;
import plugily.projects.minigamesbox.classic.utils.services.locale.LocaleRegistry;
import plugily.projects.minigamesbox.classic.utils.services.metrics.Metrics;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.events.EventsInitializer;
import plugily.projects.minigamesbox.inventory.boot.InventoryManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 12.09.2021
 */
public class PluginMain extends JavaPlugin implements IPluginMain {

  private final String pluginMessagePrefix = "[" + getDescription().getName() + "] ";
  private String pluginNamePrefix;
  private String pluginNamePrefixLong;
  private MessageUtils messageUtils;
  private ConfigPreferences configPreferences;
  private ArenaOptionManager arenaOptionManager;
  private PartyHandler partyHandler;
  private Debugger debugger;
  private UserManager userManager;
  private StatsStorage statsStorage;
  private BukkitHelper bukkitHelper;

  private JavaScriptEngine javaScriptEngine;
  private CuboidSelector cuboidSelector;
  private SpecialItemManager specialItemManager;
  private RewardsFactory rewardsHandler;
  private KitMenuHandler kitMenuHandler;
  private HologramManager hologramManager;
  private SignManager signManager;
  private PowerupRegistry powerupRegistry;
  private LeaderboardRegistry leaderboardRegistry;
  private HolidayManager holidayManager;
  private PlaceholderManager placeholderManager;
  private boolean forceDisable = false;
  private ExceptionLogHandler exceptionLogHandler;
  private PermissionsManager permissionsManager;
  private BungeeManager bungeeManager;
  private ActionBarManager actionBarManager;
  private FileConfiguration languageConfig;
  private FileConfiguration internalData;
  private PluginArenaRegistry arenaRegistry;
  private KitRegistry kitRegistry;
  private MessageManager messageManager;
  private LanguageManager languageManager;
  private KitAbilityManager kitAbilityManager;
  private PluginArgumentsRegistry argumentsRegistry;
  private PluginArenaManager arenaManager;
  private Metrics metrics;
  private SpectatorItemsManager spectatorItemsManager;
  private final Random random = new Random();

  @TestOnly
  public PluginMain() {
    super();
  }

  @Override
  public void onEnable() {
    long start = System.currentTimeMillis();

    //run file creation
    saveDefaultConfig();

    //check debug mode
    debugger = new Debugger(this, getDescription().getVersion().contains("-debug") || getConfig().getBoolean("Debug"));
    exceptionLogHandler = new ExceptionLogHandler(this);
    messageUtils = new MessageUtils(this);

    //checking startup
    if(!validateIfPluginShouldStart()) {
      return;
    }

    debugger.debug("[System] [Core] Initialization start");
    if(getDescription().getVersion().contains("-debug") || getConfig().getBoolean("Developer-Mode")) {
      debugger.deepDebug(true);
      debugger.debug(Level.FINE, "Deep debug enabled");
      getConfig().getStringList("Performance-Listenable").forEach(debugger::monitorPerformance);
      debugger.debug(Level.INFO, "Performance monitoring enabled: " + Arrays.toString(debugger.getListenedPerformance().toArray()));
    }

    setupFiles();
    LocaleRegistry.registerLocale(new Locale("Default", "Default", "default", "Internal Plugin", Arrays.asList("default")));

    if(!ServiceRegistry.registerService(this)) {
      debugger.sendConsoleMsg(pluginMessagePrefix + "&cSadly, we can't connect to Plugily Projects Services. Some functions may won't work. e.g. Translations, Automatic Error Report");
    }

    configPreferences = new ConfigPreferences(this);


    File file = new File(getDataFolder(), "internal/data.yml");
    if(file.delete()) {
      saveDefaultFile("internal/data");
    } else {
      new File(getDataFolder().getName() + "/internal").mkdir();
    }
    internalData = ConfigUtils.getConfig(this, "/internal/data");
    //check for updates
    checkUpdate(internalData.getInt("Plugin.Id.Spigot", 0));

    //start metrics
    setupPluginMetrics(internalData.getInt("Plugin.Id.BStats", 0));

    //set command prefixes
    pluginNamePrefix = internalData.getString("Plugin.Name.Short", getName()).toLowerCase();
    pluginNamePrefixLong = internalData.getString("Plugin.Name.Long", getName()).toLowerCase();

    //setup InvManager
    new InventoryManager(this);

    //setup ItemManager
    ItemManager.register(this);

    //setup Scoreboard
    ScoreboardLib.setPluginInstance(this);

    //initialize default classes
    initializeDefaultClasses();

    //send console message
    MiscUtils.sendStartUpMessage(this);

    //finished initial start
    debugger.debug("[System] [Core] Initialization finished took {0}ms", System.currentTimeMillis() - start);

  }

  public void initializeDefaultClasses() {
    messageManager = new MessageManager(this);
    languageManager = new LanguageManager(this);
    kitAbilityManager = new KitAbilityManager(this);
    MessageBuilder.init(this);
    TitleBuilder.init(this);
    languageConfig = ConfigUtils.getConfig(this, "language");
    actionBarManager = new ActionBarManager(this);
    bukkitHelper = new BukkitHelper(this);
    javaScriptEngine = new JavaScriptEngine(this);
    partyHandler = new PartySupportInitializer().initialize(this);
    kitRegistry = new KitRegistry(this);
    User.init(this);
    User.cooldownHandlerTask();
    userManager = new UserManager(this);
    placeholderManager = new PlaceholderManager(this);
    statsStorage = new StatsStorage(this);
    new EventsInitializer(this);

    specialItemManager = new SpecialItemManager(this);
    new SpecialItemEvent(this);
    kitMenuHandler = new KitMenuHandler(this);
    new KitAbilityHandler(this);
    rewardsHandler = new RewardsFactory(this);
    hologramManager = new HologramManager(this);
    powerupRegistry = new PowerupRegistry(this);
    holidayManager = new HolidayManager(this);

    permissionsManager = new PermissionsManager(this);
    if(configPreferences.getOption("BUNGEEMODE")) {
      debugger.debug("Bungee enabled");
      bungeeManager = new BungeeManager(this);
      new BungeeEvents(this);
    }

    new SpectatorEvents(this);
    new JoinEvent(this);
    new QuitEvent(this);
    new ChatEvents(this);
    new Events(this);
    new LobbyEvents(this);
    spectatorItemsManager = new SpectatorItemsManager(this);
    cuboidSelector = new CuboidSelector(this);
    //arena
    arenaOptionManager = new ArenaOptionManager(this);

    signManager = new SignManager(this);

    PluginArenaUtils.init(this);
    PluginArena.init(this);
    if(configPreferences.getOption("LEADERBOARDS")) {
      if(!new File(getDataFolder(), "internal/leaderboards_data.yml").exists()) {
        new File(getDataFolder() + "/internal").mkdir();
      }
      //running later due to plugin specific stats
      Bukkit.getScheduler().runTaskLater(this, () -> leaderboardRegistry = new LeaderboardRegistry(this), 20L * 15);
    }
    new CycleEvents(this);
  }

  private boolean validateIfPluginShouldStart() {
    try {
      Class.forName("org.spigotmc.SpigotConfig");
    } catch(Exception e) {
      MiscUtils.sendLineBreaker(getName());
      messageUtils.thisVersionIsNotSupported();
      debugger.sendConsoleMsg(pluginMessagePrefix + "&cYour server software is not supported by " + getDescription().getName() + "!");
      debugger.sendConsoleMsg(pluginMessagePrefix + "&cWe support Spigot and Spigot forks only! Shutting off...");
      MiscUtils.sendLineBreaker(getName());
      forceDisable = true;
      getServer().getPluginManager().disablePlugin(this);
      return false;
    }
    if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_8_8)) {
      MiscUtils.sendLineBreaker(getName());
      messageUtils.thisVersionIsNotSupported();
      MiscUtils.sendVersionInformation(this, getName(), getDescription());
      debugger.sendConsoleMsg(pluginMessagePrefix + "&cYour server version is not supported by " + getDescription().getName() + "!");
      debugger.sendConsoleMsg(pluginMessagePrefix + "&cSadly, we must shut off. Maybe you consider changing your server version?");
      MiscUtils.sendLineBreaker(getName());
      forceDisable = true;
      getServer().getPluginManager().disablePlugin(this);
      return false;
    }
    return true;
  }

  // "arena_selector", "leaderboards", "signs"
  private final ArrayList<String> fileNames = new ArrayList<>(Arrays.asList("internal/data", "internal/leaderboards_data", "arenas", "bungee", "rewards", "spectator", "stats", "permissions", "special_items", "mysql"));

  public ArrayList<String> getFileNames() {
    return fileNames;
  }

  //some plugins need to register files such as "kits"
  public void addFileName(String filename) {
    if(getFileNames().contains(filename)) {
      throw new IllegalStateException("Filename " + filename + " already on the list!");
    }
    getFileNames().add(filename);
    setupFiles();
  }

  public void setupFiles() {
    for(String fileName : fileNames) {
      saveDefaultFile(fileName);
    }
  }

  private void saveDefaultFile(String fileName) {
    if(!new File(getDataFolder(), fileName + ".yml").exists()) {
      saveResource(fileName + ".yml", false);
    }
  }

  private void checkUpdate(int pluginUpdateId) {
    if(!getConfig().getBoolean("Update-Notifier.Enabled", true)) {
      return;
    }
    UpdateChecker.init(this, pluginUpdateId).requestUpdateCheck().whenComplete((result, exception) -> {
      if(!result.requiresUpdate()) {
        return;
      }
      if(result.getNewestVersion().contains("b")) {
        if(getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true)) {
          debugger.sendConsoleMsg(pluginMessagePrefix + "Your software is ready for update! However it's a BETA VERSION. Proceed with caution.");
          debugger.sendConsoleMsg(pluginMessagePrefix + "Current version %old%, latest version %new%".replace("%old%", getDescription().getVersion()).replace("%new%",
              result.getNewestVersion()));
        }
        return;
      }
      messageUtils.updateIsHere();
      debugger.sendConsoleMsg(pluginMessagePrefix + "&aYour " + getDescription().getName() + " plugin is outdated! Download it to keep with latest changes and fixes.");
      debugger.sendConsoleMsg(pluginMessagePrefix + "&aDisable this option in config.yml if you wish.");
      debugger.sendConsoleMsg(pluginMessagePrefix + "&eCurrent version: &c" + getDescription().getVersion() + " &eLatest version: &a" + result.getNewestVersion());
    });
  }

  private void setupPluginMetrics(int pluginMetricsId) {
    metrics = new Metrics(this, pluginMetricsId);

    metrics.addCustomChart(new Metrics.SimplePie("database_enabled", () -> String.valueOf(configPreferences
        .getOption("DATABASE"))));
    metrics.addCustomChart(new Metrics.SimplePie("locale_used", () -> languageManager.getPluginLocale().getPrefix()));
    metrics.addCustomChart(new Metrics.SimplePie("bungeecord_hooked", () -> String.valueOf(configPreferences.getOption("BUNGEEMODE"))) {
    });
    metrics.addCustomChart(new Metrics.SimplePie("update_notifier", () -> {
      if(getConfig().getBoolean("Update-Notifier.Enabled", true)) {
        return getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true) ? "Enabled with beta notifier" : "Enabled";
      }

      return getConfig().getBoolean("Update-Notifier.Notify-Beta-Versions", true) ? "Beta notifier only" : "Disabled";
    }));
  }


  @Override
  public void onDisable() {
    if(forceDisable) {
      return;
    }
    getDebugger().debug("System disable initialized");
    long start = System.currentTimeMillis();

    Bukkit.getLogger().removeHandler(getExceptionLogHandler());
    if(getArenaRegistry() != null) {
      try {
        for(IPluginArena arena : getArenaRegistry().getArenas()) {
          getArenaManager().stopGame(true, arena);
          arena.getMapRestorerManager().fullyRestoreArena();
        }
      } catch(Exception exception) {
        getDebugger().debug("Error while disabling arenas: {0}", exception.getMessage());
      }
    }
    if(getUserManager() != null) {
      getUserManager().getDatabase().disable();
    }
    if(getConfigPreferences() != null && getLeaderboardRegistry() != null && getConfigPreferences().getOption("LEADERBOARDS")) {
      getLeaderboardRegistry().disableHolograms();
    }
    if(getHologramManager() != null) {
      for(ArmorStand armorStand : getHologramManager().getArmorStands()) {
        armorStand.remove();
        armorStand.setCustomNameVisible(false);
      }
      getHologramManager().getArmorStands().clear();
    }
    getDebugger().debug(getPluginMessagePrefix() + "System disable finished took {0}ms", System.currentTimeMillis() - start);
  }


  public String getPluginMessagePrefix() {
    return pluginMessagePrefix;
  }

  @Override
  public IDebugger getDebugger() {
    return debugger;
  }

  @Override
  public IConfigPreferences getConfigPreferences() {
    return configPreferences;
  }

  public ArenaOptionManager getArenaOptionManager() {
    return arenaOptionManager;
  }

  public PartyHandler getPartyHandler() {
    return partyHandler;
  }

  public MessageUtils getMessageUtils() {
    return messageUtils;
  }

  @Override
  public IUserManager getUserManager() {
    return userManager;
  }

  public StatsStorage getStatsStorage() {
    return statsStorage;
  }

  public BukkitHelper getBukkitHelper() {
    return bukkitHelper;
  }

  public JavaScriptEngine getJavaScriptEngine() {
    return javaScriptEngine;
  }

  public SpecialItemManager getSpecialItemManager() {
    return specialItemManager;
  }

  public HologramManager getHologramManager() {
    return hologramManager;
  }

  public PowerupRegistry getPowerupRegistry() {
    return powerupRegistry;
  }

  public SignManager getSignManager() {
    return signManager;
  }

  public RewardsFactory getRewardsHandler() {
    return rewardsHandler;
  }

  public LeaderboardRegistry getLeaderboardRegistry() {
    return leaderboardRegistry;
  }

  public HolidayManager getHolidayManager() {
    return holidayManager;
  }

  public ExceptionLogHandler getExceptionLogHandler() {
    return exceptionLogHandler;
  }

  public KitMenuHandler getKitMenuHandler() {
    return kitMenuHandler;
  }

  public PlaceholderManager getPlaceholderManager() {
    return placeholderManager;
  }

  public PermissionsManager getPermissionsManager() {
    return permissionsManager;
  }

  public BungeeManager getBungeeManager() {
    return bungeeManager;
  }

  public FileConfiguration getLanguageConfig() {
    return languageConfig;
  }

  public FileConfiguration getInternalData() {
    return internalData;
  }

  @Override
  public String getPluginNamePrefix() {
    return pluginNamePrefix;
  }

  @Override
  public String getPluginNamePrefixLong() {
    return pluginNamePrefixLong;
  }

  @Override
  public String getCommandAdminPrefix() {
    return pluginNamePrefix + "a";
  }

  @Override
  public String getCommandAdminPrefixLong() {
    return pluginNamePrefixLong + "admin";
  }

  @Override
  public IPluginArenaRegistry getArenaRegistry() {
    return arenaRegistry;
  }

  @Override
  public IKitRegistry getKitRegistry() {
    return kitRegistry;
  }

  public MessageManager getMessageManager() {
    return messageManager;
  }

  @Override
  public ILanguageManager getLanguageManager() {
    return languageManager;
  }

  public PluginArgumentsRegistry getArgumentsRegistry() {
    return argumentsRegistry;
  }

  public PluginArenaManager getArenaManager() {
    return arenaManager;
  }

  public Metrics getMetrics() {
    return metrics;
  }

  public SpectatorItemsManager getSpectatorItemsManager() {
    return spectatorItemsManager;
  }

  public CuboidSelector getCuboidSelector() {
    return cuboidSelector;
  }

  public SetupInventory getSetupInventory(Player player) {
    return new SetupInventory(this, player);
  }

  public SetupInventory getSetupInventory(Player player, String arenaKey) {
    return new SetupInventory(this, player, arenaKey);
  }

  public PluginSetupCategoryManager getSetupCategoryManager(SetupInventory setupInventory) {
    return new PluginSetupCategoryManager(setupInventory);
  }

  public ActionBarManager getActionBarManager() {
    return actionBarManager;
  }

  public Random getRandom() {
    return random;
  }

  public KitAbilityManager getKitAbilityManager() {
    return kitAbilityManager;
  }
}
