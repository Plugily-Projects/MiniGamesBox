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


package plugily.projects.minigamesbox.classic.handlers.language;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.services.ServiceRegistry;
import plugily.projects.minigamesbox.classic.utils.services.locale.Locale;
import plugily.projects.minigamesbox.classic.utils.services.locale.LocaleRegistry;
import plugily.projects.minigamesbox.classic.utils.services.locale.LocaleService;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class LanguageManager {

  private final PluginMain plugin;
  private Locale pluginLocale;
  private FileConfiguration localeFile;
  private FileConfiguration languageConfig;
  private boolean messagesIntegrityPassed = true;
  private FileConfiguration defaultLanguageConfig;

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
    registerLocales();
    setupLocale();
    if(isDefaultLanguageUsed()) {
      validateMessagesIntegrity();
    }
  }

  private void validateMessagesIntegrity() {
    for(Message message : plugin.getMessageManager().getAllMessages().values()) {
      if(languageConfig.isSet(message.getPath())) {
        continue;
      }
      plugin.getDebugger().sendConsoleMsg("&cLanguage file integrity check failed! Message "
          + message.getPath() + " not found! It will be set to default value of ERR_MSG_" + message.getPath() + "_NOT_FOUND");
      languageConfig.set(message.getPath(), "ERR_MSG_" + message.getPath() + "_NOT_FOUND");
      messagesIntegrityPassed = false;
    }
    if(!messagesIntegrityPassed) {
      ConfigUtils.saveConfig(plugin, languageConfig, "language");
    }
  }

  private void registerLocales() {
    //todo after import into poeditor!
    //BB + Basque, Belarusian, Estonian, Greek, Japanese, Slovenian, Ukrainian
    //BB - Thai
    //MM + Afrikaans, Danish, Japanese, Slovak, Ukrainian
    //MM -
    //TB +
    //TB - Chinese (Traditional), Chinese (Simplified), Indonesian, Korean, Lithuanian, Thai, Turkish, Vietnamese
    Arrays.asList(new Locale("Chinese (Traditional)", "简体中文", "zh_HK", "POEditor contributors", Arrays.asList("中文(傳統)", "中國傳統", "chinese_traditional", "zh")),
            new Locale("Chinese (Simplified)", "简体中文", "zh_CN", "POEditor contributors", Arrays.asList("简体中文", "中文", "chinese", "chinese_simplified", "cn")),
            new Locale("Czech", "Český", "cs_CZ", "POEditor contributors", Arrays.asList("czech", "cesky", "český", "cs")),
            new Locale("Dutch", "Nederlands", "nl_NL", "POEditor contributors", Arrays.asList("dutch", "nederlands", "nl")),
            new Locale("English", "English", "en_GB", "Plajer", Arrays.asList("default", "english", "en")),
            new Locale("French", "Français", "fr_FR", "POEditor contributors", Arrays.asList("french", "francais", "français", "fr")),
            new Locale("German", "Deutsch", "de_DE", "Tigerkatze and POEditor contributors", Arrays.asList("deutsch", "german", "de")),
            new Locale("Hungarian", "Magyar", "hu_HU", "POEditor contributors", Arrays.asList("hungarian", "magyar", "hu")),
            new Locale("Indonesian", "Indonesia", "id_ID", "POEditor contributors", Arrays.asList("indonesian", "indonesia", "id")),
            new Locale("Italian", "Italiano", "it_IT", "POEditor contributors", Arrays.asList("italian", "italiano", "it")),
            new Locale("Korean", "한국의", "ko_KR", "POEditor contributors", Arrays.asList("korean", "한국의", "kr")),
            new Locale("Lithuanian", "Lietuviešu", "lt_LT", "POEditor contributors", Arrays.asList("lithuanian", "lietuviešu", "lietuviesu", "lt")),
            new Locale("Polish", "Polski", "pl_PL", "Plajer", Arrays.asList("polish", "polski", "pl")),
            new Locale("Portuguese (BR)", "Português Brasileiro", "pt_BR", "POEditor contributors", Arrays.asList("brazilian", "brasil", "brasileiro", "pt-br", "pt_br")),
            new Locale("Romanian", "Românesc", "ro_RO", "POEditor contributors", Arrays.asList("romanian", "romanesc", "românesc", "ro")),
            new Locale("Russian", "Pусский", "ru_RU", "POEditor contributors", Arrays.asList("russian", "pусский", "pyccknn", "russkiy", "ru")),
            new Locale("Spanish", "Español", "es_ES", "POEditor contributors", Arrays.asList("spanish", "espanol", "español", "es")),
            new Locale("Thai", "Thai", "th_TH", "POEditor contributors", Arrays.asList("thai", "th")),
            new Locale("Turkish", "Türk", "tr_TR", "POEditor contributors", Arrays.asList("turkish", "turk", "türk", "tr")),
            new Locale("Vietnamese", "Việt", "vn_VN", "POEditor contributors", Arrays.asList("vietnamese", "viet", "việt", "vn")))
        .forEach(LocaleRegistry::registerLocale);
  }

  private void loadLocaleFile() {
    if(isDefaultLanguageUsed()) {
      return;
    }
    LocaleService service = ServiceRegistry.getLocaleService(plugin);
    if(service == null) {
      plugin.getDebugger().sendConsoleMsg("&cLocales cannot be downloaded because API website is unreachable, locales will be disabled.");
      pluginLocale = LocaleRegistry.getByName("English");
      return;
    }
    if(service.isValidVersion()) {
      LocaleService.DownloadStatus status = service.demandLocaleDownload(pluginLocale);
      if(status == LocaleService.DownloadStatus.FAIL) {
        pluginLocale = LocaleRegistry.getByName("English");
        plugin.getDebugger().sendConsoleMsg("&cLocale service couldn't download latest locale for plugin! English locale will be used instead!");
        return;
      } else if(status == LocaleService.DownloadStatus.SUCCESS) {
        plugin.getDebugger().sendConsoleMsg("&aDownloaded locale " + pluginLocale.getPrefix() + " properly!");
      } else if(status == LocaleService.DownloadStatus.LATEST) {
        plugin.getDebugger().sendConsoleMsg("&aLocale " + pluginLocale.getPrefix() + " is latest! Awesome!");
      }
    } else {
      pluginLocale = LocaleRegistry.getByName("English");
      plugin.getDebugger().sendConsoleMsg("&cYour plugin version is too old to use latest locale! Please update plugin to access latest updates of locale!");
      return;
    }

    File file = new File(plugin.getDataFolder() + "/locales/"
        + pluginLocale.getPrefix() + ".yml");
    if(!file.exists()) {
      plugin.getLogger().log(Level.WARNING, "Failed to load localization file for locale " + pluginLocale.getPrefix() + "! Using English instead");
      plugin.getLogger().log(Level.WARNING, "Cause: " + "File does not exists");
      pluginLocale = LocaleRegistry.getByName("English");
      return;
    }
    file = new File(plugin.getDataFolder() + "/locales/", pluginLocale.getPrefix() + ".yml");
    YamlConfiguration config = new YamlConfiguration();
    try {
      config.load(file);
    } catch(InvalidConfigurationException | IOException ex) {
      plugin.getLogger().log(Level.WARNING, "Failed to load localization file for locale " + pluginLocale.getPrefix() + "! Using English instead");
      plugin.getLogger().log(Level.WARNING, "Cause: " + ex.getMessage());
      pluginLocale = LocaleRegistry.getByName("English");
      return;
    }
    localeFile = config;
  }

  private void setupLocale() {
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
      plugin.getDebugger().sendConsoleMsg("&cPlugin locale is invalid! Using default one...");
      pluginLocale = LocaleRegistry.getByName("English");
    }
    /* is beta release */
    if((plugin.getDescription().getVersion().contains("locales") || plugin.getDescription().getVersion().contains("pre")) && !plugin.getConfig().getBoolean("Developer-Mode", false)) {
      plugin.getDebugger().sendConsoleMsg("&cLocales aren't supported in beta versions because they're lacking latest translations! Enabling English one...");
      pluginLocale = LocaleRegistry.getByName("English");
      return;
    }
    plugin.getDebugger().sendConsoleMsg("&aLoaded locale " + pluginLocale.getName() + " (" + pluginLocale.getOriginalName() + " ID: "
        + pluginLocale.getPrefix() + ") by " + pluginLocale.getAuthor());
    loadLocaleFile();
  }

  public boolean isDefaultLanguageUsed() {
    return "English".equals(pluginLocale.getName());
  }

  public String getLanguageMessage(String path) {
    if(isDefaultLanguageUsed()) {
      return getString(path);
    }
    String prop = localeFile.getString(path);
    if(prop == null) {
      return getString(path);
    }
    if(getString(path).equalsIgnoreCase(defaultLanguageConfig.getString(path, "not found"))) {
      return prop;
    }
    return getString(path);
  }

  public List<String> getLanguageList(String path) {
    if(isDefaultLanguageUsed()) {
      return getStrings(path);
    }
    String prop = localeFile.getString(path);
    if(prop == null) {
      return getStrings(path);
    }
    if(getString(path).equalsIgnoreCase(defaultLanguageConfig.getString(path, "not found"))) {
      return Arrays.asList(plugin.getChatManager().colorRawMessage(prop).split(";"));
    }
    return getStrings(path);
  }


  private List<String> getStrings(String path) {
    if(!languageConfig.isSet(path)) {
      plugin.getDebugger().sendConsoleMsg("&cGame message not found in your locale!");
      plugin.getDebugger().sendConsoleMsg("&cPlease regenerate your language.yml file! If error still occurs report it to the developer on discord!");
      plugin.getDebugger().sendConsoleMsg("&cPath: " + path);
      return Collections.singletonList("ERR_MESSAGE_" + path + "_NOT_FOUND");
    }
    return languageConfig.getStringList(path).stream().map(string -> plugin.getChatManager().colorRawMessage(string)).collect(Collectors.toList());
  }


  private String getString(String path) {
    if(!languageConfig.isSet(path)) {
      plugin.getDebugger().sendConsoleMsg("&cGame message not found in your locale!");
      plugin.getDebugger().sendConsoleMsg("&cPlease regenerate your language.yml file! If error still occurs report it to the developer on discord!");
      plugin.getDebugger().sendConsoleMsg("&cPath: " + path);
      return "ERR_MESSAGE_" + path + "_NOT_FOUND";
    }
    return languageConfig.getString(path, "not found");
  }

  public void reloadLanguage() {
    languageConfig = ConfigUtils.getConfig(plugin, "language");
  }

  public Locale getPluginLocale() {
    return pluginLocale;
  }
}
