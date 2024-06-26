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

public class ServerVersion {

  public Version getVersion() {
    return Version.getCurrent();
  }

  public enum Version {
    v0_0_R0,
    v1_8_R1,
    v1_8_R2,
    v1_8_R3,
    v1_9_R1,
    v1_9_R2,
    v1_10_R1,
    v1_10_R2,
    v1_11_R1,
    v1_12_R1,
    v1_13_R1,
    v1_13_R2,
    v1_14_R1,
    v1_14_R2,
    v1_15_R1,
    v1_15_R2,
    v1_16_R1,
    v1_16_R2,
    v1_16_R3,
    v1_17_R1,
    v1_17_R2,
    v1_18_R1,
    v1_18_R2,
    v1_19_R1,
    v1_19_R2,
    v1_19_R3,
    v1_20_R1,
    v1_20_R2,
    v1_20_R3,
    v1_20_R4,
    V1_20_R5,
    V1_20_R6,
    V1_21_R1;

    private final int value;

    private static String[] packageVersion;
    private static Version current;

    Version() {
      value = Integer.parseInt(name().replaceAll("[^\\d.]", ""));
    }

    public int getValue() {
      return value;
    }

    public static String[] getPackageVersion() {
      if(packageVersion == null) {
        packageVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
      }

      return packageVersion;
    }

    public static Version getCurrent() {
      if(current != null)
        return current;

      String[] v = getPackageVersion();
      String vv = v[v.length - 1];
      for(Version one : values()) {
        if(one.name().equalsIgnoreCase(vv)) {
          current = one;
          break;
        }
      }

      if(current == null) { // If we forgot to add new version to enum
        current = Version.v0_0_R0;
      }

      return current;
    }

    public boolean isLower(Version version) {
      return value < version.getValue();
    }

    public boolean isHigher(Version version) {
      return value > version.getValue();
    }

    public boolean isEqual(Version version) {
      return value == version.getValue();
    }

    public boolean isEqualOrLower(Version version) {
      return value <= version.getValue();
    }

    public boolean isEqualOrHigher(Version version) {
      return value >= version.getValue();
    }

    public static boolean isCurrentEqualOrHigher(Version v) {
      return getCurrent().getValue() >= v.getValue();
    }

    public static boolean isCurrentHigher(Version v) {
      return getCurrent().getValue() > v.getValue();
    }

    public static boolean isCurrentLower(Version v) {
      return getCurrent().getValue() < v.getValue();
    }

    public static boolean isCurrentEqualOrLower(Version v) {
      return getCurrent().getValue() <= v.getValue();
    }

    public static boolean isCurrentEqual(Version v) {
      return getCurrent().getValue() == v.getValue();
    }
  }
}