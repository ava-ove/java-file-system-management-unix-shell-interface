package a2a;

import java.util.ArrayList;

/**
 * Represents mkdir command. Creates new directory/directories in specified
 * directory.
 * 
 * @author Fariha Fyrooz
 *
 */
public class DirectoryMaker extends Command {

  /**
   * Creates initial DirectoryMaker object by setting default instance variable
   * values from Command constructor
   * 
   * @param b Builder object to construct DirectoryMaker object
   */
  public DirectoryMaker(Builder b) {
    super(b);
  }

  /**
   * Represents Builder object to create DirectioryMaker object with optional
   * Command parameters
   * 
   * @author Fariha Fyrooz
   */
  public static class Builder extends Command.Builder {
    /**
     * Builds and returns DirectoryMaker object
     * 
     * @return DirectoryMaker object
     */
    public DirectoryMaker build() {
      return new DirectoryMaker(this);
    }
  }

  /**
   * Validates there is at least one argument given
   * 
   * @param arguments The argument to be validated
   * @return true if at least 1 argument given, false otherwise
   */

  public boolean validateNumArgs(String arguments) {

    if (arguments.length() == 0) {
      printMessage.printNumArgsError();
      return false;
    }
    return true;
  }

  /**
   * Checks that argument does not already exist and that the DIR name and its
   * path are valid. Outputs error message if any of the previous conditions are
   * not met.
   * 
   * @param argument The argument to be validated.
   * @return true if argument is valid, false otherwise.
   */

  @Override
  public boolean validateArgs(String arguments) {

    if (validateElement.validateDirectory(arguments)
        || validateElement.validateFile(arguments) != null) {
      printMessage.printExistingDirOrFileError();
      return false;
    }

    /*
     * argument is full path of wanted directory. dirName is everything after
     * the last /, which is the name of the new directory that should be
     * created. If dirName is valid, checks that the path where dirName would be
     * created is valid.
     */
    String dirName = arguments.substring(arguments.lastIndexOf("/") + 1);

    if (validateElement.containsValidCharacters(dirName)) {
      if (arguments.lastIndexOf("/") != 0) {
        if (validateElement.validateDirectory(
            arguments.substring(0, arguments.lastIndexOf("/")))) {
          return true;
        }
        printMessage.printInvalidDirectoryError();
        return false;
      }
      return true;
    }
    return false;
  }

  /**
   * Creates new directory/directories in specified path(s), if all paths are
   * valid
   * 
   * @param argument The argument, if valid, to become new directory/directories
   * @return Output to JShell (empty String)
   */
  @Override
  public String run(String arguments) {
    if (!validateNumArgs(arguments)) { // checks at least one argument given
      return "";
    }
    // splits arguments by space and validDirs will store the paths of validDirs
    String[] dirs = arguments.split("\\s+");
    ArrayList<String> validDirs = new ArrayList<String>();
    for (int i = 0; i < dirs.length; i++) {
      // Checks that the dir name, before getting the full path, doesn't include
      // or equal certain invalid characters.
      if (dirs[i].endsWith("/") || dirs[i].contains("//") || dirs[i].equals(".")
          || dirs[i].equals("..") || dirs[i].equals("/.")
          || dirs[i].equals("/..")) {
        printMessage.printInvalidCharError();
        return "";
      }
      dirs[i] = pathGiver.getFullPath(dirs[i]);
      if (validateArgs(dirs[i])) {
        validDirs.add(dirs[i]);
      } else { // if any of args are invalid, none of them are added
        return "";
      }
    }
    for (String dir : validDirs) { // if all args are valid, they're each added
      if (dir.lastIndexOf("/") == 0) {
        modifier.addDir("/", dir);
      } else {
        String inDir = dir.substring(0, dir.lastIndexOf("/"));
        modifier.addDir(inDir, dir);
      }
    }
    return " ";
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of mkdir command
   * 
   * @return formatted information about mkdir command
   */
  public String toString() {
    String command = "MKDIR";
    String name = "\n\tmkdir - make directories";
    String synopsis = "\n\tmkdir DIR1 [DIR2...]";
    String description =
        "\n\tCreate new directories in specified locations if a directory or "
            + "file with same path does not already exist."
            + "\n\tThe directory path may be a full path or a relative path.";
    String errors =
        "\n\tAll directory names must be valid.\n\tDirectory names must not "
            + "contain invalid characters.\n\tDirectory or file with same name "
            + "must not already exist in specified locations.\n\tFull or "
            + "relative paths must be valid.";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors;
  }

}


