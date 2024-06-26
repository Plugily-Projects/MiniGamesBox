package plugily.projects.minigamesbox.api.arena;

/**
 * @author Lagggpixel
 * @since April 24, 2024
 */
public enum IArenaState {

  WAITING_FOR_PLAYERS("Waiting"),
  STARTING("Starting"),
  FULL_GAME("Full-Game"),
  IN_GAME("In-Game"),
  ENDING("Ending"),
  RESTARTING("Restarting");

  private final String formattedName;

  IArenaState(String formattedName) {
    this.formattedName = formattedName;
  }

  public String getFormattedName() {
    return formattedName;
  }
}
