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

package plugily.projects.minigamesbox.classic.utils.helper;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author Plajer
 * <p>
 * Created at 09.03.2019
 */
public class ItemUtils {

  public static final ItemStack PLAYER_HEAD_ITEM = XMaterial.PLAYER_HEAD.parseItem();

  private ItemUtils() {
  }

  /**
   * Checks whether itemstack is named (not null, has meta and display name)
   *
   * @param stack item stack to check
   * @return true if named, false otherwise
   */
  public static boolean isItemStackNamed(ItemStack stack) {
    return stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName();
  }

  public static ItemStack getSkull(String url) {
    ItemStack head = PLAYER_HEAD_ITEM.clone();
    if(url.isEmpty() || !(head.getItemMeta() instanceof SkullMeta)) {
      return head;
    }

    SkullMeta headMeta = (SkullMeta) head.getItemMeta();
    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
    profile.getProperties().put("textures", new Property("textures", url));
    if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_15_R1)) {
      try {
        Method mtd = headMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
        mtd.setAccessible(true);
        mtd.invoke(headMeta, profile);
      } catch(Exception ignored) {
      }
    } else {
      try {
        Field profileField = headMeta.getClass().getDeclaredField("profile");
        profileField.setAccessible(true);
        profileField.set(headMeta, profile);
      } catch(Exception ignored) {
      }
    }
    head.setItemMeta(headMeta);
    return head;
  }

}
