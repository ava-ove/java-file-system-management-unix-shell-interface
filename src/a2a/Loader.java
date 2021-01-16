package a2a;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;
import driver.JShell;

/**
 * Represents load command object. Loads up state of previously saved session.
 * 
 * @author Ananya Poddar
 *
 */
public class Loader extends Command {

  /**
   * Creates initial load object by setting default instance variable values
   * from Command constructor
   * 
   * @param b Builder object to construct load object
   */
  public Loader(Builder b) {
    super(b);
  }

  /**
   * Represents Builder object to create load object with optional Command
   * parameters
   * 
   * @author Ananya Poddar
   *
   */
  public static class Builder extends Command.Builder {
    /**
     * Builds and returns created Loader object
     * 
     * @return Loader object
     */
    public Loader build() {
      return new Loader(this);
    }
  }

  /**
   * Checks that load command is being called at the beginning of the session
   * and that there is 1 argument that is a valid file to be loaded
   * 
   * @param args The arguments to be validated.
   * @return True if argument is valid, false otherwise
   */
  @Override
  public boolean validateArgs(String args) {
    if (args.split("\\s+").length > 1 || args.length() == 0) {
      printMessage.printNumArgsError();
      return false;
    }
    // "load fileName" should be the only thing in the inputLog
    if (JShell.getInputLog().size() > 1) {
      printMessage.printNotFirstCommandError();
      return false;
    }
    if (fetcher.getFile(args) == null)
      return false;
    return true;
  }

  private void loadInputLog(ArrayList<String> newLog) {
    for (int i = 0; i < newLog.size(); i++) {
      JShell.getInputLog().add(newLog.get(i));
    }
    // Adds "load fileName" to end of inputLog and removes it from start
    JShell.getInputLog().add(((String) JShell.getInputLog().get(0)));
    JShell.getInputLog().remove(0);
  }

  private void loadDirectoryStack(Stack<String> newDirStack) {
    for (int i = 0; i < newDirStack.size(); i++) {
      DirStackPusher.getDirectoryStack().push(newDirStack.get(i));
    }
  }

  private void loadFileSystem(
      TreeMap<String, TreeMap<String, FileSystemElement>> newFileSys) {
    try {
      Field outerMap = FileSystem.class.getDeclaredField("outerMap");
      outerMap.setAccessible(true);
      outerMap.set(null, newFileSys);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets all information from loaded previous session including current working
   * directory, file system, directory stack, and all inputs, if arg is valid
   * 
   * @param args The file path on file system where previous session info will
   *        be retrieved from
   * @return String representation of all loaded data if valid, empty string
   *         otherwise
   */
  @Override
  public String run(String args) {
    if (!validateArgs(args))
      return "";
    java.io.File file = fetcher.getFile(args);
    Object readObj = fetcher.getFileContent(file);

    // if there are no contents to be read, it is not a previously saved file
    if (readObj == null)
      return "";
    // if file doesn't contain an arrayList, or the arraylist contained is not
    // of size 4, then it is not a previously saved file
    else if (!(readObj instanceof ArrayList<?>)
        || (readObj instanceof ArrayList<?>)
            && ((ArrayList<?>) readObj).size() != 4)
      return "";
    else {
      ArrayList<Object> deserializedInfo = (ArrayList<Object>) readObj;

      StringBuffer deserialized = new StringBuffer();
      for (Object info : deserializedInfo) {
        deserialized.append(info.toString() + "\n");
      }
      JShell.setCurrentPath((String) deserializedInfo.get(0));
      loadInputLog((ArrayList<String>) deserializedInfo.get(1));
      loadDirectoryStack((Stack<String>) deserializedInfo.get(2));
      loadFileSystem(
          (TreeMap<String, TreeMap<String, FileSystemElement>>) deserializedInfo
              .get(3));
      return deserialized.toString();
    }
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of load command
   * 
   * @return formatted information about load command
   */
  @Override
  public String toString() {
    String command = "LOAD";
    String name = "\n\tload - load contents of a previous session from file";
    String synopsis = "\n\tload FileName";
    String description = "\n\tReload contents and state of JShell program from "
        + "the session saved in text file FileName.\n\tIncludies state of "
        + "JShell filesystem, directory stack, and all commands.";

    String errors = "\n\tFileName must be a valid path to an existing file"
        + " on computer's file system with all saved information from a "
        + "previous session."
        + "\n\tThe file extension can either be omitted or any extension can "
        + "be used, as the file that was saved must have a .ser extension."
        + "\n\tload command must be called at the beginning of a new session";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors;
  }


}
