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

package plugily.projects.minigamesbox.classic.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.stats.IStatisticType;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.api.event.player.PlugilyPlayerStatisticChangeEvent;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
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
  private Kit kit;
  private final Map<IStatisticType, Integer> stats = new HashMap<>();
  private final Map<String, Double> cooldowns = new HashMap<>();
  private boolean initialized;

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
    if(kit == null) {
      return plugin.getKitRegistry().getDefaultKit();
    }
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

  public void setSpectator(boolean spectator) {
    this.spectator = spectator;
  }

  public boolean isPermanentSpectator() {
    return permanentSpectator;
  }

  public void setPermanentSpectator(boolean permanentSpectator) {
    this.permanentSpectator = permanentSpectator;
  }

  public int getStatistic(String statistic) {
    return getStatistic(plugin.getStatsStorage().getStatisticType(statistic.toUpperCase()));
  }

  public int getStatistic(IStatisticType statisticType) {
    return stats.computeIfAbsent(statisticType, t -> 0);
  }

  public void setStatistic(IStatisticType statisticType, int value) {
    changeUserStatistic(statisticType, value);
  }

  public void setStatistic(String statistic, int value) {
    changeUserStatistic(plugin.getStatsStorage().getStatisticType(statistic), value);
  }

  private void changeUserStatistic(IStatisticType statisticType, int value) {
    stats.put(statisticType, value);

    Player player = getPlayer();

    if(player != null) {
      plugin.getDebugger().debug("Set User {0} statistic to {1} for {2} ", statisticType.getName(), value, player.getName());

      //statistics manipulation events are called async when using mysql
      Bukkit.getScheduler().runTask(plugin, () -> {
        Bukkit.getPluginManager().callEvent(new PlugilyPlayerStatisticChangeEvent(plugin.getArenaRegistry().getArena(player), player, statisticType, value));
      });
    }
  }

  public void adjustStatistic(IStatisticType statisticType, int value) {
    changeUserStatistic(statisticType, getStatistic(statisticType) + value);
  }

  public void adjustStatistic(String statistic, int value) {
    IStatisticType statisticType = plugin.getStatsStorage().getStatisticType(statistic);
    changeUserStatistic(statisticType, getStatistic(statisticType) + value);
  }

  public void resetNonePersistentStatistics() {
    for(IStatisticType statisticType : plugin.getStatsStorage().getStatistics().values()) {
      if(!statisticType.isPersistent()) {
        setStatistic(statisticType, 0);
      }
    }
  }

  public boolean checkCanCastCooldownAndMessage(String cooldown) {
    double time = getCooldown(cooldown);

    if(time <= 0) {
      return true;
    }
    new MessageBuilder("KIT_COOLDOWN").asKey().integer((int) time).player(getPlayer()).sendPlayer();
    return false;
  }

  public void setCooldown(String key, double seconds) {
    cooldowns.put(key, seconds + cooldownCounter);
  }

  public double getCooldown(String key) {
    double cooldown = cooldowns.getOrDefault(key, 0.0);
    return cooldown <= cooldownCounter ? 0 : cooldown - cooldownCounter;
  }

  public boolean isInitialized() {
    return initialized;
  }

  public void setInitialized(boolean initialized) {
    this.initialized = initialized;
  }
}
