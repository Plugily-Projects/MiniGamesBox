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

package plugily.projects.minigamesbox.classic.handlers.reward;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.api.handlers.reward.IReward;
import plugily.projects.minigamesbox.api.handlers.reward.IRewardType;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.engine.ScriptEngineHandler;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 07.10.2021
 */
public class RewardsFactory {

  private final Set<Reward> rewards = new HashSet<>();
  private final Map<String, RewardType> rewardTypes = new HashMap<>();
  private final FileConfiguration config;
  private final boolean enabled;
  private final PluginMain plugin;

  public RewardsFactory(PluginMain plugin) {
    enabled = plugin.getConfigPreferences().getOption("REWARDS");
    config = ConfigUtils.getConfig(plugin, "rewards");
    this.plugin = plugin;
    loadRewardTypes();
    registerRewards();
  }


  private void loadRewardTypes() {
    rewardTypes.putAll(RewardType.getRewardTypes());
  }

  /**
   * Returns RewardType
   *
   * @param key to get value from
   * @return RewardType
   */
  public RewardType getRewardType(String key) {
    RewardType rewardType = rewardTypes.get(key);

    if(rewardType == null) {
      throw new IllegalStateException("RewardType with name " + key + " does not exist");
    }

    return rewardType;
  }

  /**
   * Register a new config option
   *
   * @param key        The name of the Option
   * @param rewardType Contains the path and values
   */
  public void registerRewardType(String key, RewardType rewardType) {
    if(rewardTypes.containsKey(key)) {
      throw new IllegalStateException("RewardType with path " + key + " was already registered");
    }
    rewardTypes.put(key, rewardType);
    rewards.clear();
    registerRewards();
  }

  /**
   * Remove RewardTypes that are not protected
   *
   * @param key of the RewardType
   */
  public void unregisterRewardType(String key) {
    RewardType rewardType = rewardTypes.get(key);
    if(rewardType == null) {
      return;
    }
    if(rewardType.isProtected()) {
      throw new IllegalStateException("Protected rewardtype cannot be removed!");
    }
    rewardTypes.remove(key);
    rewards.clear();
    registerRewards();
  }

  public Map<String, IRewardType> getRewardTypes() {
    return Collections.unmodifiableMap(rewardTypes);
  }


  public void performReward(IPluginArena arena, IRewardType type) {
    if(!enabled) {
      return;
    }
    for(Player p : arena.getPlayers()) {
      performReward(p, type);
    }
  }


  public void performReward(Player player, IRewardType type) {
    performReward(player, null, type);
  }

  public void performReward(Player player, IPluginArena arena, IRewardType type) {
    performReward(player, arena, type, -1);
  }

  public void performReward(Player player, Set<IReward> rewards) {
    performReward(player, null, rewards);
  }

  public void performReward(Player player, IPluginArena arena, Set<IReward> rewards) {
    if(arena == null && player != null)
      arena = plugin.getArenaRegistry().getArena(player);
    for(IReward reward : rewards) {
      if(reward == null) {
        continue;
      }
      executeReward(player, arena, reward);
    }
  }

