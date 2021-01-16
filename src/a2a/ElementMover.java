package a2a;

/**
 * Represents mv command. Moves files and directories to given path, if 
 * arguments valid.
 * 
 * @author Raisa Haque
 *
 */
public class ElementMover extends Command {

  /**
   * Creates initial ElementMover object by setting instance variables
   * 
   * @param b Builder object to construct ElementMover object
   */
  public ElementMover(Builder b) {
    super(b);
  }

  /**
   * Builds ElementMover object to create ElementMover object with optional
   * Command parameters.
   * 
   * @author Raisa Haque
   */
  public static class Builder extends Command.Builder {
    /**
     * Builds and returns ElementMover object
     * 
     * @return ElementMover object
     */
    public ElementMover build() {
      return new ElementMover(this);
    }
  }

  /**
   * Moves file or directory to given file or directory given valid arguments.
   * 
   * @param args Argument for which, if valid, the file or directory will
   * be moved to the given path
   * @return Output to shell (empty string)
   */
  @Override
  public String run(String args) {

    int beforeCopyCommand = FileSystem.getOuterMap().hashCode();
    // ElementCopier class runs errors so ElementMover doesn't have to
    copier.run(args);
    int afterCopyCommand = FileSystem.getOuterMap().hashCode();

    if (beforeCopyCommand == afterCopyCommand)
      return "";

    String oldPath = pathGiver.getFullPath(args.split("\\s+")[0]);

    remover.run(oldPath);

    return " ";
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of mv command
   * 
   * @return formatted information about mv command
   */
  @Override
  public String toString() {
    // TODO Auto-generated method stub
    String command = "MV";
    String name = "\n\tmove - move files and directories";
    String synopsis = "\n\tmv OLDPATH NEWPATH";
    String description = "\n\tMove OLDPATH to NEWPATH"
        + "\n\tIf OLDPATH is a file:"
        + "\n\t\tIf NEWPATH is an existing file, overwrite NEWPATH with "
        + "OLDPATH's content and remove OLDPATH from file system."
        + "\n\t\tIf NEWPATH does not exist, move OLDPATH and rename it NEWPATH."
        + "\n\t\tIf NEWPATH is a directory, move OLDPATH to NEWPATH."
        + "\n\tIf OLDPATH is a directory:"
        + "\n\t\t If NEWPATH does not exist, move OLDPATH and rename it NEWPATH"
        + "\n\t\t If NEWPATH is a directory, move OLDPATH to NEWPATH."
        + "\n\t\t If OLDPATH already exists in NEWPATH, replace the directory "
        + "in NEWPATH with OLDPATH and remove OLDPATH.";
    String errors = "\n\tOnly 2 arguments valid."
        + "\n\tOLDPATH must be a valid file or directory."
        + "\n\tCannot move directory to file ie. If OLDPATH is a directory, "
        + "NEWPATH cannot be a file."
        + "\n\tIf NEWPATH does not already exist, the file or directory name "
        + "to be created should not contain invalid characters."
        + "\n\tCannot move root directory."
        + "\n\tCannot move a parent directory to a child directory."
        + "\n\tCannot move current directory.";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors;
  }

}
