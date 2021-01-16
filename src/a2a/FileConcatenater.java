package a2a;

import java.util.ArrayList;

/**
 * Represents cat command. Concatenates contents of files and outputs
 * appropriately
 * 
 * @author Fariha Fyrooz
 *
 */
public class FileConcatenater extends Command {

  private File fileName;
  private Redirection redirectOutput;

  /**
   * Creates initial FileConcatenater object by setting default instance
   * variable values from Command constructor and initializing Redirection
   * object
   * 
   * @param b Builder object to construct FileConcatenator object
   */
  public FileConcatenater(Builder b) {
    super(b);
    this.redirectOutput = new Redirection(b);
  }

  /**
   * Represents Builder object to create FileConcatenater object with optional
   * Command parameters
   * 
   * @author Fariha Fyrooz
   */
  public static class Builder extends Command.Builder {
    /**
     * Builds and returns FileConcatenater object
     * 
     * @return FileConcatenater object
     */
    public FileConcatenater build() {
      return new FileConcatenater(this);
    }
  }

  /**
   * Validates there is at least one argument given. Outputs error message
   * otherwise.
   * 
   * @param arguments The argument to be validated
   * @return true if at least 1 argument given, false otherwise
   */
  public boolean validateNumArgs(String arguments) {

    if (arguments.length() == 0) {
      printMessage.printNumArgsError();
      return false;
    }
    return true;
  }

  /**
   * Validates that file exists at given path and sets fileName as given File
   * 
   * @param arguments The argument is the file path to be validated
   * @return true if argument is valid, false otherwise
   */
  @Override
  public boolean validateArgs(String arguments) {

    fileName = validateElement.validateFile(arguments);
    if (fileName != null) {
      return true;
    }
    return false;
  }

  /**
   * Outputs concatenated files based on whether output will be printed to shell
   * or added to a file
   * 
   * @param redirectCondition If true, redirects content to indicated file
   * @param validFiles content of each valid file
   * @param numFiles Number of files being concatenated
   * @return Concatenated files
   */
  private String output(boolean redirectCondition, ArrayList<String> validFiles,
      int numFiles) {
    // adds and formats all content to be redirected
    if (redirectCondition) {
      StringBuffer valid = new StringBuffer();
      for (String row : validFiles) {
        valid.append(row + "\n");
      }

      redirectOutput.redirect(
          valid.toString().substring(0, valid.toString().length() - 1),
          parsedArgs.get(numFiles), parsedArgs.get(numFiles + 1));
      return valid.toString().substring(0, valid.toString().length() - 1);
    }
    // output to shell
    else {
      printMessage.printStringArraylist(validFiles);
      StringBuffer toReturn = new StringBuffer();
      for (String row : validFiles)
        toReturn.append(row + "\n");
      return toReturn.substring(0, toReturn.toString().length() - 1);
    }

  }

  /**
   * Outputs concatenated file(s) if valid to shell or redirects to a file,
   * error if invalid
   * 
   * @param arguments The file(s) to be concatenated, if valid
   * @return Concatenated files if all files valid, error otherwise. Empty
   *         String if wrong number of arguments
   */
  @Override
  public String run(String arguments) {
    if (!validateNumArgs(arguments))
      return "";
    // files stores array of given arguments (presumably Files) validFiles will
    // store contents of valid Files
    String[] files = arguments.split("\\s+");
    ArrayList<String> validFiles = new ArrayList<String>();
    String spacing = "\n\n";

    int numFiles = files.length;
    boolean redirectCond = (numFiles >= 3 && (files[numFiles - 2].equals(">")
        || files[numFiles - 2].equals(">>")));
    if (redirectCond)
      numFiles -= 2;

    for (int i = 0; i < numFiles; i++) {
      /*
       * There are no extra line breaks if there is only 1 file or if it's the
       * last file. Gets full path of given file, adds its content to validFiles
       * ArrayList if valid and returns error if the file is invalid
       */
      if (i == numFiles - 1)
        spacing = "";
      files[i] = pathGiver.getFullPath(files[i]);
      if (validateArgs(files[i])) {
        validFiles.add(fileName.getContent() + spacing);
      } else {
        printMessage.printInvalidFileError();
        return "Invalid file(s).";
      }
    }
    parsedArgs.clear();
    super.parseArgs(arguments);
    return output(redirectCond, validFiles, numFiles);
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of cat command
   * 
   * @return formatted information about cat command
   */
  public String toString() {
    String command = "CAT";
    String name =
        "\n\tcat - concatenate files and output on the shell or redirect";
    String synopsis = "\n\tcat FILE1 [FILE2...] [>/>> OUTFILE]";
    String description =
        "\n\tConcatenate one or more files and output to shell or redirect to "
            + "OUTFILE.\n\tFile path(s) may be a full or relative path.";
    String errors =
        "\n\tMust have 1 or more arguments.\n\tArguments given must be valid "
            + "paths to an existing file. The last two arguments can be valid "
            + "redirection arguments.";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors + redirectOutput.toString();
  }


}
