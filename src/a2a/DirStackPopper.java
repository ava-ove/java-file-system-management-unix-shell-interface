package a2a;

/**
 * Represents popd command. Removes top entry in directory stack and changes the
 * current working directory to the removed entry.
 * 
 * @author Raisa Haque
 *
 */
public class DirStackPopper extends Command {

  private Command changeDir;

  /**
   * Creates initial DirStackPopper object by setting instance variables
   * 
   * @param b Builder object to construct DirStackPopper object
   */
  public DirStackPopper(Builder b) {
    super(b);
    changeDir = new DirectoryChanger(b);
  }

  /**
   * Removes the top entry in the directory stack and changes the current
   * working directory to the removed entry given valid argument (empty)
   * 
   * @param argument The argument to validate
   * @return Output to shell (empty string)
   */
  @Override
  public String run(String argument) {
    if (!validateArgs(argument)) {
      return "";
    }
    if (DirStackPusher.getDirectoryStack().isEmpty()) {
      printMessage.printDirectoryStackEmpty();
      return "";
    }
    changeDir.run(DirStackPusher.getDirectoryStack().pop());
    return " ";
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of popd command
   * 
   * @return formatted information about popd command
   */
  public String toString() {
    String command = "POPD";
    String name = "\n\tpopd - pop top entry from directory stack";
    String synopsis = "\n\tpop";
    String description =
        "\n\tRemove the top entry from the directory stack and change the "
            + "current working directory to the removed entry.";
    String errors =
        "\n\tNo arguments expected.\n\tCurrent working directory remains "
            + "unchanged when the directory stack is empty.";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors;
  }
}
