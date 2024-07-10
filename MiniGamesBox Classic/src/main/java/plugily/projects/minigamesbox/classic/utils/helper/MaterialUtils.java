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
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class MaterialUtils {

  private static final Set<Material> WALL_SIGNS = new HashSet<Material>() {
    {
      add(getMat("WALL_SIGN"));
      add(getMat("ACACIA_WALL_SIGN"));
      add(getMat("BIRCH_WALL_SIGN"));
      add(getMat("DARK_OAK_WALL_SIGN"));
      add(getMat("JUNGLE_WALL_SIGN"));
      add(getMat("OAK_WALL_SIGN"));
      add(getMat("SPRUCE_WALL_SIGN"));
      add(getMat("WARPED_WALL_SIGN"));
      add(getMat("CRIMSON_WALL_SIGN"));
    }
  };

  public static final Set<Material> BASIC_SIGNS = new HashSet<Material>() {
    {
      add(getMat("SIGN"));
      add(getMat("STANDING_SIGN"));
      add(getMat("ACACIA_SIGN"));
      add(getMat("BIRCH_SIGN"));
      add(getMat("DARK_OAK_SIGN"));
      add(getMat("JUNGLE_SIGN"));
      add(getMat("OAK_SIGN"));
      add(getMat("SPRUCE_SIGN"));
      add(getMat("WARPED_SIGN"));
      add(getMat("CRIMSON_SIGN"));
    }
  };

  public static final Set<Material> ALL_SIGNS = new HashSet<Material>() {
    {
      addAll(WALL_SIGNS);
      addAll(BASIC_SIGNS);
    }
  };

  public static final Set<Material> DOORS = new HashSet<Material>() {
    {
      add(getMat("WOODEN_DOOR"));
      add(getMat("ACACIA_DOOR"));
      add(getMat("BIRCH_DOOR"));
      add(getMat("DARK_OAK_DOOR"));
      add(getMat("JUNGLE_DOOR"));
      add(getMat("SPRUCE_DOOR"));
      add(getMat("OAK_DOOR"));
      add(getMat("WOOD_DOOR"));
      add(getMat("WARPED_DOOR"));
      add(getMat("CRIMSON_DOOR"));
    }
  };

  public static boolean isWallSign(Material mat) {
    return WALL_SIGNS.contains(mat);
  }

  public static boolean isDoor(Material mat) {
    return DOORS.contains(mat);
  }

  private static Material getMat(String name) {
    Material material = Material.getMaterial(name.toUpperCase());
    if(material == null) {
      material = XMaterial.OAK_SIGN.parseMaterial();
    }
    return material;
  }
}
