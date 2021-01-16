// **********************************************************
// Assignment2:
// Student1:
// UTORID user_name: poddara2
// UT Student #: 1006160761
// Author: Ananya Poddar
//
// Student2:
// UTORID user_name: fyroozfa
// UT Student #: 1006071830
// Author: Fariha Fyrooz
//
// Student3:
// UTORID user_name: haquerai
// UT Student #: 1006162395
// Author: Raisa Haque
//
// Student4:
// UTORID user_name:oveisiav
// UT Student #: 1006412482
// Author: Ava Oveisi
//
//
// Honor Code: I pledge that this program represents my own
// program code and that I have coded on my own. I received
// help from no one in designing and debugging my program.
// I have also read the plagiarism section in the course info
// sheet of CSC B07 and understand the consequences.
// *********************************************************
package driver;

import java.util.ArrayList;
import java.util.Scanner;
import a2a.AudioOutput;
import a2a.AudioOutput.Builder;
import a2a.FileSystem;
import a2a.InputParser;
import a2a.ShellOutput;
import a2a.ValidCommand;

/**
 * Takes and stores user input and invokes appropriate commands
 * 
 * @author Raisa Haque
 * @author Ananya Poddar
 */
public class JShell {

  private static ShellOutput outputPath = new ShellOutput();

  private static ArrayList<String> inputLog = new ArrayList<String>();

  private static String currentPath = "/";

  public static ArrayList<String> getInputLog() {
    return inputLog;
  }

  public static String getCurrentPath() {
    return currentPath;
  }

  public static void setCurrentPath(String path) {
    currentPath = path;
  }

  private static Builder builderForAudioOutput;

  public static void main(String[] args) {
    String userInput = "";
    // sets the FileSystemModifier to be the actual modifier, not mock
    try (Scanner in = new Scanner(System.in)) {
      ValidCommand validateCommand = new ValidCommand();
      // adds "/" as root directory to File System
      FileSystem.createFileSystemInstance();
      while (true) {
        outputPath.printUserPrompt(currentPath);
        userInput = in.nextLine();
        InputParser parser = new InputParser(userInput);
        // user input for speak
        if (userInput.trim().startsWith("speak")) {
          AudioOutput speak = new AudioOutput(builderForAudioOutput);
          userInput = speak.getInput(parser.getArguments());
        }
        // store in inputLog if user input given
        if (!userInput.trim().isEmpty()) {
          inputLog.add(userInput);
          parser = new InputParser(userInput);
          if (validateCommand.validate(parser.getCommand())) {
            /*
             * gets the constructor for the appropriate command from
             * ValidCommands, hashmap and calls the run method of that command
             */
            (validateCommand.getValidCommands()).get(parser.getCommand())
                .run(parser.getArguments());
          }
        }
      }
    }
  }
}
