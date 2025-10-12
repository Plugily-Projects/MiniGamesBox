package plugily.projects.minigamesbox.classic.commands.arguments.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.version.TextComponentBuilder;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 12.10.2025
 */
public class KitFileArgument {

  public KitFileArgument(PluginArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getCommandAdminPrefixLong(), new LabeledCommandArgument("kitfile", registry.getPlugin().getPluginNamePrefixLong() + ".admin.kitfile", CommandArgument.ExecutorType.BOTH,
        new LabelData("/" + registry.getPlugin().getCommandAdminPrefix() + " kitfile", "/" + registry.getPlugin().getCommandAdminPrefix() + " kitfile <name>",
            "&7Creates kit file \n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.kitfile")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        new MessageBuilder("Need a name!").send(sender);
        if (args.length != 2) {
          new MessageBuilder(ChatColor.DARK_RED + "Please provide a name!").prefix().send(sender);
          return;
        }
        if(!(sender instanceof Player)) {
          new MessageBuilder("COMMANDS_ONLY_BY_PLAYER").asKey().send(sender);
          return;
        }
        String name = args[1];
        registry.getPlugin().getKitRegistry().savePlayerAsNewKit(name, (Player) sender);
        new MessageBuilder("COMMANDS_COMMAND_EXECUTED").asKey().send(sender);
      }
    });
  }


}
