package a2a;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import driver.JShell;

/**
 * Represents save command object. Saves state of all commands and file system
 * from the session.
 * 
 * @author Ananya Poddar
 *
 */
public class Saver extends Command {

  /**
   * Creates initial save object by setting default instance variable values
   * from Command constructor
   * 
   * @param b Builder object to construct save object
   */
  public Saver(Builder b) {
    super(b);
  }

  /**
   * Represents Builder object to create save object with optional Command
   * parameters
   * 
   * @author Ananya Poddar
   *
   */
  public static class Builder extends Command.Builder {
    /**
     * Builds and returns created Saver object
     * 
     * @return Saver object
     */
    public Saver build() {
      return new Saver(this);
    }
  }

  /**
   * Validates that there is 1 argument that it is a valid path to a file on
   * local file system
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
    if (fetcher.getFile(args) == null) {
      return false;
    }
    return true;
  }

  /**
   * Saves state of current session including current working directory, file
   * system directory stack, and all user input to specified file, if valid
   * 
   * @param args The file path on file system where current session will be
   *        saved
   * @return String representation of all saved data if valid, empty string
   *         otherwise
   */
  @Override
  public String run(String args) {
    if (validateArgs(args)) {
      // store all relevant information in Arraylist in the order: current
      // path, input log, directory stack, file system
      ArrayList<Object> allInfo = new ArrayList<>();
      allInfo.add(JShell.getCurrentPath());
      allInfo.add(JShell.getInputLog());
      allInfo.add(DirStackPusher.getDirectoryStack());
      allInfo.add(FileSystem.getOuterMap());
      StringBuffer sb = new StringBuffer();
      try {
        FileOutputStream fos =
            new FileOutputStream(fetcher.getFile(args).getAbsolutePath());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(allInfo);
        for (Object info : allInfo) {
          sb.append(info + "\n");
        }
        oos.close();
        return sb.toString();
      } catch (IOException e) {
        printMessage.printInvalidFileError();
      } catch (Exception e) {
        printMessage.printGeneralException();
      }
      return "";
    }
    return "";
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of save command
   * 
   * @return formatted information about save command
   */
  @Override
  public String toString() {
    String command = "SAVE";
    String name = "\n\tsave - store state of JShell program to file";
    String synopsis = "\n\tsave FileName";
    String description = "\n\tStore state of JShell program for a session to a "
        + "file FileName on computer's File System.\n\tIncludes state of JShell"
        + " filesystem, directory stack, working directory, and all commands.";

    String errors = "\n\tFileName must be a valid path to a (new or existing) "
        + "file on computer's file system."
        + "\n\tThe file extension can either be omitted or any extension can "
        + "be used, as the file will be saved with a .ser extension."
        + "\n\tAll characters except spaces are allowed for file paths.";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors;
  }


}
