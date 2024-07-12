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

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerVersion {

  public Version getVersion() {
    return Version.getCurrent();
  }

  public enum Version {
    v0_0_0(0, 0),
    v1_8_8(8, 4),
    v1_9(9, 4),
    v1_10(10, 2),
    v1_11(11, 0),
    v1_12(12, 0),
    v1_13(13, 1),
    v1_14(14, 0),
    v1_15(15, 0),
    v1_16(16, 0),
    v1_17(17, 0),
    v1_18(18, 0),
    v1_19(19, 0),
    v1_20(20, 0),
    v1_21(21, 0);


    private static Version current;
    private final int minor;
    private final int minPatch;

    Version(int minor, int minPatch) {
      this.minor = minor;
      this.minPatch = minPatch;
    }

    public int getMinor() {
      return minor;
    }

    public int getMinPatch() {
      return minPatch;
    }

    public static Version getCurrent() {
      if(current != null) {
        return current;
      }

      Matcher serverVersion = Pattern.compile("^(?<major>\\d+)\\.(?<minor>\\d+)(?:\\.(?<patch>\\d+))?").matcher(Bukkit.getBukkitVersion());
      if(serverVersion.find()) {
        int serverMinor = Integer.parseInt(serverVersion.group("minor"));
        String patch = serverVersion.group("patch");
        int serverPatch = Integer.parseInt((patch == null || patch.isEmpty()) ? "0" : patch);

        for(Version value : values()) {
          if(value.getMinor() == serverMinor && serverPatch >= value.getMinPatch()) {
            current = value;
            break;
          }
        }
      } else {
        throw new IllegalStateException("Cannot parse server version: \"" + Bukkit.getBukkitVersion() + '"');
      }

      if(current == null) { // Fallback
        current = Version.v0_0_0;
      }

      return current;
    }

    public boolean isLower(Version version) {
      return minor < version.getMinor();
    }

    public boolean isHigher(Version version) {
      return minor > version.getMinor();
    }

    public boolean isEqual(Version version) {
      return minor == version.getMinor();
    }

    public boolean isEqualOrLower(Version version) {
      return minor <= version.getMinor();
    }

    public boolean isEqualOrHigher(Version version) {
      return minor >= version.getMinor();
    }

    public static boolean isCurrentEqualOrHigher(Version fixedVersion) {
      return getCurrent().getMinor() >= fixedVersion.getMinor();
    }

    public static boolean isCurrentHigher(Version fixedVersion) {
      return getCurrent().getMinor() > fixedVersion.getMinor();
    }

    public static boolean isCurrentLower(Version fixedVersion) {
      return getCurrent().getMinor() < fixedVersion.getMinor();
    }

    public static boolean isCurrentEqualOrLower(Version fixedVersion) {
      return getCurrent().getMinor() <= fixedVersion.getMinor();
    }

    public static boolean isCurrentEqual(Version fixedVersion) {
      return getCurrent().getMinor() == fixedVersion.getMinor();
    }
  }
}