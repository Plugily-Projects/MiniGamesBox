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


package plugily.projects.minigamesbox.classic.handlers.hologram;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import plugily.projects.minigamesbox.api.stats.IStatisticType;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.Message;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.hologram.ArmorStandHologram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 09.10.2021
 */
//todo enhanced leaderboards management with leaderboards.yml such as top stats over x amount of time
public class LeaderboardHologram {

  private final PluginMain plugin;
  private final int id;
  private final IStatisticType statistic;
  private final int topAmount;
  private ArmorStandHologram hologram;
  private final Location location;
  private final String header;

  public LeaderboardHologram(PluginMain plugin, int id, IStatisticType statistic, int amount, Location location) {
    this.plugin = plugin;
    this.id = id;
    this.statistic = statistic;
    this.topAmount = amount;
    this.location = location;
    Message statisticMessage = statisticToMessage();
    this.header = new MessageBuilder("LEADERBOARD_TYPE_HOLOGRAM_HEADER").asKey().integer(topAmount).value(new MessageBuilder(statisticMessage).build().replace("%number%", "")).build();
    plugin.getDebugger().debug("Loaded ArmorStand {0} with header {1}", id, header);
    this.hologram = new ArmorStandHologram(location, header);
    updateHologram();
  }

  public void updateHologram() {
    java.util.Map<UUID, Integer> values = plugin.getStatsStorage().getStats(statistic);
    List<UUID> reverseKeys = new ArrayList<>(values.keySet());
    Collections.reverse(reverseKeys);

    List<String> update = new ArrayList<>(Collections.singletonList(header));

    for(int i = 0; i < topAmount; i++) {
      String text;
      if(i < reverseKeys.size()) {
        UUID uuid = reverseKeys.get(i);
        text = new MessageBuilder("LEADERBOARD_TYPE_HOLOGRAM_FORMAT").asKey().integer(i + 1).value(String.valueOf(values.get(uuid))).build();
        text = StringUtils.replace(text, "%player%", getPlayerNameSafely(uuid));
      } else {
        text = new MessageBuilder("LEADERBOARD_TYPE_HOLOGRAM_EMPTY_FORMAT").asKey().integer(i + 1).build();
      }
      update.add(text);
    }
    plugin.getDebugger().debug("Updating ArmorStand {0} with lines {1}", id, update.toString());
    this.hologram = hologram.overwriteLines(update);
  }

  public void delete() {
    hologram.delete();
    this.hologram = null;
  }

  private String getPlayerNameSafely(UUID uuid) {
    String name = plugin.getUserManager().getDatabase().getPlayerName(uuid);
    // Attempts to get the bukkit name instead if the name is null from database or an empty string
    if (name == null || name.isBlank()) {
      name = Bukkit.getOfflinePlayer(uuid).getName();
    }
    if (name == null || name.isBlank()) {
      return new MessageBuilder("LEADERBOARD_UNKNOWN_PLAYER").asKey().build();
    }
    return name;
  }

  private Message statisticToMessage() {
    return plugin.getMessageManager().getMessage("LEADERBOARD_STATISTICS_" + statistic.getName().toUpperCase());
  }

  public int getId() {
    return id;
  }

  public IStatisticType getStatistic() {
    return statistic;
  }

  public int getTopAmount() {
    return topAmount;
  }

  public ArmorStandHologram getHologram() {
    return hologram;
  }

  public Location getLocation() {
    return location;
  }
}
