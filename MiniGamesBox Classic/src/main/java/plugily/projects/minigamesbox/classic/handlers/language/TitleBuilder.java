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

package plugily.projects.minigamesbox.classic.handlers.language;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.api.arena.IPluginArena;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.number.NumberUtils;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 05.03.2022
 */
public class TitleBuilder {

  private String message;
  private String title;
  private String subTitle;
  private int fadeIn;
  private int stay;
  private int fadeOut;
  private Player player;
  private String value;
  private int integer = -99;
  private IPluginArena arena;
  private static PluginMain plugin;


  public TitleBuilder(String message) {
    this.message = message;
  }

  public static void init(PluginMain plugin) {
    TitleBuilder.plugin = plugin;
  }

  public TitleBuilder asKey() {
    message = plugin.getLanguageManager().getLanguageMessage(plugin.getMessageManager().getPath(message));
    return this;
  }

  public TitleBuilder player(Player player) {
    this.player = player;
    return this;
  }

  public TitleBuilder value(String value) {
    this.value = value;
    return this;
  }

  public TitleBuilder integer(int integer) {
    this.integer = integer;
    return this;
  }

  public TitleBuilder arena(IPluginArena arena) {
    this.arena = arena;
    return this;
  }

  private void sendTitles(Player player) {
    split();
    if(title != null && !title.isEmpty() && subTitle != null && !subTitle.isEmpty()) {
      VersionUtils.sendTitles(player, title, subTitle, fadeIn, stay, fadeOut);
      return;
    }
    if(title != null && !title.isEmpty()) {
      VersionUtils.sendTitle(player, title, fadeIn, stay, fadeOut);
      return;
    }
    if(subTitle != null && !subTitle.isEmpty()) {
      VersionUtils.sendSubTitle(player, subTitle, fadeIn, stay, fadeOut);
    }
  }


  public void send(Player player) {
    sendTitles(player);
  }

  public void send(PluginArena arena) {
    for(Player arenaPlayer : arena.getPlayers()) {
      sendTitles(arenaPlayer);
    }
  }

  public void sendPlayer() {
    if(player != null) {
      sendTitles(player);
    }
  }

  public void sendArena() {
    if(arena != null) {
      for(Player arenaPlayer : arena.getPlayers()) {
        sendTitles(arenaPlayer);
      }
    }
  }

  public void broadcast() {
    for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      sendTitles(onlinePlayer);
    }
  }


  private void split() {
    String[] split = message.split(";", 3);
    String[] times = split.length >= 1 ? split[0].split(",", 3) : new String[]{""};
    fadeIn = times.length > 1 ? NumberUtils.parseInt(times[0].replace(" ", "")).orElse(20) : 20;
    stay = times.length > 2 ? NumberUtils.parseInt(times[1].replace(" ", "")).orElse(30) : 30;
    fadeOut = times.length > 3 ? NumberUtils.parseInt(times[2].replace(" ", "")).orElse(20) : 20;
    title = split.length >= 2 ? buildMessage(split[1]) : "";
    subTitle = split.length >= 3 ? buildMessage(split[2]) : "";
  }

  private String buildMessage(String message) {
    MessageBuilder messageBuilder = new MessageBuilder(message);
    if(player != null) {
      messageBuilder = messageBuilder.player(player);
    }
    if(arena != null) {
      messageBuilder = messageBuilder.arena(arena);
    }
    if(value != null) {
      messageBuilder = messageBuilder.value(value);
    }
    if(integer != -99) {
      messageBuilder = messageBuilder.integer(integer);
    }
    return messageBuilder.build();
  }
}
