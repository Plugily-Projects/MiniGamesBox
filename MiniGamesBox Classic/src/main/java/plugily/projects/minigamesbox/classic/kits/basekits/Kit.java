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

package plugily.projects.minigamesbox.classic.kits.basekits;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public abstract class Kit {

    private static final PluginMain plugin = JavaPlugin.getPlugin(PluginMain.class);

    private FileConfiguration kitsConfig = ConfigUtils.getConfig(plugin, "kits");

    private String name = "";

    private String key = "";

    private boolean unlockedOnDefault = false;
    private String[] description = new String[0];

    private HashMap<ItemStack, Integer> kitItems = new HashMap<>();
    private ItemStack kitHelmet;
    private ItemStack kitChestplate;
    private ItemStack kitLeggings;
    private ItemStack kitBoots;

    protected Kit() {
    }

    public Kit(String name) {
        setName(name);
        setKey(name);
    }

    public Kit(String name, String key) {
        setName(name);
        setKey(key);
    }

    public abstract boolean isUnlockedByPlayer(Player p);

    public boolean isUnlockedOnDefault() {
        return unlockedOnDefault;
    }

    public void setUnlockedOnDefault(boolean unlockedOnDefault) {
        this.unlockedOnDefault = unlockedOnDefault;
    }

    public void addKitItem(ItemStack item, Integer slot) {
        kitItems.put(item, slot);
    }

    public HashMap<ItemStack, Integer> getKitItems() {
        return kitItems;
    }

    public void setKitItems(HashMap<ItemStack, Integer> kitItems) {
        this.kitItems = kitItems;
    }

    /**
     * @return main plugin
     */
    public PluginMain getPlugin() {
        return plugin;
    }

    /**
     * @return config file of kits
     */
    public FileConfiguration getKitsConfig() {
        return kitsConfig;
    }

    public void saveKitsConfig() {
        ConfigUtils.saveConfig(plugin, kitsConfig, "kits");
        kitsConfig = ConfigUtils.getConfig(plugin, "kits");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public void setKey(String key) {
        this.key = key;
    }

    public abstract void setupKitItems();

    public String getKey() {
        if (key.equalsIgnoreCase("")) {
            return name;
        }
        return key;
    }

    public String[] getDescription() {
        return description.clone();
    }

    public void setDescription(String[] description) {
        if (description != null) {
            this.description = description.clone();
        }
    }

    public void setDescription(List<String> description) {
        if (description != null) {
            this.description = description.toArray(new String[0]);
        }
    }

    public abstract ItemStack getItemStack();

    public void giveKitItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        kitItems.forEach((item, slot) -> player.getInventory().setItem(slot, item));
        if (kitHelmet != null) player.getInventory().setHelmet(kitHelmet);
        if (kitChestplate != null) player.getInventory().setHelmet(kitChestplate);
        if (kitLeggings != null) player.getInventory().setHelmet(kitLeggings);
        if (kitBoots != null) player.getInventory().setHelmet(kitBoots);
    }

    ;

    public abstract void reStock(Player player);

    /**
     * @return Returns the configuration path for the kit
     */
    public String getKitConfigPath() {
        return "kit." + key;
    }

    /**
     * @return Returns the configuration section for the kit
     */
    public @Nonnull ConfigurationSection getKitConfigSection() {
        ConfigurationSection configurationSection = kitsConfig.getConfigurationSection(getKitConfigPath());
        if (configurationSection == null) {
            kitsConfig.createSection(getKitConfigPath());
        }
        assert configurationSection != null;
        return configurationSection;
    }

    public ItemStack getKitHelmet() {
        return kitHelmet;
    }

    public void setKitHelmet(ItemStack kitHelmet) {
        this.kitHelmet = kitHelmet;
    }

    public ItemStack getKitChestplate() {
        return kitChestplate;
    }

    public void setKitChestplate(ItemStack kitChestplate) {
        this.kitChestplate = kitChestplate;
    }

    public ItemStack getKitLeggings() {
        return kitLeggings;
    }

    public void setKitLeggings(ItemStack kitLeggings) {
        this.kitLeggings = kitLeggings;
    }

    public ItemStack getKitBoots() {
        return kitBoots;
    }

    public void setKitBoots(ItemStack kitBoots) {
        this.kitBoots = kitBoots;
    }
}
