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

package plugily.projects.minigamesbox.classic.commands.arguments;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.commands.arguments.admin.*;
import plugily.projects.minigamesbox.classic.commands.arguments.admin.arena.DeleteArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.admin.arena.ForceStartArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.admin.arena.ReloadArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.admin.arena.StopArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.admin.statistic.AdjustStatisticArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.game.*;
import plugily.projects.minigamesbox.classic.commands.completion.TabCompletion;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.version.ServerVersion;
import plugily.projects.minigamesbox.classic.utils.version.TextComponentBuilder;
import plugily.projects.minigamesbox.string.StringMatcher;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class PluginArgumentsRegistry implements CommandExecutor {

  private final SpyChatArgument spyChat;
  private final PluginMain plugin;
  private final TabCompletion tabCompletion;
  private final Map<String, List<CommandArgument>> mappedArguments = new HashMap<>();

  public PluginArgumentsRegistry(PluginMain plugin) {
    this.plugin = plugin;
    tabCompletion = new TabCompletion(this);
    Optional.ofNullable(plugin.getCommand(plugin.getPluginNamePrefixLong())).ifPresent(plugily -> {
      plugily.setExecutor(this);
      plugily.setTabCompleter(tabCompletion);
    });
    Optional.ofNullable(plugin.getCommand(plugin.getCommandAdminPrefixLong())).ifPresent(plugilyadmin -> {
      plugilyadmin.setExecutor(this);
      plugilyadmin.setTabCompleter(tabCompletion);
    });

    //register basic arguments
    new SetupArgument(this);
    new JoinArguments(this);
    new ArenaSelectorArgument(this);
    new LeaderboardArgument(this);
    new LeaveArgument(this);
    if(plugin.getConfigPreferences().getOption("KITS")) {
      new SelectKitArgument(this);
    }
    new StatsArgument(this);

    //register admin arguments
    //arena related arguments
    new DeleteArgument(this);
    new ForceStartArgument(this);
    new ReloadArgument(this);
    new StopArgument(this);

    //player statistic related arguments
    new AdjustStatisticArgument(this);

    //other admin related arguments
    new ListArenasArgument(this);
    new LocationWandArgument(this);
    new PlaceholderCheckArgument(this);
    new LocalesArgument(this);
    new LocaleArgument(this);
    spyChat = new SpyChatArgument(this);
    new TeleportArgument(this);
    if(plugin.getConfigPreferences().getOption("LEADERBOARDS")) {
      new HologramArgument(this);
    }
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    for(Map.Entry<String, List<CommandArgument>> entry : mappedArguments.entrySet()) {
      if(!cmd.getName().equalsIgnoreCase(entry.getKey())) {
        continue;
      }
      if(cmd.getName().equalsIgnoreCase(plugin.getPluginNamePrefixLong())) {
        if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
          sendHelpCommand(sender);
          return true;
        }
        if(args.length > 1 && args[0].equalsIgnoreCase("edit")) {
          if(!checkSenderIsExecutorType(sender, CommandArgument.ExecutorType.PLAYER)
              || !plugin.getBukkitHelper().hasPermission(sender, plugin.getPluginNamePrefixLong() + ".admin.setup")) {
            return true;
          }
          IPluginArena arena = plugin.getArenaRegistry().getArena(args[1]);
          if(arena == null) {
            new MessageBuilder("COMMANDS_NO_ARENA_LIKE_THAT").asKey().send(sender);
            return true;
          }
          plugin.getSetupInventory((Player) sender, arena.getId()).open();
          return true;
        }
      }
      if(cmd.getName().equalsIgnoreCase(plugin.getCommandAdminPrefixLong()) && (args.length == 0 || args[0].equalsIgnoreCase("help"))) {
        if(!sender.hasPermission(plugin.getPluginNamePrefixLong() + ".admin")) {
          return true;
        }
        sendAdminHelpCommand(sender);
        return true;
      }
      for(CommandArgument argument : entry.getValue()) {
        if(argument.getArgumentName().equalsIgnoreCase(args[0])) {
          //does it make sense that it is a list?
          for(String perm : argument.getPermissions()) {
            if(perm.isEmpty() || plugin.getBukkitHelper().hasPermission(sender, perm)) {
              break;
            }
            //user has no permission to execute command
            return true;
          }
          if(checkSenderIsExecutorType(sender, argument.getValidExecutors())) {
            argument.execute(sender, args);
          }
          //return true even if sender is not good executor or hasn't got permission
          return true;
        }
      }

      //sending did you mean help
      List<StringMatcher.Match> matches = StringMatcher.match(args[0], entry.getValue().stream().map(CommandArgument::getArgumentName).collect(Collectors.toList()));
      if(!matches.isEmpty()) {
        new MessageBuilder("COMMANDS_DID_YOU_MEAN").asKey().value(label + " " + matches.get(0).getMatch()).send(sender);
        return true;
      }
    }
    return false;
  }

  private void sendHelpCommand(CommandSender sender) {
    new MessageBuilder("COMMANDS_MAIN_HEADER").asKey().send(sender);
    List<String> description = plugin.getLanguageManager().getLanguageListFromKey("COMMANDS_MAIN_DESCRIPTION");
    description.forEach(string -> new MessageBuilder(string).send(sender));


    if(sender.hasPermission(plugin.getPluginNamePrefixLong() + ".admin")) {
      new MessageBuilder("COMMANDS_MAIN_ADMIN_BONUS_DESCRIPTION").asKey().send(sender);
      sendAdminHelpCommand(sender);
    }
    new MessageBuilder("COMMANDS_MAIN_FOOTER").asKey().send(sender);
  }

  private void sendAdminHelpCommand(CommandSender sender) {
    sender.sendMessage(ChatColor.GREEN + "  " + ChatColor.BOLD + plugin.getPluginNamePrefixLong().toUpperCase() + " " + ChatColor.GRAY + plugin.getDescription().getVersion());
    sender.sendMessage(ChatColor.RED + " []" + ChatColor.GRAY + " = optional  " + ChatColor.GOLD + "<>" + ChatColor.GRAY + " = required");

    boolean senderIsPlayer = sender instanceof Player;

    if(senderIsPlayer) {
      sender.sendMessage(ChatColor.GRAY + "Hover command to see more, click command to suggest it.");
    }

    List<LabelData> data = mappedArguments.get(plugin.getCommandAdminPrefixLong()).stream().filter(arg -> arg instanceof LabeledCommandArgument)
        .map(arg -> ((LabeledCommandArgument) arg).getLabelData()).collect(Collectors.toList());
    data.addAll(mappedArguments.get(plugin.getPluginNamePrefixLong()).stream().filter(arg -> arg instanceof LabeledCommandArgument)
        .map(arg -> ((LabeledCommandArgument) arg).getLabelData()).collect(Collectors.toList()));

    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_11_R1)) {
      for(LabelData labelData : data) {
        sender.sendMessage(labelData.getText() + " - " + labelData.getDescription().split("\n", 2)[0]);
      }
      return;
    }

    for(LabelData labelData : data) {
      if(senderIsPlayer) {
        new TextComponentBuilder(labelData.getText()).player((Player) sender)
            .setClickEvent(TextComponentBuilder.ClickAction.SUGGEST_COMMAND, labelData.getCommand())
            .setHoverEvent(TextComponentBuilder.HoverAction.SHOW_TEXT, labelData.getDescription())
            .sendPlayer();

      } else {
        //more descriptive for console - split at \n to show only basic description
        plugin.getDebugger().sendConsoleMsg(labelData.getText() + " - " + labelData.getDescription().split("\n", 2)[0]);
      }
    }
  }

  private boolean checkSenderIsExecutorType(CommandSender sender, CommandArgument.ExecutorType type) {
    switch(type) {
      case BOTH:
        return sender instanceof ConsoleCommandSender || sender instanceof Player;
      case CONSOLE:
        return sender instanceof ConsoleCommandSender;
      case PLAYER:
        if(sender instanceof Player) {
          return true;
        }
        new MessageBuilder("COMMANDS_ONLY_BY_PLAYER").asKey().send(sender);
        return false;
      default:
        return false;
    }
  }

  /**
   * Maps new argument to the main command
   *
   * @param mainCommand mother command ex. /mm
   * @param argument    argument to map ex. leave (for /mm leave)
   */
  public void mapArgument(String mainCommand, CommandArgument argument) {
    List<CommandArgument> args = mappedArguments.getOrDefault(mainCommand, new ArrayList<>());
    args.add(argument);
    mappedArguments.put(mainCommand, args);
  }

  public Map<String, List<CommandArgument>> getMappedArguments() {
    return mappedArguments;
  }

  public PluginMain getPlugin() {
    return plugin;
  }

  public TabCompletion getTabCompletion() {
    return tabCompletion;
  }

  public SpyChatArgument getSpyChat() {
    return spyChat;
  }

  public List<IPluginArena> getSpecificFilteredArenas(List<IPluginArena> arenas, String filter) {
    return arenas;
  }
}
