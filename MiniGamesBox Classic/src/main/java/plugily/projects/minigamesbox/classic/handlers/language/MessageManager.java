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


package plugily.projects.minigamesbox.classic.handlers.language;

import plugily.projects.minigamesbox.classic.Main;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * all plugin messages accessors.
 * Used for getting game messages safely and for integrity checks.
 * <p>
 * Getting messages from config can be hard to control for thousands of strings
 * so we must use centralized method which is always constant.
 *
 * @author Tigerpanzer
 */
public class MessageManager {
  private final Main plugin;
  private final Map<String, Message> options = new HashMap<>();
  private final Map<String, Integer> customPermissions = new HashMap<>();

  public MessageManager(Main plugin) {
    this.plugin = plugin;
    loadMessages();
  }

  private void loadMessages() {
    Message.getMessages().forEach((s, message) -> options.put(s, new Message(message.getPath(), plugin.getConfig().getString(message.getPath(), message.getValue()), message.isProtected())));
  }

  /**
   * Returns message
   *
   * @param name message to get value from
   * @return Message
   */
  public Message getMessage(String name) {
    if(!options.containsKey(name)) {
      throw new IllegalStateException("Message with name " + name + " does not exist");
    }
    return options.get(name);
  }

  /**
   * Returns message path
   *
   * @param name message to get value from
   * @return String path
   */
  public String getPath(String name) {
    if(!options.containsKey(name)) {
      throw new IllegalStateException("Message with name " + name + " does not exist");
    }
    return options.get(name).getPath();
  }


  /**
   * Register a new messages
   *
   * @param name    The name of the message
   * @param message Contains the path and the default value
   */
  public void registerMessage(String name, Message message) {
    if(options.containsKey(name)) {
      throw new IllegalStateException("messages with name " + name + " was already registered");
    }
    options.put(name, message);
  }

  /**
   * Remove messages that are not protected
   *
   * @param name The name of the message
   */
  public void unregisterMessage(String name) {
    Message message = options.get(name);
    if(message == null) {
      return;
    }
    if(message.isProtected()) {
      throw new IllegalStateException("Protected messages cannot be removed!");
    }
    options.remove(name);
  }

  public Map<String, Message> getAllMessages() {
    return Collections.unmodifiableMap(options);
  }

}
