package plugily.projects.minigamesbox.classic;

import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.minigamesbox.classic.utils.misc.MiscUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.EventsInitializer;

public class Initializer {

  public Initializer(JavaPlugin plugin) {
    new EventsInitializer().initialize(plugin);
    MiscUtils.sendStartUpMessage(plugin);
  }

}
