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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.placeholder.Placeholder;
import plugily.projects.minigamesbox.classic.utils.services.locale.Locale;
import plugily.projects.minigamesbox.classic.utils.services.locale.LocaleRegistry;
import plugily.projects.minigamesbox.classic.utils.version.TextComponentBuilder;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 13.05.2022
 */
public class LocalesArgument {

  public LocalesArgument(PluginArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getCommandAdminPrefixLong(), new LabeledCommandArgument("locales", registry.getPlugin().getPluginNamePrefixLong() + ".admin.locales", CommandArgument.ExecutorType.BOTH, new LabelData("/" + registry.getPlugin().getCommandAdminPrefix() + " locales", "/" + registry.getPlugin().getCommandAdminPrefix() + " locales", "&7Shows list with all locales\n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.locales")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        new MessageBuilder("---- LOCALES THAT CAN BE USED FOR " + registry.getPlugin().getPluginNamePrefixLong().toUpperCase() + " ----").send(sender);
        for(Locale locale : LocaleRegistry.getRegisteredLocales()) {
          new MessageBuilder(locale.getName() + " by setting " + locale.getPrefix() + " in config.yml@locale").send(sender);
        }
      }
    });
  }

}
