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

package plugily.projects.minigamesbox.classic.commands.arguments.admin;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.commands.arguments.PluginArgumentsRegistry;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabelData;
import plugily.projects.minigamesbox.classic.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.placeholder.Placeholder;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 13.05.2022
 */
public class PlaceholderCheckArgument {

  public PlaceholderCheckArgument(PluginArgumentsRegistry registry) {
    registry.mapArgument(registry.getPlugin().getCommandAdminPrefixLong(), new LabeledCommandArgument("placeholders", registry.getPlugin().getPluginNamePrefixLong() + ".admin.placeholders", CommandArgument.ExecutorType.BOTH,
        new LabelData("/" + registry.getPlugin().getCommandAdminPrefix() + " placeholders", "/" + registry.getPlugin().getCommandAdminPrefix() + " placeholders",
            "&7Shows list with all loaded placeholders\n&6Permission: &7" + registry.getPlugin().getPluginNamePrefixLong() + ".admin.placeholders")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
          return;
        }
        new MessageBuilder("---- PLACEHOLDERS OF " + registry.getPlugin().getPluginNamePrefixLong().toUpperCase() + " ----").send(sender);
        new MessageBuilder("---- INTERNAL PLACEHOLDERS ----").send(sender);
        for(Placeholder placeholder : registry.getPlugin().getPlaceholderManager().getRegisteredInternalPlaceholders()) {
          String listMessage = new MessageBuilder("&aID #" + placeholder.getId() + "# &bTYPE " + placeholder.getPlaceholderType() + " &cEXECUTOR " + placeholder.getPlaceholderExecutor()).player((Player) sender).arena(registry.getPlugin().getArenaRegistry().getArenas().get(0)).build();
          TextComponent component = new TextComponent(listMessage);
          component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("%" + placeholder.getId() + "%")));
          component.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "%" + placeholder.getId() + "%"));
          ((Player) sender).spigot().sendMessage(component);
        }
        new MessageBuilder("---- EXTERNAL (PAPI) PLACEHOLDERS ----").send(sender);
        for(Placeholder placeholder : registry.getPlugin().getPlaceholderManager().getRegisteredPAPIPlaceholders()) {
          String listMessage = new MessageBuilder("&aID #" + registry.getPlugin().getPluginNamePrefixLong() + placeholder.getId() + "# &bTYPE " + placeholder.getPlaceholderType() + " &cEXECUTOR " + placeholder.getPlaceholderExecutor()).player((Player) sender).arena(registry.getPlugin().getArenaRegistry().getArenas().get(0)).build();
          TextComponent component = new TextComponent(listMessage);
          component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("%" + registry.getPlugin().getPluginNamePrefixLong() + placeholder.getId() + "%")));
          component.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "%" + registry.getPlugin().getPluginNamePrefixLong() + placeholder.getId() + "%"));
          ((Player) sender).spigot().sendMessage(component);
        }
      }
    });
  }

}
