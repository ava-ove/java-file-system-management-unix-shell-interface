package a2a;

/**
 * Represents a parser for a command/argument pair.
 * 
 * @author Ava Oveisi
 */
public class InputParser {

  private String command = ""; // default no command given
  private String arguments = ""; // default no arguments given

  /**
   * Parses command and each argument from input (separated by space(s)) and
   * stores in this.command and this.arguments, respectively
   * 
   * @param input Input to be parsed
   */
  public InputParser(String input) {
    this.command = input.trim().split("\\s+")[0];
    if (!(input.trim().equals(this.command))) {
      for (int i = 1; i < input.trim().split("\\s+").length; i++)
        this.arguments += input.trim().split("\\s+")[i] + " ";
      this.arguments = this.arguments.trim();
    }
  }
  
  /**
   * Gets and returns this.command
   * 
   * @return command
   */
  public String getCommand() {
    return this.command;
  }
  
  /**
   * Gets and returns this.arguments
   * 
   * @return command
   */
  public String getArguments() {
    return this.arguments;
  }
   
}
