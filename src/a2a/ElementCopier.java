package a2a;

import java.util.Iterator;
import driver.JShell;
import interfaces.IElementCopier;

/**
 * Represents cp command. Copies a file or directory to specified location
 * 
 * @author Fariha Fyrooz
 * @author Raisa Haque
 * 
 */
public class ElementCopier extends Command
    implements Iterable<String>, IElementCopier {

  private String dirToCopy;
  private File oldFile;
  private File newFile;
  private FileOverwriter overFile;
  private Builder build;

  /**
   * Creates initial ElementCopier object by setting default instance variable
   * values from Command constructor and initializing File and FileOverwriter
   * objects.
   * 
   * @param b Builder object to construct ElementCopier object
   */
  public ElementCopier(Builder b) {
    super(b);
    dirToCopy = "";
    oldFile = new File("", "");
    newFile = new File("", "");
    overFile = new FileOverwriter(new File("", ""), build);
  }

  /**
   * Represents Builder object to create ElementCopier object with optional
   * Command parameters
   * 
   * @author Fariha Fyrooz
   */
  public static class Builder extends Command.Builder {
    /**
     * Builds and returns ElementCopier object
     * 
     * @return ElementCopier object
     */
    public ElementCopier build() {
      return new ElementCopier(this);
    }
  }

  /**
   * Checks that there are two arguments given and that the first argument is a
   * valid existing file or directory. Outputs error message if either of the
   * previous conditions are not met.
   * 
   * @param argument The argument to be validated.
   * @return true if argument is valid, false otherwise.
   */
  @Override
  public boolean validateArgs(String argument) {
    parsedArgs.clear();
    parseArgs(argument);

    if (parsedArgs.size() != 2) {
      printMessage.printNumArgsError();
      return false;
    }

    String oldPath = pathGiver.getFullPath(argument.split("\\s+")[0]);
    if (oldPath.equals("/")) {
      printMessage.printParentError();
      return false;
    }
    if (validateElement.validateDirectory(oldPath)
        || validateElement.validateFile(oldPath) != null) {
      return true;
    }
    printMessage.printInvalidDirFilePath();
    return false;
  }

  /**
   * Copies existing file at oldPath to indicated newPath. If a file doesn't
   * exist at newPath, it's created. Content of file at newPath is overwritten
   * with content of existing file at oldPath.
   * 
   * @param oldPath Path to the existing file
   * @param newPath Path to file where existing file will be copied
   */
  @Override
  public void copyFileToFile(String oldPath, String newPath) {

    oldFile = validateElement.validateFile(oldPath);
    // if file at newPath doesn't exist, it will add. if it exists, it won't add
    modifier.addFile(newPath);
    if (validateElement.validateFile(newPath) != null) {
      newFile = validateElement.validateFile(newPath);
      overFile = new FileOverwriter(newFile, build);
      overFile.run(oldFile.getContent());
    }
  }

  /**
   * Provides the various errors for the case when a directory is being copied
   * into another directory
   * 
   * @param oldDir The path of the directory which is being copied
   * @param newDir The path where the old directory is being copied to
   * @return True if the old directory is allowed to be copied to the new
   *         directory. Otherwise, returns false.
   */
  @Override
  public boolean copyDirToDirErrors(String oldDir, String newDir) {
    String curPath = JShell.getCurrentPath();
    if (newDir.startsWith(oldDir + "/") || newDir.equals(oldDir)
        || oldDir.startsWith(curPath + "/") || oldDir.equals(curPath)) {
      printMessage.printParentError();
      return false;
    }
    if (!validateElement.validateDirectory(
        newDir.substring(0, Math.max(1, newDir.lastIndexOf("/"))))) {
      printMessage.printCopyinError();
      return false;
    }
    if (validateElement.validateDirectory(
        newDir + oldDir.substring(oldDir.lastIndexOf("/")))) {
      if (newDir.equals(oldDir.substring(0, oldDir.lastIndexOf("/")))
          || oldDir.startsWith(
              newDir + oldDir.substring(oldDir.lastIndexOf("/")) + "/")) {
        printMessage.printExistingDirOrFileError();
        return false;
      }
      remover.run(newDir + oldDir.substring(oldDir.lastIndexOf("/")));
      return true;
    }
    if ((newDir.equals("/") && validateElement
        .validateDirectory(oldDir.substring(oldDir.lastIndexOf("/"))))) {
      if (oldDir.lastIndexOf("/") == 0 || oldDir
          .startsWith(oldDir.substring(oldDir.lastIndexOf("/")) + "/")) {
        printMessage.printExistingDirOrFileError();
        return false;
      }
      remover.run(oldDir.substring(oldDir.lastIndexOf("/")));
    }
    return true;
  }

  /**
   * Copies one directory into another directory, given that the old directory
   * is allowed to be copied into the new directory
   * 
   * @param oldDir The path of the directory which is being copied
   * @param newDir The path where the old directory is being copied to
   * 
   */
  @Override
  public void copyDirToDir(String oldDir, String newDir) {
    if (!copyDirToDirErrors(oldDir, newDir))
      return;
    String pathToChange = oldDir.substring(0, oldDir.lastIndexOf("/"));
    dirToCopy = oldDir;
    boolean iteratorNextUsed = false;

    if (!validateElement.validateDirectory(newDir)
        && validateElement.validateDirectory(
            newDir.substring(0, Math.max(1, newDir.lastIndexOf("/"))))) {
      pathToChange = iterator().next();
      iteratorNextUsed = true;
      modifier.addDir(newDir.substring(0, Math.max(1, newDir.lastIndexOf("/"))),
          newDir);
    }
    if (newDir.equals("/"))
      newDir = "";
    for (String path : this) {
      if (iteratorNextUsed && path.equals(oldDir))
        continue;
      if (validateElement.validateDirectory(path)) {
        String dirToAdd = newDir + path
            .substring(path.indexOf(pathToChange) + pathToChange.length());
        modifier.addDir(
            dirToAdd.substring(0, Math.max(1, dirToAdd.lastIndexOf("/"))),
            dirToAdd);
      } else if (validateElement.validateFile(path) != null)
        copyFileToFile(path, newDir + path
            .substring(path.indexOf(pathToChange) + pathToChange.length()));
    }
  }

  /**
   * Copies existing file or directory to specified path, if both paths are
   * valid.
   * 
   * @param argument Element to be copied and the specified location
   * @return Output to JShell (empty String)
   */
  @Override
  public String run(String arguments) {
    if (validateArgs(arguments)) {
      // correct num args + oldPath exists
      String[] paths = arguments.split("\\s+");
      String oldPath = pathGiver.getFullPath(paths[0]);
      String newPath = pathGiver.getFullPath(paths[1]);

      if (validateElement.validateDirectory(oldPath)) {
        if (validateElement.validateFile(newPath) == null) {
          // oldPath a directory and newPath isn't a file
          copyDirToDir(oldPath, newPath);
        } else {
          // oldPath is a directory and newPath is a file
          printMessage.printCopyinError();
        }
      } else if (validateElement.validateFile(oldPath) != null) {
        // oldPath is a file
        if (validateElement.validateDirectory(newPath)) {
          // newPath is a directory. Copy to existing directory
          // the new file's path is the existing dir newPath + name of the file
          // we're copying
          copyFileToFile(oldPath,
              newPath + oldPath.substring(oldPath.lastIndexOf("/")));
        } else {
          // newPath is a file
          copyFileToFile(oldPath, newPath);
        }
      }
    }
    // oldPath not valid file or directory
    return "";
  }

  /**
   * Returns an iterator to search through the tree
   * 
   * @return A TreeIterator that iterates through the file system tree
   */
  @Override
  public Iterator<String> iterator() {
    return new TreeIterator(dirToCopy);
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of cp command
   * 
   * @return formatted information about cp command
   */
  @Override
  public String toString() {
    String command = "CP";
    String name = "\n\tcp - copy files and directories";
    String synopsis = "\n\tcp OLDPATH NEWPATH";
    String description = "\n\tCopy OLDPATH to NEWPATH."
        + "\n\tIf OLDPATH is a file:"
        + "\n\t\tIf NEWPATH is an existing file, overwrite NEWPATH with "
        + "OLDPATH's content."
        + "\n\t\tIf NEWPATH does not exist, copy OLDPATH and rename it NEWPATH."
        + "\n\t\tIf NEWPATH is a directory, copy OLDPATH to NEWPATH."
        + "\n\tIf OLDPATH is a directory:"
        + "\n\t\t If NEWPATH does not exist, copy OLDPATH and rename it NEWPATH"
        + "\n\t\t If NEWPATH is a directory, copy OLDPATH to NEWPATH."
        + "\n\t\t If OLDPATH already exists in NEWPATH, replace the directory "
        + "in NEWPATH with OLDPATH.";
    String errors = "\n\tOnly 2 arguments valid."
        + "\n\tOLDPATH must be a valid file or directory."
        + "\n\tCannot copy directory to file ie. If OLDPATH is a directory, "
        + "NEWPATH cannot be a file."
        + "\n\tIf NEWPATH does not already exist, the file or directory name to"
        + " be created should not contain invalid characters."
        + "\n\tCannot copy root directory."
        + "\n\tCannot copy a parent directory to a child directory."
        + "\n\tCannot copy current directory.";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors;

  }

}
