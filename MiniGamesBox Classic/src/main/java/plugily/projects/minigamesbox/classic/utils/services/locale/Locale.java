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

import plugily.projects.minigamesbox.api.utils.services.locale.ILocale;

import java.util.List;

/**
 * Class for locales
 *
 * @since 1.2.0
 */
public class Locale implements ILocale {

  /* LANGUAGES USED BEFORE
      new Locale("Hungarian", "Magyar", "hu_HU", "POEditor contributors", Arrays.asList("hungarian", "magyar", "hu"));
        new Locale("Indonesian", "Indonesia", "id_ID", "POEditor contributors", Arrays.asList("indonesian", "indonesia", "id"));
        new Locale("Korean", "한국의", "ko_KR", "POEditor contributors", Arrays.asList("korean", "한국의", "kr"));
        new Locale("Lithuanian", "Lietuviešu", "lt_LT", "POEditor contributors", Arrays.asList("lithuanian", "lietuviešu", "lietuviesu", "lt"));
        new Locale("Romanian", "Românesc", "ro_RO", "POEditor contributors", Arrays.asList("romanian", "romanesc", "românesc", "ro"));
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
   * Retrieves the Locale object matching the specified locale prefix or name.
   *
   * @param  locale  the locale prefix or name to search for
   * @return the Locale object matching the specified locale prefix or name, or null if no match is found
   */
  public static Locale getLocale(String locale) {
    for (Locale l : LocaleRegistry.getRegisteredLocales()) {
      if (l.getPrefix().equals(locale)) {
        return l;
      }
    }
    for (Locale l : LocaleRegistry.getRegisteredLocales()) {
      if (l.getName().equals(locale)) {
        return l;
      }
    }
    return null;
  }


  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getOriginalName() {
    return originalName;
  }

  @Override
  public String getAuthor() {
    return author;
  }

  @Override
  public String getPrefix() {
    return prefix;
  }

  @Override
  public List<String> getAliases() {
    return aliases;
  }

}
