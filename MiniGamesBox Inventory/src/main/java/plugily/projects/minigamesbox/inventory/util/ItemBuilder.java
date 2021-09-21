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

package plugily.projects.minigamesbox.inventory.util;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Simple {@link ItemStack} builder
 *
 * @author MrMicky
 */
public class ItemBuilder {

  private final ItemStack item;
  private final ItemMeta meta;

  public ItemBuilder(Material material) {
    this(new ItemStack(material));
  }

  public ItemBuilder(ItemStack item) {
    this.item = Objects.requireNonNull(item, "item");
    this.meta = item.getItemMeta();

    if(this.meta == null) {
      throw new IllegalArgumentException("The type " + item.getType() + " doesn't support item meta");
    }
  }

  public ItemBuilder type(Material material) {
    this.item.setType(material);
    return this;
  }

  public ItemBuilder data(int data) {
    return durability((short) data);
  }

  @SuppressWarnings("deprecation")
  public ItemBuilder durability(short durability) {
    this.item.setDurability(durability);
    return this;
  }

  public ItemBuilder amount(int amount) {
    this.item.setAmount(amount);
    return this;
  }

  public ItemBuilder enchant(Enchantment enchantment) {
    return enchant(enchantment, 1);
  }

  public ItemBuilder enchant(Enchantment enchantment, int level) {
    this.meta.addEnchant(enchantment, level, true);
    return this;
  }

  public ItemBuilder removeEnchant(Enchantment enchantment) {
    this.meta.removeEnchant(enchantment);
    return this;
  }

  public ItemBuilder removeEnchants() {
    this.meta.getEnchants().keySet().forEach(this.meta::removeEnchant);
    return this;
  }

  public ItemBuilder meta(Consumer<ItemMeta> metaConsumer) {
    metaConsumer.accept(this.meta);
    return this;
  }

  public <T extends ItemMeta> ItemBuilder meta(Class<T> metaClass, Consumer<T> metaConsumer) {
    if(metaClass.isInstance(this.meta)) {
      metaConsumer.accept(metaClass.cast(this.meta));
    }
    return this;
  }

  public ItemBuilder name(String name) {
    this.meta.setDisplayName(name);
    return this;
  }

  public ItemBuilder lore(String lore) {
    return lore(Collections.singletonList(lore));
  }

  public ItemBuilder lore(String... lore) {
    return lore(Arrays.asList(lore));
  }

  public ItemBuilder lore(List<String> lore) {
    this.meta.setLore(lore);
    return this;
  }

  public ItemBuilder addLore(String line) {
    List<String> lore = this.meta.getLore();

    if(lore == null) {
      return lore(line);
    }

    lore.add(line);
    return lore(lore);
  }

  public ItemBuilder addLore(String... lines) {
    return addLore(Arrays.asList(lines));
  }

  public ItemBuilder addLore(List<String> lines) {
    List<String> lore = this.meta.getLore();

    if(lore == null) {
      return lore(lines);
    }

    lore.addAll(lines);
    return lore(lore);
  }

  public ItemBuilder flags(ItemFlag... flags) {
    this.meta.addItemFlags(flags);
    return this;
  }

  public ItemBuilder flags() {
    return flags(ItemFlag.values());
  }

  public ItemBuilder removeFlags(ItemFlag... flags) {
    this.meta.removeItemFlags(flags);
    return this;
  }

  public ItemBuilder removeFlags() {
    return removeFlags(ItemFlag.values());
  }

  public ItemBuilder armorColor(Color color) {
    return meta(LeatherArmorMeta.class, m -> m.setColor(color));
  }

  public ItemStack build() {
    this.item.setItemMeta(this.meta);
    return this.item;
  }
}