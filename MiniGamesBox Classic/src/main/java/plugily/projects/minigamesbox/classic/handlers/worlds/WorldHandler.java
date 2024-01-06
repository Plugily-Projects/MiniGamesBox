package plugily.projects.minigamesbox.classic.handlers.worlds;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;

public class WorldHandler {

  /**
   * A method to clone a world given the original world to clone from and the name of the new world
   *
   * @param world The original world
   * @param name  The name of the new world
   * @return World The cloned world
   */
  public static World cloneWorld(World world, String name) {
    WorldCreator creator = new WorldCreator(name);
    creator.copy(world);

    return creator.createWorld();
  }

  /**
   * Deletes a specified world from the server.
   *
   * @param  world  the world to be deleted
   * @return        true if the world was successfully deleted, false otherwise
   */
  public static boolean deleteWorld(World world) {
    boolean success = true;
    if (world != null) {
      Bukkit.unloadWorld(world, true);
      File worldFolder = world.getWorldFolder();
      if (worldFolder.exists()) {
        try {
          deleteFolder(worldFolder);
        }
        catch (WorldDeletionException e) {
          success = false;
        }
      }
    }
    return success;
  }

  /**
   * Deletes a folder and all its contents recursively.
   *
   * @param  folder  the folder to be deleted
   * @throws WorldDeletionException  if the folder deletion fails
   */
  private static void deleteFolder(File folder) throws WorldDeletionException {
    if (folder.isDirectory()) {
      File[] files = folder.listFiles();
      if (files != null) {
        for (File file : files) {
          deleteFolder(file);
        }
      }
    }
    if (!folder.delete()) {
      throw new WorldDeletionException("Failed to delete folder: " + folder.getAbsolutePath());
    }
  }

  /**
   * Custom exception for world deletion errors.
   */
  public static class WorldDeletionException extends Exception {
    public WorldDeletionException(String message) {
      super(message);
    }
  }

}
