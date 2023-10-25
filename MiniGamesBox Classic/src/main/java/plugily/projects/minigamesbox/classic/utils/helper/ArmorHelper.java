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

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Tigerpanzer_02 on 17.12.2020.
 */
public class ArmorHelper {
  private static final List<Material> armorTypes = new ArrayList<>(Arrays.asList(Material.LEATHER_BOOTS, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_HELMET,
      XMaterial.GOLDEN_BOOTS.parseMaterial(), XMaterial.GOLDEN_CHESTPLATE.parseMaterial(), XMaterial.GOLDEN_LEGGINGS.parseMaterial(),
      XMaterial.GOLDEN_HELMET.parseMaterial(), Material.DIAMOND_BOOTS, Material.DIAMOND_LEGGINGS, Material.DIAMOND_CHESTPLATE,
      Material.DIAMOND_HELMET, Material.IRON_CHESTPLATE, Material.IRON_BOOTS, Material.IRON_HELMET, Material.IRON_LEGGINGS,
      Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_HELMET));

  private ArmorHelper() {
  }

  public static List<Material> getArmorTypes() {
    return Collections.unmodifiableList(armorTypes);
  }

  public static void setArmor(Player player, ArmorType type) {
    PlayerInventory inv = player.getInventory();
    switch(type) {
      case LEATHER:
        inv.setBoots(new ItemStack(Material.LEATHER_BOOTS));
        inv.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        inv.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        inv.setHelmet(new ItemStack(Material.LEATHER_HELMET));
        break;
      case IRON:
        inv.setBoots(new ItemStack(Material.IRON_BOOTS));
        inv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        inv.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        inv.setHelmet(new ItemStack(Material.IRON_HELMET));
        break;
      case GOLD:
        inv.setBoots(XMaterial.GOLDEN_BOOTS.parseItem());
        inv.setLeggings(XMaterial.GOLDEN_LEGGINGS.parseItem());
        inv.setChestplate(XMaterial.GOLDEN_CHESTPLATE.parseItem());
        inv.setHelmet(XMaterial.GOLDEN_HELMET.parseItem());
        break;
      case DIAMOND:
        inv.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        inv.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        inv.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        inv.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        break;
      default:
        break; //o.o
    }
  }

  public static void setColouredArmor(Color color, Player player) {
    ItemStack helmet = new ItemStack(Material.LEATHER_HELMET),
        chestplate = new ItemStack(Material.LEATHER_CHESTPLATE),
        leggings = new ItemStack(Material.LEATHER_LEGGINGS),
        boots = new ItemStack(Material.LEATHER_BOOTS);

    LeatherArmorMeta helmMeta = (LeatherArmorMeta) helmet.getItemMeta();
    helmMeta.setColor(color);
    LeatherArmorMeta armorMeta = (LeatherArmorMeta) chestplate.getItemMeta();
    armorMeta.setColor(color);
    LeatherArmorMeta legsMeta = (LeatherArmorMeta) leggings.getItemMeta();
    legsMeta.setColor(color);
    LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
    bootsMeta.setColor(color);

    boots.setItemMeta(bootsMeta);
    helmet.setItemMeta(helmMeta);
    chestplate.setItemMeta(armorMeta);
    leggings.setItemMeta(legsMeta);
    boots.setItemMeta(bootsMeta);

    PlayerInventory inv = player.getInventory();
    inv.setHelmet(helmet);
    inv.setChestplate(chestplate);
    inv.setLeggings(leggings);
    inv.setBoots(boots);
  }

  public enum ArmorType {
    LEATHER, IRON, DIAMOND, GOLD
  }

}


