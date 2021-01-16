package a2a;

/**
 * Outputs to shell.
 * 
 * @author Ananya Poddar
 */

import java.util.ArrayList;

public class ShellOutput {

  public void printCommandError() {
    System.out.println("Invalid command.");
  }

  public void printArgTypeError() {
    System.out.println("Invalid argument type.");
  }

  public void printNumArgsError() {
    System.out.println("Invalid number of arguments.");
  }

  public void printNegativeIntegerError() {
    System.out.println("Integer argument must be >=0.");
  }

  public void printInvalidDirectoryError() {
    System.out.println("Invalid directory(s).");
  }

  public void printInvalidFileError() {
    System.out.println("Invalid file(s).");
  }

  public void printInvalidURLError() {
    System.out.println("Invalid URL.");
  }

  public void printInvalidString() {
    System.out.println("Invalid String literal given.");
  }

  public void printInvalidFilePath() {
    System.out.println("Invalid path to file given.");
  }

  public void printInvalidDirFilePath() {
    System.out.println("Invalid path to file/directory.");
  }

  public void printInvalidCharError() {
    System.out.println("File/directory name contains invalid characters.");
  }

  public void printExistingDirOrFileError() {
    System.out.println("File/directory already exists in specified directory.");
  }

  public void printParentError() {
    System.out.println(
        "Parent or current file/directory cannot be copied, moved, or removed.");
  }

  public void printCopyinError() {
    System.out
        .println("Unable to copy or move directory to specified location.");
  }

  public void printInvalidUserManual(String arg) {
    System.out.println("No manual entry for " + arg + ".");
  }

  public void printDirectoryStackEmpty() {
    System.out.println("Directory stack is empty.");
  }

  public void printInvalidClassError() {
    System.out.println("Class not found.");
  }

  public void printNotFirstCommandError() {
    System.out.println("Must be the first command called.");
  }

  public void printNoContent() {
    System.out.println("No file content provided.");
  }
  
  public void printInvalidArgSyntax() {
    System.out.println("Invalid argument syntax. Refer to user manual.");
}
  
  public void printSpecificInvalidPath(String path) {
    System.out.println(path + " is an invalid path.");
  }
  
  public void printElementNotFound() {
    System.out.println("No such directory/file was found.");
  }

  public void printGeneralException() {
    System.out.println("Something went wrong.");
  }

  public void printString(String str) {
    System.out.println(str);
  }

  public void printUserPrompt(String currentPath) {
    System.out.print(currentPath + ":");
  }

  public void printStringArraylist(ArrayList<String> output) {
    for (String row : output)
      System.out.println(row);
  }
}
