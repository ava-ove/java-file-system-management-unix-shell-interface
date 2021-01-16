package a2a;

import java.util.ArrayList;
// import java.util.Arrays;
import driver.JShell;

/**
 * Represents history table object for history command
 * 
 * @author Ananya Poddar
 *
 */
public class History extends Command {

  private ArrayList<String> historyTableVals;
  private ArrayList<String> historyTable;
  private Redirection redirectOutput;

  /**
   * Constructs history object by setting default instance variable values from
   * Command constructor and initializing list of values for history table, and
   * redirection collaborator
   * 
   * @param b Builder object to construct history object
   * 
   */
  public History(Builder b) {
    super(b);
    this.historyTableVals = JShell.getInputLog();
    this.redirectOutput = new Redirection(b);
  }

  /**
   * Represents Builder object to create history object with optional Command
   * parameters
   * 
   * @author Ananya Poddar
   *
   */
  public static class Builder extends Command.Builder {
    /**
     * Builds and returns History object
     * 
     * @return History object
     */
    public History build() {
      return new History(this);
    }
  }

  /**
   * Validates arguments, if any specified, which must be an integer >=0 or must
   * be correctly formatted for redirection
   * 
   * @param argument The argument to be validated.
   * @return true if argument is valid, false otherwise.
   */
  @Override
  public boolean validateArgs(String argument) {
    if (argument.trim().isEmpty()) {
      return true;
    }
    parsedArgs.clear();
    super.parseArgs(argument);
    // can have 1 2 or 3 arguments
    if (this.parsedArgs.size() > 3 || this.parsedArgs.size() > 1
        && !this.parsedArgs.contains(">") && !this.parsedArgs.contains(">>")) {
      printMessage.printNumArgsError();
      return false;
    }
    if (this.parsedArgs.size() == 1 || this.parsedArgs.size() == 3) {
      try {
        // regardless of it if has 1 arg or 3, the first one must be a valid int
        if (Integer.parseInt(parsedArgs.get(0)) >= 0) {
          return true;
        } else {
          printMessage.printNegativeIntegerError();
          return false;
        }
      } catch (Exception ex) {
        printMessage.printArgTypeError();
        return false;
      }
    }
    // 2 arguments (formatted as: history > 1)
    else
      return true;
  }

  /**
   * Creates history table with rows equal to specified number of commands
   * 
   * @param lastNumCommands The number of commands to be put into history table
   */
  private void makeHistoryTable(int lastNumCommands) {
    // must be reset everytime
    this.historyTable = new ArrayList<>();
    if (lastNumCommands == 0)
      return;
    else {
      /*
       * The starting value takes the maximum of 0 and the last number of
       * commands requested in the case that more commands were requested than
       * stored
       */
      for (int i = Math.max(0,
          historyTableVals.size() - lastNumCommands); i < historyTableVals
              .size(); i++)
        this.historyTable.add(String.valueOf(i + 1) + ". "
            + String.valueOf(historyTableVals.get(i)));
    }
  }

  /**
   * Creates history table of appropriate size if argument is valid and
   * redirects or outputs history
   * 
   * @param argument The input argument to be validated and used to get
   *        appropriate size of outputHistory
   * @return String representation of history table
   */
  @Override
  public String run(String argument) {
    if (!validateArgs(argument))
      return "";
    // all the ways where you would have no integer output
    if (argument.isEmpty() || parsedArgs.size() == 2) {
      makeHistoryTable((this.historyTableVals).size());
    } else {
      makeHistoryTable(Integer.parseInt(argument.trim().split(" ")[0]));
    }

    StringBuffer tableReader = new StringBuffer();
    for (String row : this.historyTable) {
      tableReader.append(row.trim() + "\n");
    }
    // redirection
    if (parsedArgs.size() > 1) {
      // no integer argument before redirection
      if (parsedArgs.size() == 2) {
        redirectOutput.redirect(tableReader.toString(), parsedArgs.get(0),
            parsedArgs.get(1));
      } else
        redirectOutput.redirect(tableReader.toString(), parsedArgs.get(1),
            parsedArgs.get(2));
    } else {
      printMessage.printStringArraylist(this.historyTable);
    }
    return tableReader.toString();
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of history command
   * 
   * @return formatted information about history command
   */
  public String toString() {
    String command = "HISTORY";
    String name = "\n\thistory - output history of commands or redirect";
    String synopsis = "\n\thistory [number] [>/>> OUTFILE]";
    String description = "\n\tOutput numbered table of all commands if no "
        + "argument is specified, or last [number] commands if specified. "
        + "\n\tRedirect output if specified.";

    String errors =
        "\n\t1 optional integer argument >=0 valid, and optional redirection "
            + "arguments are valid.";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors + redirectOutput.toString();
  }

  public Object getHistoryTable() {
    return historyTable;
  }
}
