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

package plugily.projects.minigamesbox.classic.commands.arguments.admin;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.stats.IStatisticType;
import plugily.projects.minigamesbox.classic.api.StatisticType;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.hologram.LeaderboardHologram;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.number.NumberUtils;

import java.util.Locale;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class HologramArgument {

  private final PluginArgumentsRegistry registry;

  public HologramArgument(PluginArgumentsRegistry registry) {
    this.registry = registry;

    final String commandAdminPrefix = registry.getPlugin().getCommandAdminPrefix();

    registry.mapArgument(registry.getPlugin().getCommandAdminPrefixLong(), new LabeledCommandArgument("leaderboard", registry.getPlugin().getPluginNamePrefixLong() + ".admin.leaderboard.manage", CommandArgument.ExecutorType.PLAYER,
        new LabelData("/" + commandAdminPrefix + " leaderboard &6<action>", "/" + commandAdminPrefix + " leaderboard <action>", "&7Command handles 3 arguments:\n&7• /" + commandAdminPrefix + " leaderboard add <statistic type> <amount> - creates new hologram"
            + "of target statistic\n&7with top X amount of players (max 20)\n&7• /" + commandAdminPrefix + " hologram remove <id> - removes hologram of target ID\n"
            + "&7• /" + commandAdminPrefix + " leaderboard list - prints list of all leaderboard holograms")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
          new MessageBuilder("&cToo few arguments! Please type /" + commandAdminPrefix + " leaderboard <add/remove/list>").prefix().send(sender);
          return;
        }

        switch (args[1].toLowerCase(Locale.ENGLISH)) {
        case "add":
          handleAddArgument((Player) sender, args);
          break;
        case "list":
          handleListArgument(sender);
          break;
        case "remove":
          handleDeleteArgument(sender, args);
          break;
        default:
          new MessageBuilder("&cBad arguments! Please type /" + commandAdminPrefix + " leaderboard <add/remove/list>").prefix().send(sender);
        }
      }
    });
  }

  private void handleAddArgument(Player player, String[] args) {
    if(args.length != 4) {
      new MessageBuilder("&cToo few arguments! Please type /" + registry.getPlugin().getCommandAdminPrefix() + " leaderboard add <statistic type> <amount>").prefix().player(player).sendPlayer();
      return;
    }

    IStatisticType statistic;
    try {
      statistic = registry.getPlugin().getStatsStorage().getStatisticType(args[2].toUpperCase(Locale.ENGLISH));
    } catch(IllegalStateException ex) {
      sendInvalidStatisticMessage(player);
      return;
    }

    if(!statistic.isPersistent()) {
      sendInvalidStatisticMessage(player);
      return;
    }

    java.util.Optional<Integer> opt = NumberUtils.parseInt(args[3]);
    if(!opt.isPresent()) {
      new MessageBuilder("&cLeaderboard amount entries must be a number!").prefix().player(player).sendPlayer();
      return;
    }
    int amount = opt.get();
    if(amount <= 0 || amount > 20) {
      new MessageBuilder("&cLeaderboard amount entries amount are limited to 20 and minimum of 0!").prefix().player(player).sendPlayer();
      return;
    }

    FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "internal/leaderboards_data");
    int nextValue = config.getConfigurationSection("holograms").getKeys(false).size() + 1;
    config.set("holograms." + nextValue + ".statistics", statistic.getName());
    config.set("holograms." + nextValue + ".top-amount", amount);
    config.set("holograms." + nextValue + ".location", LocationSerializer.locationToString(player.getLocation()));
    ConfigUtils.saveConfig(registry.getPlugin(), config, "internal/leaderboards_data");

    LeaderboardHologram leaderboard = new LeaderboardHologram(registry.getPlugin(), nextValue, statistic, amount, player.getLocation());
    registry.getPlugin().getLeaderboardRegistry().registerHologram(leaderboard);
    new MessageBuilder("&aHologram with ID " + nextValue + " with statistic " + statistic.getName() + " added!").prefix().player(player).sendPlayer();
  }

  private void sendInvalidStatisticMessage(Player player) {
    StringBuilder values = new StringBuilder();
    for(IStatisticType value : registry.getPlugin().getStatsStorage().getStatistics().values()) {
      values.append(value.getName()).append(' ');
    }
    new MessageBuilder("&cInvalid statistic type! Valid types: &e" + values).prefix().player(player).sendPlayer();
  }

  private void handleListArgument(CommandSender sender) {
    FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "internal/leaderboards_data");
    new MessageBuilder("&aHOLOGRAMS").prefix().send(sender);
    for(String key : config.getConfigurationSection("holograms").getKeys(false)) {
      new MessageBuilder("&aID " + key).prefix().send(sender);
      new MessageBuilder("&eTop: " + config.getInt("holograms." + key + ".top-amount")
          + " Stat: " + config.getString("holograms." + key + ".statistics", "")).prefix().send(sender);
      new MessageBuilder("&eLocation: " + getFriendlyLocation(LocationSerializer.getLocation(config.getString("holograms." + key + ".location", null)))).prefix().send(sender);
    }
  }

  private String getFriendlyLocation(Location location) {
    return location == null ? "null" : "World: " + location.getWorld().getName() + ", X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ();
  }

  private void handleDeleteArgument(CommandSender sender, String[] args) {
    if(args.length != 3) {
      new MessageBuilder("&cPlease type leaderboard ID to remove it!").prefix().send(sender);
      return;
    }
    String id = args[2];
    java.util.Optional<Integer> opt = NumberUtils.parseInt(id);
    if(!opt.isPresent()) {
      new MessageBuilder("&cLeaderboard ID must be a number!").prefix().send(sender);
      return;
    }
    FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "internal/leaderboards_data");
    if(!config.isSet("holograms." + id)) {
      new MessageBuilder("&cLeaderboard with that ID doesn't exist!").prefix().send(sender);
      return;
    }
    config.set("holograms." + id, null);
    ConfigUtils.saveConfig(registry.getPlugin(), config, "internal/leaderboards_data");
    registry.getPlugin().getLeaderboardRegistry().disableHologram(opt.get());
    new MessageBuilder("&aLeaderboard with ID " + id + " sucessfully deleted!").prefix().send(sender);
  }

}
