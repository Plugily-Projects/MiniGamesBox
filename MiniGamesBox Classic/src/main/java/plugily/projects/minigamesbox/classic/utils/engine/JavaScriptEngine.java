package plugily.projects.minigamesbox.classic.utils.engine;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import plugily.projects.minigamesbox.classic.PluginMain;
import org.bukkit.plugin.RegisteredServiceProvider;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaScriptEngine {

  // private List<String> engineNames = new ArrayList<>(Arrays.asList("js", "JS", "javascript", "JavaScript", "ecmascript", "ECMAScript", "nashorn", "Nashorn"));
  private List<String> engineNames = new ArrayList<>(Arrays.asList("plugilyprojects"));
  private ScriptEngineManager scriptEngineManager;
  private ScriptEngineFactory scriptEngineFactory;

  private  boolean jdkNashorn;

  public JavaScriptEngine(PluginMain plugin) {
    ServicesManager servicesManager = plugin.getServer().getServicesManager();

    if(servicesManager.isProvidedFor(ScriptEngineManager.class)) {
      RegisteredServiceProvider<ScriptEngineManager> registered = servicesManager.getRegistration(ScriptEngineManager.class);
      scriptEngineManager = registered.getProvider();
      setFactory(scriptEngineManager);
    } else {
      scriptEngineManager = getScriptEngineManager();
      servicesManager.register(ScriptEngineManager.class, scriptEngineManager, plugin, ServicePriority.Highest);
    }
    if(scriptEngineManager != null) {
      setFactory(scriptEngineManager);
    }
  }

  private  void setFactory(ScriptEngineManager manager) {
    if(scriptEngineFactory == null) {
      for(ScriptEngineFactory factory : manager.getEngineFactories()) {
        if(factory.getLanguageName().equalsIgnoreCase("ECMAScript")) {
          scriptEngineFactory = factory;
        }
      }
      if(scriptEngineFactory == null) {
        jdkNashorn = true;
      }
    }

    if(jdkNashorn) {
      if(scriptEngineFactory == null || !(scriptEngineFactory instanceof NashornScriptEngineFactory)) {
        scriptEngineFactory = new NashornScriptEngineFactory();
      }
    }

    engineNames.stream()
        .forEach(n -> manager.registerEngineName(n, scriptEngineFactory));
  }

  public  ScriptEngineManager getScriptEngineManager() {
    if(scriptEngineManager == null) {
      scriptEngineManager = new ScriptEngineManager();
      if(scriptEngineManager.getEngineByName("JavaScript") == null) {
        jdkNashorn = true;
      }
      setFactory(scriptEngineManager);
    }
    return scriptEngineManager;
  }

  public  ScriptEngine getEngine() {
    return getScriptEngineManager().getEngineByName(engineNames.get(0));
  }
}
