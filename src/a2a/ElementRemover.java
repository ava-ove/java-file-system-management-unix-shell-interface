package a2a;

import java.util.Iterator;
import driver.JShell;
import interfaces.IElementRemover;

/**
 * Represents rm command. Removes file or directory from file system, if
 * argument valid.
 * 
 * @author Raisa Haque
 * @author Ananya Poddar
 *
 */
public class ElementRemover extends Command
    implements Iterable<String>, IElementRemover {

  private String elementToRemove;

  /**
   * Creates initial ElementRemover object by setting instance variables and
   * initializing elementToRemove
   * 
   * @param b Builder object to construct ElementRemover object
   */
  public ElementRemover(Builder b) {
    super(b);
    elementToRemove = "";
  }

  /**
   * Checks that argument is an existing file or directory that is allowed to be
   * removed from the file system.
   * 
   * @param args The argument to be validated.
   * @return True if argument is valid, false otherwise.
   */
  @Override
  public boolean validateArgs(String args) {
    String workingDir = JShell.getCurrentPath();
    // invalid number of arguments
    if (args.isEmpty() || args.split("\\s+").length > 1) {
      printMessage.printNumArgsError();
      return false;
    }
    args = pathGiver.getFullPath(args);
    // check that argument is a valid directory
    if (!validateElement.validateDirectory(args)
        && validateElement.validateFile(args) == null) {
      printMessage.printInvalidDirFilePath();
      return false;
    }
    // if the directory to be removed is either the working directory or a
    // parent of the working directory, or the root directory, do not remove
    if (args.equals("/") || workingDir.startsWith(args + "/")
        || args.equals(workingDir)) {
      printMessage.printParentError();
      return false;
    }
    return true;
  }

  /**
   * Removes file or directory from file system given valid argument.
   * 
   * @param args Argument for which, if valid, the file or directory will be
   *        removed.
   * @return Output to shell (empty string)
   */
  @Override
  public String run(String args) {

    if (!validateArgs(args))
      return "";

    this.elementToRemove = pathGiver.getFullPath(args);

    // removes appropriate file
    if (validateElement.validateFile(this.elementToRemove) != null) {
      FileSystem.getOuterMap()
          .get(elementToRemove.substring(0,
              Math.max(1, elementToRemove.lastIndexOf("/"))))
          .remove(elementToRemove.substring(elementToRemove.lastIndexOf("/")));
      return "";
    }

    // removes appropriate directory and its contents
    for (String path : this) {
      if (validateElement.validateDirectory(path)) {
        FileSystem.getOuterMap().remove(path);
        if (FileSystem.getOuterMap()
            .containsKey(path.substring(0, Math.max(1, path.lastIndexOf("/")))))
          FileSystem.getOuterMap()
              .get(path.substring(0, Math.max(1, path.lastIndexOf("/"))))
              .remove(path);
      }
    }
    return " ";
  }

  /**
   * Returns an iterator to search through the tree
   * 
   * @return A TreeIterator that iterates through the file system tree
   */
  @Override
  public Iterator<String> iterator() {
    return new TreeIterator(elementToRemove);
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of rm command
   * 
   * @return formatted information about rm command
   */
  @Override
  public String toString() {
    String command = "REMOVE";
    String name = "\n\trm - remove directory/file from file system";
    String synopsis = "\n\trm DIR";
    String description = "\n\tRemove specified directory DIR and all its "
        + "contents, or specified file from the file system.";

    String errors = "\n\tOnly one argument valid \n\tDIR must be an existing "
        + "file or directory in file system "
        + "\n\tDIR cannot be the root directory, working directory, or a parent"
        + " of the working directory.";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors;
  }

}
