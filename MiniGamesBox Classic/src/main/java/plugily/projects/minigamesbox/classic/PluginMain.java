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

package plugily.projects.minigamesbox.classic;

import fr.mrmicky.fastinv.FastInvManager;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.TestOnly;
import plugily.projects.minigamesbox.classic.api.StatsStorage;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.PluginArenaEvents;
import plugily.projects.minigamesbox.classic.arena.PluginArenaManager;
import plugily.projects.minigamesbox.classic.arena.PluginArenaRegistry;
import plugily.projects.minigamesbox.classic.arena.PluginArenaUtils;
import plugily.projects.minigamesbox.classic.arena.managers.BungeeManager;
import plugily.projects.minigamesbox.classic.arena.options.ArenaOptionManager;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.events.ChatEvents;
import plugily.projects.minigamesbox.classic.events.CycleEvents;
import plugily.projects.minigamesbox.classic.events.Events;
import plugily.projects.minigamesbox.classic.events.JoinEvent;
import plugily.projects.minigamesbox.classic.events.LobbyEvents;
import plugily.projects.minigamesbox.classic.events.QuitEvent;
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
import plugily.projects.minigamesbox.classic.handlers.setup.PluginSetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupUtilities;
import plugily.projects.minigamesbox.classic.handlers.sign.SignManager;
import plugily.projects.minigamesbox.classic.kits.KitMenuHandler;
import plugily.projects.minigamesbox.classic.kits.KitRegistry;
import plugily.projects.minigamesbox.classic.preferences.ConfigPreferences;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.user.UserManager;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.helper.BukkitHelper;
import plugily.projects.minigamesbox.classic.utils.hologram.HologramManager;
import plugily.projects.minigamesbox.classic.utils.items.ItemManager;
import plugily.projects.minigamesbox.classic.utils.misc.Debugger;
import plugily.projects.minigamesbox.classic.utils.misc.MessageUtils;
import plugily.projects.minigamesbox.classic.utils.misc.MiscUtils;
import plugily.projects.minigamesbox.classic.utils.services.ServiceRegistry;
import plugily.projects.minigamesbox.classic.utils.services.UpdateChecker;
import plugily.projects.minigamesbox.classic.utils.services.exception.ExceptionLogHandler;
import plugily.projects.minigamesbox.classic.utils.services.metrics.Metrics;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.events.EventsInitializer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 12.09.2021
 */
public class PluginMain extends JavaPlugin {

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
  private FileConfiguration languageConfig;
  private FileConfiguration internalData;
  private PluginArenaRegistry arenaRegistry;
  private KitRegistry kitRegistry;
  private MessageManager messageManager;
  private LanguageManager languageManager;
  private PluginArgumentsRegistry argumentsRegistry;
  private PluginArenaManager arenaManager;
  private Metrics metrics;
  private SpectatorItemsManager spectatorItemsManager;
  private SetupUtilities setupUtilities;

  @TestOnly
  public PluginMain() {
    super();
  }

  @TestOnly
  protected PluginMain(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
    super(loader, description, dataFolder, file);
  }

