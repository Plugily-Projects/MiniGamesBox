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

import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.Main;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
//todo more possibilities aka permissions.yml
public class PermissionsManager {

  private final Main plugin;
  private String joinFullPerm;
  private String vipPerm;
  private String mvpPerm;
  private String elitePerm;
  private String joinPerm;

  public PermissionsManager(Main plugin) {
    this.plugin = plugin;
    this.joinFullPerm = plugin.getName().toLowerCase() + ".fullgames";
    this.vipPerm = plugin.getName().toLowerCase() + ".vip";
    this.mvpPerm = plugin.getName().toLowerCase() + ".mvp";
    this.elitePerm = plugin.getName().toLowerCase() + ".elite";
    this.joinPerm = plugin.getName().toLowerCase() + ".join.<arena>";
    setupPermissions();
  }


  public String getJoinFullGames() {
    return joinFullPerm;
  }

  private void setJoinFullGames(String joinFullGames) {
    joinFullPerm = joinFullGames;
  }

  public String getVip() {
    return vipPerm;
  }

  private void setVip(String vip) {
    vipPerm = vip;
  }

  public String getMvp() {
    return mvpPerm;
  }

  private void setMvp(String mvp) {
    mvpPerm = mvp;
  }

  public String getElite() {
    return elitePerm;
  }

  private void setElite(String elite) {
    elitePerm = elite;
  }

  public String getJoinPerm() {
    return joinPerm;
  }

  private void setJoinPerm(String joinPerm) {
    joinPerm = joinPerm;
  }

  public boolean isPremium(Player p) {
    return p.hasPermission(vipPerm) || p.hasPermission(mvpPerm) || p.hasPermission(elitePerm);
  }

  private void setupPermissions() {
    setJoinFullGames(plugin.getConfig().getString("Basic-Permissions.Full-Games-Permission", plugin.getName().toLowerCase() + ".fullgames"));
    setVip(plugin.getConfig().getString("Basic-Permissions.Vip-Permission", plugin.getName().toLowerCase() + ".vip"));
    setMvp(plugin.getConfig().getString("Basic-Permissions.Mvp-Permission", plugin.getName().toLowerCase() + ".mvp"));
    setElite(plugin.getConfig().getString("Basic-Permissions.Elite-Permission", plugin.getName().toLowerCase() + ".elite"));
    setJoinPerm(plugin.getConfig().getString("Basic-Permissions.Join-Permission", plugin.getName().toLowerCase() + ".join.<arena>"));
    plugin.getDebugger().debug("Basic permissions registered");
  }

}
