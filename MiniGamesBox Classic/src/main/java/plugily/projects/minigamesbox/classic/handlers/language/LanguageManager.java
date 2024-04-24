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


package plugily.projects.minigamesbox.classic.handlers.language;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import plugily.projects.minigamesbox.api.handlers.language.ILanguageManager;
import plugily.projects.minigamesbox.api.utils.services.locale.ILocale;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.services.ServiceRegistry;
import plugily.projects.minigamesbox.classic.utils.services.locale.Locale;
import plugily.projects.minigamesbox.classic.utils.services.locale.LocaleRegistry;
import plugily.projects.minigamesbox.classic.utils.services.locale.LocaleService;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class LanguageManager implements ILanguageManager {

  private final PluginMain plugin;
  private ILocale pluginLocale;
  private FileConfiguration localeFile;
  private FileConfiguration languageConfig;
  private boolean messagesIntegrityPassed = true;
  private final FileConfiguration defaultLanguageConfig;

  /**
   * Initializes language management system
   * Executes language migration if needed
   *
   * @param plugin plugin instance
   * @see LanguageMigrator
   */
  public LanguageManager(PluginMain plugin) {
    this.plugin = plugin;
    if(!new File(plugin.getDataFolder() + File.separator + "language.yml").exists()) {
      plugin.saveResource("language.yml", false);
    }
    //auto update
    plugin.saveResource("locales/language_default.yml", true);

    new LanguageMigrator(plugin);
    languageConfig = ConfigUtils.getConfig(plugin, "language");
    defaultLanguageConfig = ConfigUtils.getConfig(plugin, "locales/language_default");
    setupLocale();
    if(isDefaultLanguageUsed()) {
      validateMessagesIntegrity();
    }
  }

  private void validateMessagesIntegrity() {
    for(Message message : plugin.getMessageManager().getAllMessages().values()) {
      if(!message.isProtected()) {
        continue;
      }
      if(languageConfig.isSet(message.getPath())) {
        continue;
      }
      plugin.getDebugger().debug(Level.WARNING, "&cLanguage file integrity check failed! Message "
          + message.getPath() + " not found! It will be set to default value of ERR_MSG_" + message.getPath() + "_NOT_FOUND");
      languageConfig.set(message.getPath(), "ERR_MSG_" + message.getPath() + "_NOT_FOUND");
      messagesIntegrityPassed = false;
    }
    plugin.getDebugger().debug("Message integrity passed for language.yml!");
    if(!messagesIntegrityPassed) {
      plugin.getDebugger().debug("Saving language.yml with integrity messages!");
      ConfigUtils.saveConfig(plugin, languageConfig, "language");
    }
  }

  private void loadLocaleFile() {
    if(isDefaultLanguageUsed()) {
      return;
    }
    LocaleService service = ServiceRegistry.getLocaleService(plugin);
    if(service == null) {
      plugin.getDebugger().debug(Level.WARNING, "&cLocales cannot be downloaded because API website is unreachable, locales will be disabled.");
      pluginLocale = LocaleRegistry.getByName("Default");
      return;
    }
    if(service.isValidVersion()) {
      plugin.getDebugger().debug("LocaleService got valid version!");
      LocaleService.DownloadStatus status = service.demandLocaleDownload(pluginLocale);
      if(status == LocaleService.DownloadStatus.FAIL) {
        pluginLocale = LocaleRegistry.getByName("Default");
        plugin.getDebugger().debug(Level.WARNING, "&cLocale service couldn't download latest locale for plugin! English locale will be used instead!");
        return;
      } else if(status == LocaleService.DownloadStatus.SUCCESS) {
        plugin.getDebugger().debug(Level.WARNING, "&aDownloaded locale " + pluginLocale.getPrefix() + " properly!");
      } else if(status == LocaleService.DownloadStatus.LATEST) {
        plugin.getDebugger().debug(Level.WARNING, "&aLocale " + pluginLocale.getPrefix() + " is latest! Awesome!");
      }
    } else {
      pluginLocale = LocaleRegistry.getByName("Default");
      plugin.getDebugger().debug(Level.WARNING, "&cYour plugin version is too old to use latest locale! Please update plugin to access latest updates of locale!");
      return;
    }
    File file = new File(plugin.getDataFolder() + "/locales/"
        + pluginLocale.getPrefix() + ".yml");
    if(!file.exists()) {
      plugin.getDebugger().debug(Level.WARNING, "Failed to load localization file for locale " + pluginLocale.getPrefix() + "! Using English instead");
      plugin.getDebugger().debug(Level.WARNING, "Cause: " + "File does not exists");
      pluginLocale = LocaleRegistry.getByName("Default");
      return;
    }
    file = new File(plugin.getDataFolder() + "/locales/", pluginLocale.getPrefix() + ".yml");
    YamlConfiguration config = new YamlConfiguration();
    try {
      config.load(file);
    } catch(InvalidConfigurationException | IOException ex) {
      plugin.getDebugger().debug(Level.WARNING, "Failed to load localization file for locale " + pluginLocale.getPrefix() + "! Using English instead");
      plugin.getDebugger().debug(Level.WARNING, "Cause: " + ex.getMessage());
      pluginLocale = LocaleRegistry.getByName("Default");
      return;
    }
    localeFile = config;
  }

  @Override
  public void setupLocale() {
    String localeName = plugin.getConfig().getString("locale", "default").toLowerCase();
    for(Locale locale : LocaleRegistry.getRegisteredLocales()) {
      if(locale.getPrefix().equalsIgnoreCase(localeName)) {
        pluginLocale = locale;
        break;
      }
      for(String alias : locale.getAliases()) {
        if(alias.equals(localeName)) {
          pluginLocale = locale;
          break;
        }
      }
    }
    if(pluginLocale == null) {
      plugin.getDebugger().debug(Level.WARNING, "&cPlugin locale is invalid! Using default one...");
      pluginLocale = LocaleRegistry.getByName("Default");
    }
    /* is snapshot release */
    if((plugin.getDescription().getVersion().contains("locales") || plugin.getDescription().getVersion().contains("-SNAPSHOT")) && !plugin.getConfig().getBoolean("Developer-Mode", false)) {
      plugin.getDebugger().debug(Level.WARNING, "Locales aren't supported in this versions because they're lacking latest translations! Enabling English one...");
      pluginLocale = LocaleRegistry.getByName("Default");
      return;
    }
    plugin.getDebugger().debug(Level.WARNING, "&aLoaded locale " + pluginLocale.getName() + " (" + pluginLocale.getOriginalName() + " ID: "
        + pluginLocale.getPrefix() + ") by " + pluginLocale.getAuthor());
    loadLocaleFile();
  }

  @Override
  public boolean isDefaultLanguageUsed() {
    return "Default".equalsIgnoreCase(pluginLocale.getName());
  }

  @Override
  public String getLanguageMessage(String path) {
    if(isDefaultLanguageUsed()) {
      return getString(path);
    }
    String prop = localeFile.getString(path);
    if(prop == null) {
      return getString(path);
    }
    if(getString(path).equalsIgnoreCase(defaultLanguageConfig.getString(path, "_NOT_FOUND"))) {
      return prop;
    }
    return getString(path);
  }

  @Override
  public List<String> getLanguageList(String path) {
    return getLanguageListContent(path);
  }

  @Override
  public List<String> getLanguageListFromKey(String key) {
    String path = plugin.getMessageManager().getPath(key);
    return getLanguageListContent(path);
  }

  private List<String> getLanguageListContent(String path) {
    if(isDefaultLanguageUsed()) {
      return getStringsColoured(path);
    }
    List<String> prop = localeFile.getStringList(path);
    if(prop.isEmpty()) {
      return getStringsColoured(path);
    }
    if(getStrings(path).equals(defaultLanguageConfig.getStringList(path))) {
      return prop.stream().map(string -> new MessageBuilder(string).build()).collect(Collectors.toList());
    }
    return getStringsColoured(path);
  }


  private List<String> getStringsColoured(String path) {
    if(checkLanguageList(path)) return Collections.singletonList("ERR_MESSAGE_" + path + "_NOT_FOUND");
    return languageConfig.getStringList(path).stream().map(string -> new MessageBuilder(string).build()).collect(Collectors.toList());
  }

  private List<String> getStrings(String path) {
    if(checkLanguageList(path)) return Collections.singletonList("ERR_MESSAGE_" + path + "_NOT_FOUND");
    return languageConfig.getStringList(path);
  }

  private boolean checkLanguageList(String path) {
    if(!languageConfig.isSet(path)) {
      plugin.getDebugger().debug(Level.WARNING, "&cGame message not found in your locale!");
      plugin.getDebugger().debug(Level.WARNING, "&cPlease regenerate your language.yml file! If error still occurs report it to the developer on discord!");
      plugin.getDebugger().debug(Level.WARNING, "&cPath: " + path);
      return true;
    }
    return false;
  }


  private String getString(String path) {
    if(!languageConfig.isSet(path)) {
      plugin.getDebugger().debug(Level.WARNING, "&cGame message not found in your locale!");
      plugin.getDebugger().debug(Level.WARNING, "&cPlease regenerate your language.yml file! If error still occurs report it to the developer on discord!");
      plugin.getDebugger().debug(Level.WARNING, "&cPath: " + path);
      return "ERR_MESSAGE_" + path + "_NOT_FOUND";
    }
    return languageConfig.getString(path, "not found");
  }

  @Override
  public void reloadLanguage() {
    languageConfig = ConfigUtils.getConfig(plugin, "language");
  }

  @Override
  public ILocale getPluginLocale() {
    return pluginLocale;
  }
}
