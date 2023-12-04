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

package plugily.projects.minigamesbox.classic.utils.engine;

import org.bukkit.Bukkit;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 21.09.2021
 */
public class ScriptEngine {

  private final javax.script.ScriptEngine scriptEngine;

  public ScriptEngine() {
    if(Double.parseDouble(System.getProperty("java.specification.version")) < 11) {
      scriptEngine = new ScriptEngineManager().getEngineByName("js");
    } else {
      ScriptEngineFactory nashornScriptEngineFactory = new org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory();
      scriptEngine = nashornScriptEngineFactory.getScriptEngine();
    }
  }

  public void setValue(String value, Object valueObject) {
    if(scriptEngine != null)
      scriptEngine.put(value, valueObject);
  }

  public void execute(String executable) {
    if(scriptEngine == null)
      return;

    try {
      scriptEngine.eval(executable);
    } catch(ScriptException e) {
      Bukkit.getLogger().log(Level.SEVERE, "Script failed to parse expression! Expression was written wrongly!");
      Bukkit.getLogger().log(Level.SEVERE, "Expression value: " + executable);
      Bukkit.getLogger().log(Level.SEVERE, "Error log:");
      e.printStackTrace();
      Bukkit.getLogger().log(Level.SEVERE, "---- THIS IS AN ISSUE BY USER CONFIGURATION NOT AUTHOR BUG ----");
    }
  }

}
