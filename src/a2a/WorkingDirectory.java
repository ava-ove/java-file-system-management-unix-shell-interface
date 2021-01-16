package a2a;

import driver.JShell;

/**
 * Represents pwd command. Outputs full path to working directory.
 * 
 * @author Fariha Fyrooz
 *
 */
public class WorkingDirectory extends Command {

  private Redirection redirectOutput;

  /**
   * Creates initial WorkingDirectory object by setting default instance
   * variable values from Command constructor and initializing Redirection
   * collaborator
   * 
   * @param b Builder object to construct WorkingDirectory object
   */
  public WorkingDirectory(Builder b) {
    super(b);
    this.redirectOutput = new Redirection(b);
  }

  public boolean validateArgs(String arguments) {
    // checks if 0 args
    if (arguments.trim().isEmpty())
      return true;
    parsedArgs.clear();
    super.parseArgs(arguments);

    // error if not 0 or 2 args, or if doesn't contain >/>> if 2 args
    if (this.parsedArgs.size() == 2) {
      if (parsedArgs.get(0).equals(">") || parsedArgs.get(0).equals(">>")) {
        return true;
      }
      printMessage.printArgTypeError();
      return false;
    }
    printMessage.printNumArgsError();
    return false;
  }



  /**
   * Outputs full path to working directory, if valid
   * 
   * @param arguments Argument to be validated (must be empty) before outputting
   *        working directory
   * @return String containing current directory if arguments are valid, empty
   *         String if not
   */
  @Override
  public String run(String arguments) {
    if (!validateArgs(arguments)) {
      return "";
    }
    if (parsedArgs.size() == 2) {
      redirectOutput.redirect(JShell.getCurrentPath(), parsedArgs.get(0),
          parsedArgs.get(1));
    } else {
      printMessage.printString(JShell.getCurrentPath());
    }
    return JShell.getCurrentPath();
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of pwd command
   * 
   * @return formatted information about pwd command
   */
  public String toString() {
    String command = "PWD";
    String name = "\n\tpwd - output name of current working directory";
    String synopsis = "\n\tpwd [>/>> OUTFILE]";
    String description =
        "\n\tOutput the full path of the current working directory.";
    String errors =
        "\n\tTakes 0 arguments, or 2 valid arguments for redirection.";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors + redirectOutput.toString();
  }

}
