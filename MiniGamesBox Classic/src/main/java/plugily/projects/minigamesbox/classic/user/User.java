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

package plugily.projects.minigamesbox.classic.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.api.StatisticType;
import plugily.projects.minigamesbox.classic.api.event.player.PlugilyPlayerStatisticChangeEvent;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.kits.basekits.Kit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class User {

  private static PluginMain plugin;
  private static long cooldownCounter = 0;
  private final UUID uuid;
  private boolean spectator = false;
  private boolean permanentSpectator = false;
  private Kit kit = plugin.getKitRegistry().getDefaultKit();
  private final Map<StatisticType, Integer> stats = new HashMap<>();
  private final Map<String, Long> cooldowns = new HashMap<>();

  @Deprecated
  public User(Player player) {
    this(player.getUniqueId());
  }

  public User(UUID uuid) {
    this.uuid = uuid;
  }

  public UUID getUniqueId() {
    return uuid;
  }

  public static void init(PluginMain plugin) {
    User.plugin = plugin;
  }

  public static void cooldownHandlerTask() {
    Bukkit.getScheduler().runTaskTimer(plugin, () -> cooldownCounter++, 20, 20);
  }

  public Kit getKit() {
    return kit;
  }

  public void setKit(Kit kit) {
    this.kit = kit;
  }

  public PluginArena getArena() {
    return plugin.getArenaRegistry().getArena(getPlayer());
  }

  public Player getPlayer() {
    return Bukkit.getPlayer(uuid);
  }

  public boolean isSpectator() {
    return spectator;
  }

  public void setSpectator(boolean b) {
    spectator = b;
  }

  public boolean isPermanentSpectator() {
    return permanentSpectator;
  }

  public void setPermanentSpectator(boolean permanentSpectator) {
    this.permanentSpectator = permanentSpectator;
  }

  public int getStat(String statistic) {
    StatisticType statisticType = plugin.getStatsStorage().getStatisticType(statistic);
    return stats.computeIfAbsent(statisticType, t -> 0);
  }

  public int getStat(StatisticType s) {
    return stats.computeIfAbsent(s, t -> 0);
  }

  public void setStat(StatisticType s, int i) {
    stats.put(s, i);

    //statistics manipulation events are called async when using mysql
    Bukkit.getScheduler().runTask(plugin, () -> {
      Player player = getPlayer();
      Bukkit.getPluginManager().callEvent(new PlugilyPlayerStatisticChangeEvent(plugin.getArenaRegistry().getArena(player), player, s, i));
    });
  }

  public void addStat(StatisticType s, int i) {
    stats.put(s, getStat(s) + i);

    //statistics manipulation events are called async when using mysql
    Bukkit.getScheduler().runTask(plugin, () -> {
      Player player = getPlayer();
      Bukkit.getPluginManager().callEvent(new PlugilyPlayerStatisticChangeEvent(plugin.getArenaRegistry().getArena(player), player, s, getStat(s)));
    });
  }

  public void setStat(String statistic, int i) {
    StatisticType statisticType = plugin.getStatsStorage().getStatisticType(statistic);
    stats.put(statisticType, i);

    //statistics manipulation events are called async when using mysql
    Bukkit.getScheduler().runTask(plugin, () -> {
      Player player = getPlayer();
      Bukkit.getPluginManager().callEvent(new PlugilyPlayerStatisticChangeEvent(plugin.getArenaRegistry().getArena(player), player, statisticType, i));
    });
  }

  public void addStat(String statistic, int i) {
    StatisticType statisticType = plugin.getStatsStorage().getStatisticType(statistic);
    stats.put(statisticType, getStat(statisticType) + i);

    //statistics manipulation events are called async when using mysql
    Bukkit.getScheduler().runTask(plugin, () -> {
      Player player = getPlayer();
      Bukkit.getPluginManager().callEvent(new PlugilyPlayerStatisticChangeEvent(plugin.getArenaRegistry().getArena(player), player, statisticType, getStat(statisticType)));
    });
  }

  public boolean checkCanCastCooldownAndMessage(String cooldown) {
    long time = getCooldown(cooldown);

    if(time <= 0) {
      return true;
    }

    String message = plugin.getChatManager().colorMessage("KIT_COOLDOWN");
    message = message.replaceFirst("%cooldown%", Long.toString(time));
    getPlayer().sendMessage(message);
    return false;
  }

  public void setCooldown(String s, int seconds) {
    cooldowns.put(s, seconds + cooldownCounter);
  }

  public long getCooldown(String s) {
    long coold = cooldowns.getOrDefault(s, 0L);
    return coold <= cooldownCounter ? 0 : coold - cooldownCounter;
  }

}
