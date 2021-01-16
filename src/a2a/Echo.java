package a2a;

/**
 * Represents Echo command which prints STRING given
 * 
 * @author Ava Oveisi
 */
public class Echo extends Command {

  private Redirection redirectObj;

  /**
   * Creates initial Echo object by setting default instance variable values
   * from Command constructor and initializing Redirection object.
   * 
   * @param b Builder object to construct Echo object
   */
  public Echo(Builder b) {
    super(b);
    redirectObj = new Redirection(b);
  }

  /**
   * Represents Builder object to create Echo object
   */
  public static class Builder extends Command.Builder {

    /**
     * Builds and returns Echo object
     * 
     * @return Echo object
     */
    public Echo build() {
      return new Echo(this);
    }
  }

  /**
   * Stores STRING with surrounding quotations removed, and the rest of the
   * arguments provided in this.parsedArgs
   * 
   * @param args command arguments to be parsed
   */
  @Override
  public void parseArgs(String args) {
    String quote = "\"";
    this.parsedArgs.clear();
    // storing STRING without quotation surrounding it
    if (args.contains(quote) && args.trim().indexOf(quote) == 0
        && args.lastIndexOf(quote) != args.indexOf(quote)) {
      this.parsedArgs.add(args.trim().substring(1, args.lastIndexOf(quote)));
      // storing >/>> and OUTFILE if they exist
      if (!args.substring(args.lastIndexOf(quote)).trim().equals(quote)) {
        for (String arg : args.substring(args.lastIndexOf(quote) + 1).trim()
            .split("\\s"))
          this.parsedArgs.add(arg);
      }
    }
  }

  /**
   * Returns true if args contains 1 or 3 arguments, first one being a valid
   * string literal otherwise returns false.
   * 
   * @param args command arguments
   * @return true if args is a valid argument, false otherwise
   */
  @Override
  public boolean validateArgs(String args) {
    parseArgs(args);
    // string given is invalid
    if (this.parsedArgs.isEmpty() || this.parsedArgs.get(0).contains("\"")) {
      printMessage.printInvalidString();
      return false;
    }
    // invalid if number of arguments are not 1 or 3
    if (this.parsedArgs.size() != 1 && this.parsedArgs.size() != 3) {
      // more than 3 args given
      printMessage.printNumArgsError();
      return false;
    }
    return true;
  }

  /**
   * Prints content of STRING if only one argument given, otherwise redirects
   * 
   * @param args arguments to be executed
   * @return STRING if STRING is valid, empty String otherwise
   */
  @Override
  public String run(String args) {
    if (validateArgs(args)) {
      // print if only STRING given
      if (this.parsedArgs.size() == 1) {
        printMessage.printString(this.parsedArgs.get(0));
      } else {
        redirectObj.redirect(this.parsedArgs.get(0), this.parsedArgs.get(1),
            this.parsedArgs.get(2));
      }
      return this.parsedArgs.get(0);
    }
    return "";
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors for both modes of echo command
   * 
   * @return formatted information about echo command
   */
  public String toString() {
    String command = "ECHO";
    String name = "\n\techo - print STRING [>/>> OUTFILE]";
    String synopsis = "\n\techo STRING";
    String description =
        "\n\tPrint STRING or redirect STRING output to " + "OUTFILE";
    String errors = "\n\tThe first argument must be a valid string literal "
        + "argument.\n\tCan be followed by valid redirection arguments.";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors + redirectObj.toString();
  }
}
