package plugily.projects.minigamesbox.api.utils.services.locale;

import java.util.List;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface ILocale {

  /**
   * Gets name of locale, ex. English or German
   *
   * @return name of locale
   */
  String getName();

  /**
   * Gets original name of locale ex. for German it will return Deutsch, Polish returns Polski etc.
   *
   * @return name of locale in its language
   */
  String getOriginalName();

  /**
   * @return authors of locale
   */
  String getAuthor();

  /**
   * Language code ex. en_GB, de_DE, pl_PL etc.
   *
   * @return language code of locale
   */
  String getPrefix();

  /**
   * Valid aliases of locale ex. for German - deutsch, de, german; Polish - polski, pl, polish etc.
   *
   * @return aliases for locale
   */
  List<String> getAliases();
}
