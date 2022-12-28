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

package plugily.projects.minigamesbox.classic.handlers.permissions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 07.12.2021
 */
public class PermissionCategory {

  private static final Map<String, PermissionCategory> permissionCategories = new HashMap<>();


  static {
    permissionCategories.put("EXP_BOOSTER", new PermissionCategory("Exp-Boost", null, true));
  }

  private final String path;
  private Map<String, Integer> customPermissions;
  private final boolean protectedCategory;


  public PermissionCategory(String path, Map<String, Integer> customPermissions, boolean protectedCategory) {
    this.path = path;
    this.customPermissions = customPermissions;
    this.protectedCategory = protectedCategory;
  }

  public PermissionCategory(String path, Map<String, Integer> customPermissions) {
    this.path = path;
    this.customPermissions = customPermissions;
    this.protectedCategory = false;
  }

  public String getPath() {
    return path;
  }

  public Map<String, Integer> getCustomPermissions() {
    return Collections.unmodifiableMap(customPermissions);
  }

  /**
   * @return if permission category is protected and cannot be unregistered
   */
  public boolean isProtected() {
    return protectedCategory;
  }

  public static Map<String, PermissionCategory> getPermissionsCategories() {
    return Collections.unmodifiableMap(permissionCategories);
  }

}