  @Override
  public void onEnable() {
    long start = System.currentTimeMillis();

    //run file creation
    saveDefaultConfig();

    //check debug mode
    debugger = new Debugger(this, getDescription().getVersion().contains("debug") || getConfig().getBoolean("Debug"));
    exceptionLogHandler = new ExceptionLogHandler(this);
    messageUtils = new MessageUtils(this);

    //checking startup
    if(!validateIfPluginShouldStart()) {
      return;
    }

    debugger.debug("[System] [Core] Initialization start");
    if(getDescription().getVersion().contains("debug") || getConfig().getBoolean("Developer-Mode")) {
      debugger.deepDebug(true);
      debugger.debug(Level.FINE, "Deep debug enabled");
      getConfig().getStringList("Performance-Listenable").forEach(debugger::monitorPerformance);
    }

    setupFiles();

    if(!ServiceRegistry.registerService(this)) {
      debugger.sendConsoleMsg(pluginMessagePrefix + "&cSadly, we can't connect to Plugily Projects Services. Some functions may won't work. e.g. Translations, Automatic Error Report");
    }

    configPreferences = new ConfigPreferences(this);


    if(!new File(getDataFolder(), "internal/data.yml").exists()) {
      new File(getDataFolder().getName() + "/internal").mkdir();
    }
    internalData = ConfigUtils.getConfig(this, "/internal/data");
    //check for updates
    checkUpdate(internalData.getInt("Plugin.Id.Spigot", 0));

    //start metrics
    setupPluginMetrics(internalData.getInt("Plugin.Id.BStats", 0));

    //set command prefixes
    pluginNamePrefix = internalData.getString("Plugin.Name.Short", getName().toLowerCase());
    pluginNamePrefixLong = internalData.getString("Plugin.Name.Long", getName().toLowerCase());

    //setup InvManager
    FastInvManager.register(this);

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
    MessageBuilder.init(this);
    TitleBuilder.init(this);
    languageConfig = ConfigUtils.getConfig(this, "language");
    bukkitHelper = new BukkitHelper(this);
    partyHandler = new PartySupportInitializer().initialize(this);
    userManager = new UserManager(this);
    placeholderManager = new PlaceholderManager(this);
    statsStorage = new StatsStorage(this);
    new EventsInitializer(this);

    specialItemManager = new SpecialItemManager(this);
    new SpecialItemEvent(this);
    kitMenuHandler = new KitMenuHandler(this);
    kitRegistry = new KitRegistry(this);
    rewardsHandler = new RewardsFactory(this);
    hologramManager = new HologramManager(this);
    powerupRegistry = new PowerupRegistry(this);
    holidayManager = new HolidayManager(this);

    permissionsManager = new PermissionsManager(this);
    User.init(this);
    User.cooldownHandlerTask();
    if(configPreferences.getOption("BUNGEEMODE")) {
      debugger.debug("Bungee enabled");
      bungeeManager = new BungeeManager(this);
      new BungeeEvents(this);
    }

    new PluginArenaEvents(this);
    new SpectatorEvents(this);
    new JoinEvent(this);
    new QuitEvent(this);
    new ChatEvents(this);
    new Events(this);
    new LobbyEvents(this);
    spectatorItemsManager = new SpectatorItemsManager(this);

    //arena
    arenaOptionManager = new ArenaOptionManager(this);

    signManager = new SignManager(this);

    setupUtilities = new SetupUtilities(this);
    PluginArenaUtils.init(this);
    PluginArena.init(this);
    if(configPreferences.getOption("LEADERBOARDS")) {
      if(!new File(getDataFolder(), "internal/leaderboards_data.yml").exists()) {
        new File(getDataFolder().getName() + "/internal").mkdir();
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
      messageUtils.thisVersionIsNotSupported();
      debugger.sendConsoleMsg(pluginMessagePrefix + "&cYour server software is not supported by " + getDescription().getName() + "!");
      debugger.sendConsoleMsg(pluginMessagePrefix + "&cWe support Spigot and Spigot forks only! Shutting off...");
      forceDisable = true;
      getServer().getPluginManager().disablePlugin(this);
      return false;
    }
    if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_8_R3)) {
      messageUtils.thisVersionIsNotSupported();
      debugger.sendConsoleMsg(pluginMessagePrefix + "&cYour server version is not supported by " + getDescription().getName() + "!");
      debugger.sendConsoleMsg(pluginMessagePrefix + "&cSadly, we must shut off. Maybe you consider changing your server version?");
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
    if(fileNames.contains(filename)) {
      throw new IllegalStateException("Filename " + filename + " already on the list!");
    }
    fileNames.add(filename);
    setupFiles();
  }

  public void setupFiles() {
    for(String fileName : fileNames) {
      File file = new File(getDataFolder(), fileName + ".yml");
      if(!file.exists()) {
        saveResource(fileName + ".yml", false);
      }
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
    debugger.debug("System disable initialized");
    long start = System.currentTimeMillis();

    Bukkit.getLogger().removeHandler(exceptionLogHandler);
    if(arenaRegistry != null) {
      for(PluginArena arena : arenaRegistry.getArenas()) {
        for(Player player : arena.getPlayers()) {
          arenaManager.leaveAttempt(player, arena);
        }
        arena.getMapRestorerManager().fullyRestoreArena();
      }
    }
    if(userManager != null) {
      userManager.getDatabase().disable();
    }
    if(configPreferences != null && leaderboardRegistry != null && configPreferences.getOption("LEADERBOARDS")) {
      leaderboardRegistry.disableHolograms();
    }
    if(hologramManager != null) {
      for(ArmorStand armorStand : hologramManager.getArmorStands()) {
        armorStand.remove();
        armorStand.setCustomNameVisible(false);
      }
      hologramManager.getArmorStands().clear();
    }
    debugger.debug(pluginMessagePrefix + "System disable finished took {0}ms", System.currentTimeMillis() - start);
  }


  public String getPluginMessagePrefix() {
    return pluginMessagePrefix;
  }

  public Debugger getDebugger() {
    return debugger;
  }

  public ConfigPreferences getConfigPreferences() {
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

  public UserManager getUserManager() {
    return userManager;
  }

  public StatsStorage getStatsStorage() {
    return statsStorage;
  }

  public BukkitHelper getBukkitHelper() {
    return bukkitHelper;
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

  public String getPluginNamePrefix() {
    return pluginNamePrefix;
  }

  public String getPluginNamePrefixLong() {
    return pluginNamePrefixLong;
  }

  public String getCommandAdminPrefix() {
    return pluginNamePrefix + "a";
  }

  public String getCommandAdminPrefixLong() {
    return pluginNamePrefixLong + "admin";
  }

  public PluginArenaRegistry getArenaRegistry() {
    return arenaRegistry;
  }

  public KitRegistry getKitRegistry() {
    return kitRegistry;
  }

  public MessageManager getMessageManager() {
    return messageManager;
  }

  public LanguageManager getLanguageManager() {
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

  public SetupUtilities getSetupUtilities() {
    return setupUtilities;
  }

  public PluginSetupInventory openSetupInventory(PluginArena arena, Player player) {
    return new PluginSetupInventory(this, arena, player);
  }

  public PluginSetupInventory openSetupInventory(PluginArena arena, Player player, SetupUtilities.InventoryStage inventoryStage) {
    return new PluginSetupInventory(this, arena, player, inventoryStage);
  }
}
