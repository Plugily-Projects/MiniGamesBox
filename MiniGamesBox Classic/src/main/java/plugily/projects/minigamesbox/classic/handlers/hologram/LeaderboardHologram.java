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


package plugily.projects.minigamesbox.classic.handlers.hologram;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import plugily.projects.minigamesbox.classic.Main;
import plugily.projects.minigamesbox.classic.api.StatisticType;
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
public class LeaderboardHologram extends BukkitRunnable {

  private final Main plugin;
  private final int id;
  private final StatisticType statistic;
  private final int topAmount;
  private final ArmorStandHologram hologram;
  private final Location location;

  public LeaderboardHologram(Main plugin, int id, StatisticType statistic, int amount, Location location) {
    this.plugin = plugin;
    this.id = id;
    this.statistic = statistic;
    this.topAmount = amount;
    this.location = location;
    this.hologram = new ArmorStandHologram(location);

    String header = color(plugin.getLanguageConfig().getString(LanguageMessage.HOLOGRAMS_HEADER.getAccessor()));
    header = StringUtils.replace(header, "%amount%", Integer.toString(topAmount));

    LanguageMessage lm = statisticToMessage();
    header = StringUtils.replace(header, "%statistic%", lm != null ? color(plugin.getLanguageConfig().getString(lm.getAccessor())) : "null");
    hologram.appendLine(header);


  }

  //todo performance!! use stat update event instead!
  public void initUpdateTask() {
    runTaskTimerAsynchronously(plugin, 0, 100);
  }

  @Override
  public void run() {
    if(!plugin.isEnabled()) {
      cancel();
      return;
    }

    java.util.Map<UUID, Integer> values = plugin.getStatsStorage().getStats(statistic);
    List<UUID> reverseKeys = new ArrayList<>(values.keySet());
    Collections.reverse(reverseKeys);

    List<String> update = new ArrayList<>();

    for(int i = 0; i < topAmount; i++) {
      String text;
      if(i < reverseKeys.size()) {
        UUID uuid = reverseKeys.get(i);
        text = color(plugin.getLanguageConfig().getString(LanguageMessage.HOLOGRAMS_FORMAT.getAccessor()));
        text = StringUtils.replace(text, "%nickname%", getPlayerNameSafely(uuid));
        text = StringUtils.replace(text, "%value%", String.valueOf(values.get(uuid)));
      } else {
        text = color(plugin.getLanguageConfig().getString(LanguageMessage.HOLOGRAMS_FORMAT_EMPTY.getAccessor()));
      }
      text = StringUtils.replace(text, "%place%", Integer.toString(i + 1));
      update.add(text);
    }

    hologram.appendLines(update);
  }

  @Override
  public synchronized void cancel() {
    super.cancel();
    hologram.delete();
  }

  private String getPlayerNameSafely(UUID uuid) {
    String name = plugin.getUserManager().getDatabase().getPlayerName(uuid);
    return name != null ? name : color(plugin.getLanguageConfig().getString(LanguageMessage.HOLOGRAMS_UNKNOWN_PLAYER.getAccessor()));
  }

  private LanguageMessage statisticToMessage() {
    switch(statistic.getName()) {
      case "KILLS":
        return LanguageMessage.STATISTIC_KILLS;
      case "DEATHS":
        return LanguageMessage.STATISTIC_DEATHS;
      case "GAMES_PLAYED":
        return LanguageMessage.STATISTIC_GAMES_PLAYED;
      case "LEVEL":
        return LanguageMessage.STATISTIC_LEVEL;
      case "XP":
        return LanguageMessage.STATISTIC_EXP;
      default:
        return null;
    }
  }

  public int getId() {
    return id;
  }

  public StatisticType getStatistic() {
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

  private String color(String message) {
    return plugin.getChatManager().colorRawMessage(message);
  }
}
