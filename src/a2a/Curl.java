package a2a;

import java.net.HttpURLConnection;
import java.net.URL;
import driver.JShell;

/**
 * Represents curl command. Retrieves the file at the argument URL and adds it
 * to the current working directory.
 * 
 * @author Fariha Fyrooz
 *
 */

public class Curl extends Command {

  private FileOverwriter overFile;
  private Builder build;

  /**
   * Creates initial Curl object by setting default instance variable values
   * from Command constructor
   * 
   * @param b Builder object to construct Curl object
   */

  public Curl(Builder b) {
    super(b);
  }

  /**
   * Represents Builder object to create Curl object with optional Command
   * parameters
   * 
   * @author Fariha Fyrooz
   */
  public static class Builder extends Command.Builder {

    /**
     * Builds and returns Curl object
     * 
     * @return Curl object
     */
    public Curl build() {
      return new Curl(this);
    }
  }


  /**
   * Checks that there is one argument and that it's a valid URL containing a
   * .txt file.
   * 
   * @param args The argument to be validated.
   * @return true if argument is valid, false otherwise.
   */
  public boolean validateArgs(String args) {

    if (args.trim().isEmpty()) {
      printMessage.printNumArgsError();
      return false;
    }

    try {
      // ensures that args is a valid URL that ends with .txt
      URL givenUrl = new URL(args);
      HttpURLConnection huc = (HttpURLConnection) givenUrl.openConnection();
      int responseCode = huc.getResponseCode();
      if (HttpURLConnection.HTTP_OK != responseCode || !args.endsWith(".txt")) {
        printMessage.printInvalidURLError();
        return false;
      }
    } catch (Exception e) {
      e.getStackTrace();
      printMessage.printInvalidURLError();
      return false;
    }
    return true;
  }

  /**
   * Adds file at given URL to working directory if the URL is valid
   * 
   * @param args URL leading to a file to be validated and added to working
   *        directory
   * @return String of content received from the URL if valid, empty String
   *         otherwise
   */

  @Override
  public String run(String args) {
    if (!validateArgs(args)) {
      return "";
    }

    String content = "";
    // name of file that URL leads to
    File newFile = modifier.addFile(JShell.getCurrentPath()
        + args.substring(args.lastIndexOf("/"), args.indexOf(".txt")));
    if (JShell.getCurrentPath().equals("/")) {
      newFile = modifier
          .addFile(args.substring(args.lastIndexOf("/"), args.indexOf(".txt")));
    }
    // calls getRemoteDataFromUrl to fetch data and adds it to the local file
    content = rdFetcher.getRemoteDataFromUrl(args, newFile);
    overFile = new FileOverwriter(new File("", ""), build);
    overFile = new FileOverwriter(newFile, build);
    overFile.run(content); 
    
    return content;
  }
 

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of curl command
   * 
   * @return formatted information about curl command
   */

  @Override
  public String toString() {
    String command = "CURL";
    String name = "\n\tcurl - create file from URL";
    String synopsis = "\n\tcurl URL";
    String description =
        "\n\tRetrieves the file at the argument URL and adds it to the current "
            + "working directory.";
    String errors = "\n\tArgument must be a valid URL leading to a file.";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors;
  }


  // REFERENCE for Curl code
  // https://docs.oracle.com/javase/tutorial/networking/urls/readingURL.html

}
