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

package plugily.projects.minigamesbox.classic.utils.actionbar;

import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 02.09.2022
 */
public class ActionBar {

  private final MessageBuilder message;

  private String key;

  private final ActionBarType actionBarType;
  private final int priority;
  private final int ticks;

  private int executedTicks = 0;

  public ActionBar(MessageBuilder message, ActionBarType actionBarType, int priority, int ticks) {
    this.message = message;
    this.actionBarType = actionBarType;
    this.priority = priority;
    this.ticks = ticks;
  }

  public ActionBar(MessageBuilder message, ActionBarType actionBarType, int priority, double seconds) {
    this.message = message;
    this.actionBarType = actionBarType;
    this.priority = priority;
    this.ticks = (int) (seconds * 20);
  }

  public ActionBar(MessageBuilder message, ActionBarType actionBarType, int priority) {
    this.message = message;
    this.actionBarType = actionBarType;
    this.priority = priority;
    this.ticks = 40;
  }

  public ActionBar(MessageBuilder message, ActionBarType actionBarType, double seconds) {
    this.message = message;
    this.actionBarType = actionBarType;
    this.priority = 5;
    this.ticks = (int) (seconds * 20);
  }

  public ActionBar(MessageBuilder message, ActionBarType actionBarType) {
    this.message = message;
    this.actionBarType = actionBarType;
    this.priority = 0;
    this.ticks = 40;
  }

  public ActionBar(MessageBuilder message) {
    this.message = message;
    this.actionBarType = ActionBarType.SHOW_PERMANENT;
    this.priority = 0;
    this.ticks = 0;
  }

  public ActionBar(String key, double seconds) {
    this.message = new MessageBuilder("");
    this.key = key;
    this.actionBarType = ActionBarType.FLASHING;
    this.priority = 100;
    this.ticks = (int) (seconds * 20);
  }

  public MessageBuilder getMessage() {
    return new MessageBuilder(message.getRaw());
  }

  public String getKey() {
    return key;
  }

  public ActionBarType getActionBarType() {
    return actionBarType;
  }

  public int getPriority() {
    return priority;
  }

  public int getTicks() {
    return ticks;
  }

  public int getExecutedTicks() {
    return executedTicks;
  }

  public void setExecutedTicks(int executedTicks) {
    this.executedTicks = executedTicks;
  }

  public void addExecutedTicks(int executedTicks) {
    this.executedTicks += executedTicks;
  }

  public enum ActionBarType {
    SHOW_PERMANENT, /* shown until removed */
    DISPLAY, /* shown until time elapsed */
    PROGRESS, /* shown until progress / time is done */
    FLASHING /* shown until time elapsed while as all strings get flashed */
  }

}
