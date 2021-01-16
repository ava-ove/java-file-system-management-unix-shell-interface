package a2a;

import a2a.Command.Builder;
import interfaces.IFileSystemModifier;
import interfaces.IRedirection;

/**
 * Represents Redirection Object which redirects to FileOverwriter or
 * FileAppender based on redirection input
 * 
 * @author Ava Oveisi
 */
public class Redirection implements IRedirection {
  private FileOverwriter overFile;
  private FileAppender appendFile;
  private File mockFile;
  private String fileName;
  private String fullPath;
  /**
   * Stores ValidFileSysElement object to be used for validating
   * files/directories
   */
  protected ValidFileSysElement validateElement;
  /**
   * Stores FullPathGiver object to be used for getting full paths of
   * files/directories
   */
  protected final FullPathGiver pathGiver;
  /**
   * Stores redirection operator if valid (> or >>) otherwise stores ""
   */
  protected String redirectOperator;
  /**
   * Stores content being redirected
   */
  protected String content;
  /**
   * Stores OUTFILE which is being created, overwritten and/or appended
   */
  protected String outfile;
  /**
   * Stores file system modifier to add directories or files
   */
  public static IFileSystemModifier modifier;
  /**
   * Stores ShellOutput object for printing
   */
  protected ShellOutput printMessage;

  /**
   * Creates Redirection object and initializes all instance variables to
   * appropriate values
   * 
   * @param b Builder to construct Redirection object
   */
  public Redirection(a2a.Command.Builder b) {
    this.content = "";
    this.redirectOperator = "";
    this.outfile = "";
    this.fullPath = "/";
    Redirection.modifier = new FileSystemModifier();
    validateElement = new ValidFileSysElement();
    pathGiver = new FullPathGiver();
    mockFile = new File("", "");
    overFile = new FileOverwriter(mockFile, b);
    appendFile = new FileAppender(mockFile, b);
    this.fileName = "";
    this.printMessage = new ShellOutput();
  }

  /**
   * Returns whether or not redirection allowed: true if content is non empty,
   * redirection operator is ">" or ">>", outfile is a valid path to the file
   * and the file name contains valid characters. Otherwise returns false.
   * 
   * @param content Content being overwritten or appended
   * @param redirectOperator Redirection operator
   * @param outfile OUTFILE where content is being overwritten or appended to
   * @return true if valid input for redirection given, false otherwise
   */
  public boolean allowRedirection(String content, String redirectOperator,
      String outfile) {
    // invalid if redirect operator are invalid or content is empty
    this.fullPath = pathGiver.getFullPath(outfile); // get full path of outfile
    if (content.isEmpty()) {
      printMessage.printNoContent();
      return false;
    }
    if (!redirectOperator.equals(">") && !redirectOperator.equals(">>")) {
      printMessage.printArgTypeError();
      return false;
    }
    // invalid if path up until file is invalid
    if (fullPath.lastIndexOf("/") != 0 && !validateElement
        .validateDirectory(fullPath.substring(0, fullPath.lastIndexOf("/")))) {
      printMessage.printInvalidDirectoryError();
      return false;
    }
    // invalid if path to file given ending with /
    if (outfile.trim().endsWith("/")) {
      printMessage.printInvalidFilePath();
      return false;
    }
    fileName = fullPath.substring(fullPath.lastIndexOf("/") + 1);
    // invalid if file contains invalid characters
    if (!validateElement.containsValidCharacters(fileName)) {
      printMessage.printInvalidCharError();
      return false;
    }
    return true;
  }

  /**
   * Calls appropriate methods based on redirectOperator, if redirection allowed
   * 
   * @param content Content being overwritten or appended
   * @param redirectOperator Redirection operator
   * @param outfile OUTFILE where content is being overwritten or appended to
   */
  public void redirect(String content, String redirectOperator,
      String outfile) {
    this.content = content;
    this.redirectOperator = redirectOperator;
    this.outfile = outfile;

    if (allowRedirection(content, redirectOperator, outfile)) {
      File existingOrAddedFile = modifier.addFile(outfile);
      // redirect if valid path given and file created with addFile
      if (existingOrAddedFile != null) {
        if (redirectOperator.equals(">"))
          redirectAndOverwrite(existingOrAddedFile, content);
        else if (redirectOperator.equals(">>"))
          this.redirectAndAppend(existingOrAddedFile, content);
      }
    }
  }

  /**
   * Redirects output to file and appends input content to the file
   * 
   * @param originalFile File to which content will be appended
   * @param content Content to be appended to originalFile
   */
  public void redirectAndAppend(File originalFile, String content) {
    this.appendFile = new FileAppender(originalFile, new Builder());
    this.appendFile.run(content);
  }

  /**
   * Redirects output to file and overwrites content of the file with input
   * content
   * 
   * @param originalFile File whose content will be overwritten
   * @param content Content to replace any original content of originalFile
   */
  public void redirectAndOverwrite(File originalFile, String content) {
    this.overFile = new FileOverwriter(originalFile, new Builder());
    this.overFile.run(content);
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of the different redirection options
   * 
   * @return formatted information about redirection
   */
  public String toString() {
    String errors =
        "\n\tPath for OUTFILE must be either a valid existing file, or a "
            + "valid path to where the new file must be created.\n\tFor new "
            + "files, OUTFILE must not contain any invalid characters."
            + "\n\tDoes not create OUTFILE if there is no output from command";
    return "\n\nREDIRECTION" + this.overFile.toString() + "\n"
        + this.appendFile.toString() + "\nERRORS" + errors;
  }
}
