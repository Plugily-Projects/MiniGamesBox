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

package plugily.projects.minigamesbox.classic.utils.services.locale;

import java.util.List;

/**
 * Class for locales
 *
 * @since 1.2.0
 */
public class Locale {

  /* LANGUAGES ALREADY IN USE
todo Locale outsource to server side getting of locales to avoid need of updates to plugin if new language got translated!

        new Locale("Chinese (Traditional)", "简体中文", "zh_HK", "POEditor contributors", Arrays.asList("中文(傳統)", "中國傳統", "chinese_traditional", "zh"));
        new Locale("Chinese (Simplified)", "简体中文", "zh_CN", "POEditor contributors", Arrays.asList("简体中文", "中文", "chinese", "chinese_simplified", "cn"));
        new Locale("Czech", "Český", "cs_CZ", "POEditor contributors", Arrays.asList("czech", "cesky", "český", "cs"));
        new Locale("Dutch", "Nederlands", "nl_NL", "POEditor contributors", Arrays.asList("dutch", "nederlands", "nl"));
        new Locale("English", "English", "en_GB", "Plajer", Arrays.asList("default", "english", "en"));
        new Locale("French", "Français", "fr_FR", "POEditor contributors", Arrays.asList("french", "francais", "français", "fr"));
        new Locale("German", "Deutsch", "de_DE", "Tigerkatze and POEditor contributors", Arrays.asList("deutsch", "german", "de"));
        new Locale("Hungarian", "Magyar", "hu_HU", "POEditor contributors", Arrays.asList("hungarian", "magyar", "hu"));
        new Locale("Indonesian", "Indonesia", "id_ID", "POEditor contributors", Arrays.asList("indonesian", "indonesia", "id"));
        new Locale("Italian", "Italiano", "it_IT", "POEditor contributors", Arrays.asList("italian", "italiano", "it"));
        new Locale("Korean", "한국의", "ko_KR", "POEditor contributors", Arrays.asList("korean", "한국의", "kr"));
        new Locale("Lithuanian", "Lietuviešu", "lt_LT", "POEditor contributors", Arrays.asList("lithuanian", "lietuviešu", "lietuviesu", "lt"));
        new Locale("Polish", "Polski", "pl_PL", "Plajer", Arrays.asList("polish", "polski", "pl"));
        new Locale("Portuguese (BR)", "Português Brasileiro", "pt_BR", "POEditor contributors", Arrays.asList("brazilian", "brasil", "brasileiro", "pt-br", "pt_br"));
        new Locale("Romanian", "Românesc", "ro_RO", "POEditor contributors", Arrays.asList("romanian", "romanesc", "românesc", "ro"));
        new Locale("Russian", "Pусский", "ru_RU", "POEditor contributors", Arrays.asList("russian", "pусский", "pyccknn", "russkiy", "ru"));
        new Locale("Spanish", "Español", "es_ES", "POEditor contributors", Arrays.asList("spanish", "espanol", "español", "es"));
        new Locale("Thai", "Thai", "th_TH", "POEditor contributors", Arrays.asList("thai", "th"));
        new Locale("Turkish", "Türk", "tr_TR", "POEditor contributors", Arrays.asList("turkish", "turk", "türk", "tr"));
        new Locale("Vietnamese", "Việt", "vn_VN", "POEditor contributors", Arrays.asList("vietnamese", "viet", "việt", "vn"));
   */

  private final String name;
  private final String originalName;
  private final String prefix;
  private final String author;
  private final List<String> aliases;

  public Locale(String name, String originalName, String prefix, String author, List<String> aliases) {
    this.prefix = prefix;
    this.name = name;
    this.originalName = originalName;
    this.author = author;
    this.aliases = aliases;
  }

  /**
   * Gets name of locale, ex. English or German
   *
   * @return name of locale
   */
  public String getName() {
    return name;
  }

  /**
   * Gets original name of locale ex. for German it will return Deutsch, Polish returns Polski etc.
   *
   * @return name of locale in its language
   */
  public String getOriginalName() {
    return originalName;
  }

  /**
   * @return authors of locale
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Language code ex. en_GB, de_DE, pl_PL etc.
   *
   * @return language code of locale
   */
  public String getPrefix() {
    return prefix;
  }

  /**
   * Valid aliases of locale ex. for German - deutsch, de, german; Polish - polski, pl, polish etc.
   *
   * @return aliases for locale
   */
  public List<String> getAliases() {
    return aliases;
  }

}
