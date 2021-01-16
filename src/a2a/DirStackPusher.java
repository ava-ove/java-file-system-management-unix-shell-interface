package a2a;

import java.util.Stack;
import driver.JShell;

/**
 * Represents pushd command. Adds the current working directory to the top of
 * the directory stack and changes current working directory to argument.
 * 
 * @author Raisa Haque
 *
 */
public class DirStackPusher extends Command {

  private Command changeDir;
  private static Stack<String> directoryStack = new Stack<String>();

  /**
   * Creates initial DirStackPusher object by setting instance variables
   * 
   * @param b Builder object to construct DirStackPusher object
   */
  public DirStackPusher(Builder b) {
    super(b);
    changeDir = new DirectoryChanger(b);
  }

  /**
   * Checks that argument is a valid directory in the file system
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

  public static Stack<String> getDirectoryStack() {
    return directoryStack;
  }

  /**
   * Adds the current working directory to the directory stack and changes the
   * directory to the argument, if valid
   * 
   * @param argument The argument, if valid, to become the new working directory
   * @return Output to shell (empty string)
   */
  @Override
  public String run(String argument) {

    if (!validateArgs(argument)) {
      return "";
    }

    directoryStack.push(JShell.getCurrentPath());
    changeDir.run(argument);
    return " ";
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of pushd command
   * 
   * @return formatted information about pushd command
   */
  public String toString() {
    String command = "PUSHD";
    String name =
        "\n\tpushd - push current working directory onto directory stack";
    String synopsis = "\n\tpushd DIRECTORY";
    String description =
        "\n\tAdd the current working directory to the top of the directory "
            + "stack and change the current working directory to the specified "
            + "directory.\n\tThe directory path may be a full path or a "
            + "relative path.";
    String errors =
        "\n\tOnly 1 argument valid.\n\tDirectory must exist in the file "
            + "system.\n\tFull or relative path must be valid.";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors;
  }

}
