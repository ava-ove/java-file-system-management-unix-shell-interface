package a2a;

// import java.util.ArrayList;
// import java.util.Arrays;

/**
 * Represents man command. Displays the user manual of a command, if argument
 * valid.
 * 
 * @author Raisa Haque
 *
 */
public class UserManual extends Command {

  private ValidCommand validateCommand;
  private Redirection redirectOutput;

  /**
   * Creates initial UserManual object by setting default instance variable
   * values
   * 
   * @param b Builder object to construct UserManual object
   */
  public UserManual(Builder b) {
    super(b);
    validateCommand = new ValidCommand();
    redirectOutput = new Redirection(b);
  }

  /**
   * Checks that argument is a valid command
   * 
   * @param arguments Argument to be validated
   * @return True if valid command, false otherwise
   */
  @Override
  public boolean validateArgs(String argument) {
    parsedArgs.clear();
    super.parseArgs(argument);

    if (!(this.parsedArgs.size() == 1 || this.parsedArgs.size() == 3)
        || argument.isEmpty()) {
      printMessage.printNumArgsError();
      return false;
    }
    if (!(validateCommand.getValidCommands())
        .containsKey(this.parsedArgs.get(0))) {
      printMessage.printInvalidUserManual(this.parsedArgs.get(0));
      return false;
    }
    return true;
  }

  /**
   * Outputs user manual for command, if valid
   * 
   * @param arguments Argument for which, if valid, the user manual will be
   *        output
   * @return String of the user manual, if argument valid
   */
  @Override
  public String run(String arguments) {

    parseArgs(arguments);

    if (!validateArgs(arguments)) {
      return "";
    }

    String userManual = (validateCommand.getValidCommands())
        .get(this.parsedArgs.remove(0)).toString();

    if (this.parsedArgs.size() == 2) {
      redirectOutput.redirect(userManual, this.parsedArgs.get(0),
          this.parsedArgs.get(1));
      return "";
    }
    printMessage.printString(userManual);
    return userManual;
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of man command
   * 
   * @return formatted information about man command
   */
  public String toString() {
    String command = "MAN";
    String name = "\n\tman - print command manual";
    String synopsis = "\n\tman COMMAND [>/>> OUTFILE]";
    String description = "\n\tPrint the user manual for the specified command.";
    String errors =
        "\n\tFirst argument must be valid command.\n\tCan be followed by valid "
            + "redirection arguments.";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors + redirectOutput.toString();
  }
}
