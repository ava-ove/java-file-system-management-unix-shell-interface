package a2a;

import driver.JShell;

/**
 * Creates an absolute path for any given path
 * 
 * @author Raisa Haque
 *
 */
public class FullPathGiver {

  /**
   * Outputs the absolute path of the argument
   * 
   * @param argument argument The argument for which an absolute path is found
   * @param currentPath path from which to start absolute path
   * @return the absolute path of the given argument
   */
  public String getFullPath(String argument) {
    String currentPath = JShell.getCurrentPath();
    if (argument.contains("//"))
      return argument;

    if (argument.endsWith("/") && !argument.contentEquals("/"))
      argument = argument.substring(0, argument.length() - 1);

    /*
     * Considers different parent directory cases given a full path and if none
     * applicable, argument is a full path with proper directory names
     */
    if (argument.startsWith("/")) {
      if (argument.contentEquals("/.") || argument.contentEquals("/.."))
        return "/";
      if (argument.contains("..") || argument.contains("."))
        return getParentDir(argument, currentPath);
      return argument;
    }

    if (argument.contains("..") || argument.contains(".")) {
      return getParentDir(argument, currentPath);
    }

    if (currentPath.equals("/")) {
      if (argument.equals("/")) {
        return argument;
      }
      return currentPath + argument; // argument relative to root directory
    }
    return currentPath + "/" + argument; // current path is not at root
  }


  private String getParentDir(String argument, String currentPath) {
    String path = currentPath.substring(1);
    String[] split;

    if (argument.startsWith("/")) // argument is a full path
      path = ""; // disregard current path and start at root
    if (argument.startsWith("/../")) // start at root
      argument = argument.substring(4);
    else if (argument.startsWith("/./")) // start at root
      argument = argument.substring(3);

    split = argument.split("/");

    for (int i = 0; i < split.length; i++) {
      if (split[i].equals("..")) { // go to parent
        if (!path.contains("/")) // already at root directory
          path = "";
        else
          path = path.substring(0, path.lastIndexOf("/")); // goes to parent
                                                           // directory
      } else if (!split[i].equals(".")) {
        if (path.endsWith("/"))
          path += split[i];
        else
          path += "/" + split[i];
      }
    }
    if (!path.startsWith("/"))
      path = "/" + path;
    return path;
  }

}
