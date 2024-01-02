package plugily.projects.minigamesbox.classic.commands.arguments.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.services.locale.Locale;

public class LocaleArgument {

  public LocaleArgument(PluginArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getCommandAdminPrefixLong(),
        new LabeledCommandArgument("locale", registry.getPlugin().getCommandAdminPrefixLong() + ".admin.locale",
            CommandArgument.ExecutorType.PLAYER,
            new LabelData("/" + registry.getPlugin().getCommandAdminPrefix() + " locale &c[locale name/prefix]",
                "/" + registry.getPlugin().getCommandAdminPrefixLong() + " locale &c[locale name/prefix]",
                "&7Used for changing locale \n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.locale")) {
          @Override
          public void execute(CommandSender sender, String[] args) {
            Player player = (Player) sender;
            if (args.length != 2) {
              new MessageBuilder(ChatColor.DARK_RED + "Please provide a locale name/prefix!").prefix().send(player);
              return;
            }
            String localeName = args[1];
            Locale locale = Locale.getLocale(localeName);
            if (locale == null) {
              new MessageBuilder(ChatColor.DARK_RED + "Locale not found!").prefix().send(player);
              return;
            }
            registry.getPlugin().getConfig().set("locale", locale.getPrefix());
            registry.getPlugin().saveConfig();

            registry.getPlugin().getLanguageManager().setupLocale();
            new MessageBuilder(ChatColor.GREEN + "Locale changed to "+locale.getPrefix()+"! Make sure to make a proper restart of your server.").prefix().send(player);
          }
        });
  }

}