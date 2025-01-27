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

package plugily.projects.minigamesbox.classic.utils.helper;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.ChatPaginator;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.misc.complement.ComplementAccessor;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 06.08.2021
 * @version 2.0.0
 */
public class ItemBuilder {

  private final ItemStack itemStack;
  private final ItemMeta itemMeta;

  public ItemBuilder(final ItemStack copyStack) {
    this.itemStack = copyStack == null ? new ItemStack(Material.STONE) : copyStack;
    this.itemMeta = itemStack.getItemMeta();
  }

  public ItemBuilder(final Material material) {
    this.itemStack = new ItemStack(material == null ? Material.STONE : material);
    this.itemMeta = itemStack.getItemMeta();
  }

  public ItemBuilder type(Material material) {
    this.itemStack.setType(material == null ? Material.STONE : material);
    return this;
  }

  public ItemBuilder amount(int amount) {
    this.itemStack.setAmount(Math.max(amount, 1));
    return this;
  }

  @SuppressWarnings("deprecation")
  public ItemBuilder data(byte data) {
    org.bukkit.material.MaterialData materialData = this.itemStack.getData();

    if(materialData != null) {
      materialData.setData(data);
    }
    return this;
  }

  public ItemBuilder data(int data) {
    return durability((short) data);
  }

  @SuppressWarnings("deprecation")
  public ItemBuilder durability(short durability) {
    this.itemStack.setDurability(durability);
    return this;
  }

  public ItemBuilder name(final String name) {
    ComplementAccessor.getComplement().setDisplayName(itemMeta, name == null ? "" : name);
    return this;
  }

  public ItemBuilder glowEffect() {
    if (ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_21)) {
      itemMeta.setEnchantmentGlintOverride(true);
    } else {
      this.itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
      this.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }
    return this;
  }

  public ItemBuilder enchantment(Enchantment enchantment) {
    return enchantment(enchantment, 1);
  }

  public ItemBuilder enchantment(Enchantment enchantment, int level) {
    this.itemMeta.addEnchant(enchantment, level, true);
    return this;
  }

  public ItemBuilder removeEnchant(Enchantment enchantment) {
    this.itemMeta.removeEnchant(enchantment);
    return this;
  }

  public ItemBuilder removeEnchants() {
    this.itemMeta.getEnchants().keySet().forEach(this.itemMeta::removeEnchant);
    return this;
  }

  public ItemBuilder removeLore() {
    List<String> lore = ComplementAccessor.getComplement().getLore(itemMeta);
    lore.clear();
    ComplementAccessor.getComplement().setLore(itemMeta, lore);
    return this;
  }

  public ItemBuilder lore(String lore) {
    return lore(Collections.singletonList(lore));
  }

  public ItemBuilder lore(final String... name) {
    return lore(Arrays.asList(name));
  }

  public ItemBuilder lore(final List<String> name) {
    lore(name, true);
    return this;
  }

  public ItemBuilder lore(final List<String> name, boolean wordWrap) {
    List<String> lore = ComplementAccessor.getComplement().getLore(itemMeta);
    if(name != null) {
      if(wordWrap) {
        for(String line : name) {
          String lastColor = "";
          for(String splitLine : ChatPaginator.wordWrap(line, 40)) {
            lore.add(lastColor + splitLine);
            lastColor = ChatColor.getLastColors(splitLine);
          }
        }
      } else {
        lore.addAll(name);
      }
    }
    ComplementAccessor.getComplement().setLore(itemMeta, lore);
    return this;
  }

  public ItemBuilder colorizeItem() {
    if(itemMeta.hasDisplayName()) {
      ComplementAccessor.getComplement().setDisplayName(itemMeta,
          new MessageBuilder(ComplementAccessor.getComplement().getDisplayName(itemMeta)).build());
    }
    if(itemMeta.hasLore()) {
      List<String> lore = ComplementAccessor.getComplement().getLore(itemMeta);

      lore.replaceAll(textToTranslate -> new MessageBuilder(textToTranslate).build());

      ComplementAccessor.getComplement().setLore(itemMeta, lore);
    }
    return this;
  }

  public ItemBuilder flags(ItemFlag... flags) {
    this.itemMeta.addItemFlags(flags);
    return this;
  }

  public ItemBuilder flags() {
    return flags(ItemFlag.values());
  }

  public ItemBuilder removeFlags(ItemFlag... flags) {
    this.itemMeta.removeItemFlags(flags);
    return this;
  }

  public ItemBuilder removeFlags() {
    return removeFlags(ItemFlag.values());
  }

  public ItemStack build() {
    this.itemStack.setItemMeta(this.itemMeta);
    return itemStack;
  }

}
