package plugily.projects.minigamesbox.api.handlers.language;

import plugily.projects.minigamesbox.api.utils.services.locale.ILocale;

import java.util.List;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface ILanguageManager {
  void setupLocale();

  boolean isDefaultLanguageUsed();

  String getLanguageMessage(String path);

  List<String> getLanguageList(String path);

  List<String> getLanguageListFromKey(String key);

  void reloadLanguage();

  ILocale getPluginLocale();
}
