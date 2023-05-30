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

package plugily.projects.minigamesbox.classic.handlers.permissions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 07.12.2021
 */
public class Permission {

  private static final Map<String, Permission> permissions = new HashMap<>();


  static {
    permissions.put("JOIN_FULL_GAME", new Permission("Basic.Full-Games", "plugilyprojects.fullgames", true));
    permissions.put("JOIN", new Permission("Basic.Join", "plugilyprojects.join.<arena>", true));
    permissions.put("FORCESTART_GAME", new Permission("Basic.Forcestart", "plugilyprojects.admin.forcestart", true));
  }

  private final String path;
  private final String permission;
  private final boolean protectedCategory;


  public Permission(String path, String permission, boolean protectedCategory) {
    this.path = path;
    this.permission = permission;
    this.protectedCategory = protectedCategory;
  }

  public Permission(String path, String permission) {
    this.path = path;
    this.permission = permission;
    this.protectedCategory = false;
  }

  public String getPath() {
    return path;
  }

  /**
   * @return the permission
   */
  public String getPermission() {
    return permission;
  }

  /**
   * @return if permission is protected and cannot be unregistered
   */
  public boolean isProtected() {
    return protectedCategory;
  }

  public static Map<String, Permission> getPermissions() {
    return Collections.unmodifiableMap(permissions);
  }

}