  private void executeReward(Player player, IPluginArena arena, IReward reward) {
    //cannot execute if chance wasn't met
    plugin.getDebugger().debug("Trying to perform reward {0} as {1} executor", reward.getType(), reward.getExecutor());
    if(reward.getChance() != -1 && ThreadLocalRandom.current().nextInt(0, 100) > reward.getChance()) {
      plugin.getDebugger().debug("Reward {0} did not execute because chance is {1}", reward.getType(), reward.getChance());
      return;
    }
    String command = reward.getExecutableCode();
    if(command == null || command.isEmpty()) {
      plugin.getDebugger().debug("Reward {0} did not execute because there is no ExecutableCode {1}", reward.getType(), command);
      return;
    }
    MessageBuilder messageBuilder = new MessageBuilder(command, false);
    if(player != null) {
      plugin.getDebugger().debug("Reward {0} added player as argument", reward.getType());
      messageBuilder = messageBuilder.player(player);
    }
    if(arena != null) {
      plugin.getDebugger().debug("Reward {0} added arena as argument", reward.getType());
      messageBuilder = messageBuilder.arena(arena);
    }
    command = messageBuilder.build();
    switch(reward.getExecutor()) {
      case CONSOLE:
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        break;
      case PLAYER:
        if(player != null) {
          player.performCommand(command);
        }
        break;
      case SCRIPT:
        ScriptEngineHandler engine = new ScriptEngineHandler(plugin);
        if(player != null) {
          engine.setValue("player", player);
        }
        engine.setValue("server", Bukkit.getServer());
        if(arena != null) {
          engine.setValue("arena", arena);
        }
        engine.setValue("plugin", plugin);
        engine.execute(command);
        break;
      default:
        break;
    }
    plugin.getDebugger().debug("Reward {0} Executed command {1} as {2} executor", reward.getType(), command, reward.getExecutor());
  }

  public void performReward(Player player, IPluginArena arena, IRewardType type, int executeNumber) {
    if(!enabled) {
      return;
    }
    if(!config.contains("rewards")) {
      plugin.getDebugger().debug(Level.WARNING, "[RewardsFactory] Rewards section not found in the file. Rewards won't be loaded.");
      return;
    }

    if(arena == null && player != null)
      arena = plugin.getArenaRegistry().getArena(player);


    for(Reward reward : rewards) {
      if(reward.getType() == type) {
        //reward isn't a number executor
        if(type.getExecutorType() == RewardType.ExecutorType.NUMBER && reward.getNumberExecute() != executeNumber) {
          continue;
        }
        executeReward(player, arena, reward);
      }
    }
  }

  private void registerRewards() {
    if(!enabled) {
      return;
    }
    plugin.getDebugger().debug("[RewardsFactory] Starting rewards registration");
    long start = System.currentTimeMillis();

    Map<RewardType, Integer> registeredRewards = new HashMap<>();
    for(RewardType rewardType : rewardTypes.values()) {
      if(config.isConfigurationSection("rewards." + rewardType.getPath())) {
        ConfigurationSection section = config.getConfigurationSection("rewards." + rewardType.getPath());
        addNumberReward(registeredRewards, rewardType, section);
        plugin.getDebugger().debug(Level.INFO, "Rewards section {0} found. Registering as number reward", rewardType.getPath());
      } else {
        addReward(registeredRewards, rewardType);
        plugin.getDebugger().debug(Level.INFO, "Rewards section {0} not configured. Registering as normal reward", rewardType.getPath());
      }
    }
    for(Map.Entry<RewardType, Integer> entry : registeredRewards.entrySet()) {
      plugin.getDebugger().debug("[RewardsFactory] Registered {0} {1} rewards!", entry.getValue(), entry.getKey().getPath());
    }
    plugin.getDebugger().debug("[RewardsFactory] Registered all rewards took {0}ms", System.currentTimeMillis() - start);
  }

  private void addReward(Map<RewardType, Integer> registeredRewards, RewardType rewardType) {
    for(String reward : config.getStringList("rewards." + rewardType.getPath())) {
      rewards.add(new Reward(rewardType, reward));
      registeredRewards.put(rewardType, registeredRewards.getOrDefault(rewardType, 0) + 1);
    }
  }

  private void addNumberReward(Map<RewardType, Integer> registeredRewards, RewardType rewardType, ConfigurationSection section) {
    for(String key : section.getKeys(false)) {
      for(String reward : section.getStringList(key)) {
        rewards.add(new Reward(rewardType, reward, Integer.parseInt(key)));
        registeredRewards.put(rewardType, registeredRewards.getOrDefault(rewardType, 0) + 1);
      }
    }
  }

}
