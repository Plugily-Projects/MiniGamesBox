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

package plugily.projects.minigamesbox.classic.utils.misc;

import plugily.projects.minigamesbox.classic.Main;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.11.2021
 */
public class MessageUtils {

  private final Main plugin;

  public MessageUtils(Main plugin) {
    this.plugin = plugin;
  }

  public void thisVersionIsNotSupported() {
    plugin.getDebugger().sendConsoleMsg("&c  _   _           _                                                    _                _ ");
    plugin.getDebugger().sendConsoleMsg("&c | \\ | |   ___   | |_     ___   _   _   _ __    _ __     ___    _ __  | |_    ___    __| |");
    plugin.getDebugger().sendConsoleMsg("&c |  \\| |  / _ \\  | __|   / __| | | | | | '_ \\  | '_ \\   / _ \\  | '__| | __|  / _ \\  / _` |");
    plugin.getDebugger().sendConsoleMsg("&c | |\\  | | (_) | | |_    \\__ \\ | |_| | | |_) | | |_) | | (_) | | |    | |_  |  __/ | (_| |");
    plugin.getDebugger().sendConsoleMsg("&c |_| \\_|  \\___/   \\__|   |___/  \\__,_| | .__/  | .__/   \\___/  |_|     \\__|  \\___|  \\__,_|");
    plugin.getDebugger().sendConsoleMsg("&c                                       |_|     |_|                                        ");
  }

  public void errorOccurred() {
    plugin.getDebugger().sendConsoleMsg("&c  _____                                                                                  _   _ ");
    plugin.getDebugger().sendConsoleMsg("&c | ____|  _ __   _ __    ___    _ __      ___     ___    ___   _   _   _ __    ___    __| | | |");
    plugin.getDebugger().sendConsoleMsg("&c |  _|   | '__| | '__|  / _ \\  | '__|    / _ \\   / __|  / __| | | | | | '__|  / _ \\  / _` | | |");
    plugin.getDebugger().sendConsoleMsg("&c | |___  | |    | |    | (_) | | |      | (_) | | (__  | (__  | |_| | | |    |  __/ | (_| | |_|");
    plugin.getDebugger().sendConsoleMsg("&c |_____| |_|    |_|     \\___/  |_|       \\___/   \\___|  \\___|  \\__,_| |_|     \\___|  \\__,_| (_)");
    plugin.getDebugger().sendConsoleMsg("&c                                                                                               ");
  }

  public void updateIsHere() {
    plugin.getDebugger().sendConsoleMsg("&a  _   _               _           _          ");
    plugin.getDebugger().sendConsoleMsg("&a | | | |  _ __     __| |   __ _  | |_    ___ ");
    plugin.getDebugger().sendConsoleMsg("&a | | | | | '_ \\   / _` |  / _` | | __|  / _ \\");
    plugin.getDebugger().sendConsoleMsg("&a | |_| | | |_) | | (_| | | (_| | | |_  |  __/");
    plugin.getDebugger().sendConsoleMsg("&a  \\___/  | .__/   \\__,_|  \\__,_|  \\__|  \\___|");
    plugin.getDebugger().sendConsoleMsg("&a         |_|                                 ");
  }

  public void gonnaMigrate() {
    plugin.getDebugger().sendConsoleMsg("&a  __  __   _                          _     _                    ");
    plugin.getDebugger().sendConsoleMsg("&a |  \\/  | (_)   __ _   _ __    __ _  | |_  (_)  _ __     __ _             ");
    plugin.getDebugger().sendConsoleMsg("&a | |\\/| | | |  / _` | | '__|  / _` | | __| | | | '_ \\   / _` |            ");
    plugin.getDebugger().sendConsoleMsg("&a | |  | | | | | (_| | | |    | (_| | | |_  | | | | | | | (_| |  _   _   _ ");
    plugin.getDebugger().sendConsoleMsg("&a |_|  |_| |_|  \\__, | |_|     \\__,_|  \\__| |_| |_| |_|  \\__, | (_) (_) (_)");
    plugin.getDebugger().sendConsoleMsg("&a               |___/                                    |___/             ");
  }

  public void info() {
    plugin.getDebugger().sendConsoleMsg("&e  _____        __        _ ");
    plugin.getDebugger().sendConsoleMsg("&e |_   _|      / _|      | |");
    plugin.getDebugger().sendConsoleMsg("&e   | |  _ __ | |_ ___   | |");
    plugin.getDebugger().sendConsoleMsg("&e   | | | '_ \\|  _/ _ \\  | |");
    plugin.getDebugger().sendConsoleMsg("&e  _| |_| | | | || (_) | |_|");
    plugin.getDebugger().sendConsoleMsg("&e |_____|_| |_|_| \\___/  (_)");
  }

}
