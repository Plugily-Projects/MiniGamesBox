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

package plugily.projects.minigamesbox.classic.utils.version;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class PacketUtils {

  private static Method playerHandleMethod, sendPacketMethod;
  private static Field playerConnectionField;

  public static void sendPacket(Player player, Object packet) {
    try {
      if (playerHandleMethod == null)
        playerHandleMethod = player.getClass().getDeclaredMethod("getHandle");

      Object handle = playerHandleMethod.invoke(player);

      if (playerConnectionField == null)
        playerConnectionField = handle.getClass().getField(
              (ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_17) ? "b" : "playerConnection"));

      Object playerConnection = playerConnectionField.get(handle);

      if (sendPacketMethod == null)
        sendPacketMethod = playerConnection.getClass().getMethod((ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_18) ? "a" : "sendPacket"),
            classByName("net.minecraft.network.protocol", "Packet"));

      sendPacketMethod.invoke(playerConnection, packet);
    } catch(ReflectiveOperationException ex) {
      ex.printStackTrace();
    }
  }

  public static Class<?> classByName(String newPackageName, String className) {
    if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_17) || newPackageName == null) {
      newPackageName = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    try {
      return Class.forName(newPackageName + "." + className);
    } catch(ClassNotFoundException ex) {
      return null;
    }
  }

}
