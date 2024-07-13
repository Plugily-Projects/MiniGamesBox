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

package plugily.projects.minigamesbox.classic.utils.version;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 04.08.2022
 */
//ToDo Additional Component
public class TextComponentBuilder {

  private String message;
  private ClickAction clickEventAction = null;
  private String clickEventValue = null;
  private HoverAction hoverEventAction = null;
  private String hoverEventValue = null;

  private boolean asKey = false;
  private Player player = null;
  private String value = null;
  private int integer = -99999;
  private PluginArena arena = null;

  private TextComponentBuilder additionalComponent = null;

  public TextComponentBuilder(String message) {
    this.message = message;
  }

  public TextComponentBuilder setClickEvent(ClickAction action, String value) {
    clickEventAction = action;
    clickEventValue = value;
    return this;
  }

  public enum ClickAction {
    OPEN_URL,
    OPEN_FILE,
    RUN_COMMAND,
    SUGGEST_COMMAND,
    CHANGE_PAGE,
    COPY_TO_CLIPBOARD
  }

  public enum HoverAction {
    SHOW_TEXT,
    SHOW_ITEM,
    SHOW_ENTITY,
    SHOW_ACHIEVEMENT,
  }

  public TextComponentBuilder setHoverEvent(HoverAction action, String value) {
    hoverEventAction = action;
    hoverEventValue = value;
    return this;
  }

  public TextComponentBuilder askey(boolean value) {
    this.asKey = value;
    return this;
  }

  public TextComponentBuilder player(Player player) {
    this.player = player;
    return this;
  }

  public TextComponentBuilder value(String value) {
    this.value = value;
    return this;
  }

  public TextComponentBuilder integer(int integer) {
    this.integer = integer;
    return this;
  }

  public TextComponentBuilder arena(PluginArena arena) {
    this.arena = arena;
    return this;
  }

  public void build() {
    MessageBuilder messageBuilder = new MessageBuilder(message);
    if(asKey) {
      messageBuilder.asKey();
    }
    if(player != null) {
      messageBuilder.player(player);
    }
    if(arena != null) {
      messageBuilder.arena(arena);
    }
    if(value != null) {
      messageBuilder.value(value);
    }
    if(integer != -99999) {
      messageBuilder.integer(integer);
    }
    message = messageBuilder.build();
  }

  public void sendPlayer() {
    build();
    if(message != null && !message.isEmpty() && player != null) {
      send(player);
    }
  }

  private void send(Player onlinePlayer) {
    if(ServerVersion.Version.isCurrentEqualOrLower(ServerVersion.Version.v1_12)) {
      onlinePlayer.sendRawMessage(message);
      return;
    }
    TextComponent component = new TextComponent(message);
    if(clickEventAction != null && clickEventValue != null) {
      component.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(String.valueOf(clickEventAction)), clickEventValue));
    }
    if(hoverEventAction != null && hoverEventValue != null) {
      if(ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16)) {
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(String.valueOf(hoverEventAction)), new Text(hoverEventValue)));
      } else {
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(String.valueOf(hoverEventAction)), TextComponent.fromLegacyText(hoverEventValue)));
      }
    }
    onlinePlayer.spigot().sendMessage(component);
  }

  public void sendArena() {
    build();
    if(message != null && !message.isEmpty() && arena != null) {
      for(Player arenaPlayer : arena.getPlayers()) {
        send(arenaPlayer);
      }
    }
  }

}
