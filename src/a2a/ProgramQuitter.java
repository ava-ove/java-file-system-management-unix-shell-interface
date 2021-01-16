package a2a;

/**
 * Represents exit command object, to quit the JShell program
 * 
 * @author Ananya Poddar
 *
 */
public class ProgramQuitter extends Command {
  /**
   * Constructs ProgramQuitter object by setting default instance variable
   * values from Command constructor
   * 
   * @param b Builder object to construct ProgramQuitter object
   * 
   */
  public ProgramQuitter(Builder b) {
    super(b);
  }

  /**
   * Exits program if arguments are valid
   * 
   * @param argument The argument to be validated before exiting
   * @return Output to shell (empty string)
   */
  @Override
  public String run(String argument) {
    if (!validateArgs(argument))
      return "";
    System.exit(0);
    return " ";
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of exit command
   * 
   * @return formatted information about exit command
   */
  public String toString() {
    String command = "EXIT";
    String name = "\n\texit - terminate shell";
    String synopsis = "\n\texit ";
    String description = "\n\tTerminate the shell.";
    String errors = "\n\tNo arguments expected.";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors;
  }
}
