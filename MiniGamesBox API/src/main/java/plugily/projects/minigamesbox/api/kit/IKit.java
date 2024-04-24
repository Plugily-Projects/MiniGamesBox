package plugily.projects.minigamesbox.api.kit;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.api.IPluginMain;
import plugily.projects.minigamesbox.api.kit.ability.IKitAbility;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public interface IKit {
  boolean isUnlockedByPlayer(Player p);

  boolean isUnlockedOnDefault();

  HashMap<ItemStack, Integer> getKitItems();

  /**
   * @return main plugin
   */
  IPluginMain getPlugin();

  /**
   * Retrieves the name of the object.
   *
   * @return the name of the object
   */
  String getName();

  String getKey();

  ItemStack getItemStack();

  ArrayList<String> getDescription();

  void giveKitItems(Player player);

  /**
   * @return Returns the configuration section for the kit
   */
  ConfigurationSection getKitConfigSection();

  Object getOptionalConfiguration(String path, Object defaultValue);

  Object getOptionalConfiguration(String path);

  void addOptionalConfiguration(String path, Object object);

  boolean hasAbility(IKitAbility kitAbility);
}
