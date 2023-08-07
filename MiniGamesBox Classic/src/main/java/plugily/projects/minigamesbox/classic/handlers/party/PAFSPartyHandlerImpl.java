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

package plugily.projects.minigamesbox.classic.handlers.party;

import de.simonsator.partyandfriends.api.party.PartyManager;
import de.simonsator.partyandfriends.api.party.PlayerParty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class PAFSPartyHandlerImpl implements PartyHandler {

  @Override
  public GameParty getParty(Player player) {
    PlayerParty party = PartyManager.getInstance().getParty(player.getUniqueId());
    if(party == null)
      return null;

    Player leader = Bukkit.getPlayer(party.getLeader().getUniqueId());
    if(leader == null)
      return null;

    java.util.List<Player> allMembers = party.getAllPlayers().stream()
        .map(localPlayer -> Bukkit.getPlayer(localPlayer.getUniqueId()))
        .filter(java.util.Objects::nonNull).collect(Collectors.toList());

    return new GameParty(allMembers, leader);
  }

  @Override
  public boolean partiesSupported() {
    return true;
  }

  @Override
  public PartyPluginType getPartyPluginType() {
    return PartyPluginType.PAFSpigot;
  }
}
