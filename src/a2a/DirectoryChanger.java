package a2a;

import driver.JShell;

/**
 * Represents cd command. Changes current working directory to specified
 * directory.
 * 
 * @author Raisa Haque
 *
 */
public class DirectoryChanger extends Command {

  /**
   * Creates initial DirectoryChanger object by setting instance variables
   * 
   * @param b Builder object to construct DirectoryChanger object
   */
  public DirectoryChanger(Builder b) {
    super(b);
  }

  /**
   * Checks that argument is not empty and is a valid directory in the file
   * system
   * 
   * @param argument The argument to be validated.
   * @return true if argument is valid, false otherwise.
   */
  @Override
  public boolean validateArgs(String argument) {

    if (argument.split("\\s+").length > 1 || argument.length() == 0) {
      printMessage.printNumArgsError();
      return false;
    }

    if (!validateElement.validateDirectory(argument)) {
      printMessage.printInvalidDirectoryError();
      return false;
    }

    return true;
  }

  /**
   * Changes the current working directory to the argument, if valid
   * 
   * @param argument The argument, if valid, to become the new working directory
   * @return changed current path if valid, empty string otherwise
   */
  @Override
  public String run(String argument) {
    if (!validateArgs(argument)) {
      return "";
    }

    JShell.setCurrentPath(pathGiver.getFullPath(argument));
    return JShell.getCurrentPath();
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of cd command
   * 
   * @return formatted information about cd command
   */
  public String toString() {
    String command = "CD";
    String name = "\n\tcd - change directory";
    String synopsis = "\n\tcd DIRECTORY";
    String description =
        "\n\tChange the current working directory to the specified directory."
            + "\n\tThe directory path may be a full path or a relative path.";
    String errors = "\n\tOnly 1 argument valid.\n\tDirectory must exist in "
        + "the file system.\n\tFull or relative path must be valid.";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors;
  }
}
