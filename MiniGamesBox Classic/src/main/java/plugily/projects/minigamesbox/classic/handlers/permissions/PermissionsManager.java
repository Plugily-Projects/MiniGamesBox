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

import plugily.projects.minigamesbox.classic.PluginMain;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
//todo more possibilities aka permissions.yml
public class PermissionsManager {

  private final PluginMain plugin;
  private String joinFullPerm;
  private String joinPerm;
  private String forceStartPerm;

  public PermissionsManager(PluginMain plugin) {
    this.plugin = plugin;
    this.joinFullPerm = plugin.getPluginNamePrefixLong() + ".fullgames";
    this.joinPerm = plugin.getPluginNamePrefixLong() + ".join.<arena>";
    this.forceStartPerm = plugin.getPluginNamePrefixLong() + ".admin.forcestart";
    setupPermissions();
  }


  public String getJoinFullGames() {
    return joinFullPerm;
  }

  private void setJoinFullGames(String joinFullGames) {
    joinFullPerm = joinFullGames;
  }

  public String getJoinPerm() {
    return joinPerm;
  }

  private void setJoinPerm(String joinPerm) {
    this.joinPerm = joinPerm;
  }

  public void setForceStartPerm(String forceStartPerm) {
    this.forceStartPerm = forceStartPerm;
  }

  public String getForceStart() {
    return forceStartPerm;
  }

  private void setupPermissions() {
    setJoinFullGames(plugin.getConfig().getString("Basic-Permissions.Full-Games", plugin.getPluginNamePrefixLong() + ".fullgames"));
    setJoinPerm(plugin.getConfig().getString("Basic-Permissions.Join", plugin.getPluginNamePrefixLong() + ".join.<arena>"));
    setForceStartPerm(plugin.getConfig().getString("Basic-Permissions.Forcestart", plugin.getPluginNamePrefixLong() + ".admin.forcestart"));
    plugin.getDebugger().debug("Basic permissions registered");
  }

}
