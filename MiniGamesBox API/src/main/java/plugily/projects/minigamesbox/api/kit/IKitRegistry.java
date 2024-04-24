package plugily.projects.minigamesbox.api.kit;

import java.util.List;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IKitRegistry {
  HandleItem getHandleItem();

  void setHandleItem(HandleItem handleItem);

  /**
   * Method for registering clone and empty kit
   *
   * @param kit Kit to register
   */
  void registerKit(IKit kit);

  /**
   * Registers the kits by loading their configurations.
   */
  void registerKits(List<String> optionalConfigurations);

  /**
   * Return default game kit
   *
   * @return default game kit
   */
  IKit getDefaultKit();

  /**
   * Sets default game kit
   *
   * @param defaultKit default kit to set, must be FreeKit
   */
  void setDefaultKit(IKit defaultKit);

  /**
   * Sets the default kit for the plugin using the config option
   *
   * @param defaultKitName name of the default kit
   */
  void setDefaultKit(String defaultKitName);

  /**
   * Returns all available kits
   *
   * @return list of all registered kits
   */
  List<IKit> getKits();

  /**
   * Retrieves a Kit object based on the provided key.
   *
   * @param key the key used to search for the Kit
   * @return the Kit object with the matching key, or null if not found
   */
  IKit getKitByKey(String key);

  /**
   * Retrieves a Kit object from the 'kits' list by its name.
   *
   * @param key the name of the Kit object to retrieve
   * @return the Kit object with the specified name, or null if not found
   */
  IKit getKitByName(String key);
}
