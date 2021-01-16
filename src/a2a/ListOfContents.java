package a2a;

import java.util.Iterator;
import driver.JShell;

/**
 * Represents ls command. Displays list of file/directory contents.
 * 
 * @author Ava Oveisi
 *
 */
public class ListOfContents extends Command implements Iterable<String> {

  private Redirection redirectObj;
  private String redirectOperator;
  private String redirectFilePath;
  private String output;
  private boolean recursiveListing;
  private static String beginningDirForRecursiveListing = "/";

  /**
   * Creates initial ListOfContents object by setting default instance variable
   * values from Command constructor.
   * 
   * @param b Builder object to construct ListOfContents object
   */
  public ListOfContents(Builder b) {
    super(b);
    // by default no recursive listing is done
    this.recursiveListing = false;
    this.redirectObj = new Redirection(b);
    this.output = "";
    this.redirectOperator = "";
    this.redirectFilePath = "";
  }

  /**
   * Checks if directory(s) and/or file(s) given are valid and outputs error if
   * at least one is not. Sets redirect Operator and redirectFilePath if user
   * invokes redirection.
   * 
   * @param args command arguments
   * @return true if args is a valid argument, false otherwise
   */
  @Override
  public boolean validateArgs(String args) {
    parseArgs(args);
    if (!args.trim().isEmpty()) {
      // stores index up until to check for valid file/directory
      int validateUpToIndex = this.parsedArgs.size();
      // Do not check the last two elements if the second last element is >/>>
      if (this.parsedArgs.size() >= 2 && (this.parsedArgs
          .get(this.parsedArgs.size() - 2).equals(">")
          || this.parsedArgs.get(this.parsedArgs.size() - 2).equals(">>"))) {
        validateUpToIndex -= 2;
        redirectOperator = this.parsedArgs.get(this.parsedArgs.size() - 2);
        redirectFilePath = this.parsedArgs.get(this.parsedArgs.size() - 1);
        this.parsedArgs.remove(this.parsedArgs.size() - 2);
        this.parsedArgs.remove(this.parsedArgs.size() - 1);
      }
      // check if item in parsedArgs is not valid file/directory
      ValidFileSysElement validFileE = new ValidFileSysElement();
      for (int i = 0; i < validateUpToIndex; i++) {
        if (!validateElement.validateDirectory(this.parsedArgs.get(i))
            && validFileE.validateFile(this.parsedArgs.get(i)) == null) {
          printMessage.printInvalidDirectoryError();
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Stores the full path of file(s) and directory(s) given in args and
   * redirection operator >/>> if provided, in this.parsedArgs.
   * 
   * @param args command arguments
   */
  @Override
  public void parseArgs(String args) {
    this.parsedArgs.clear();
    if (args.trim().equals(""))
      return;
    for (String arg : args.trim().split("\\s")) {
      // get full path of arg if it is not -R
      if (arg.equals("-R"))
        recursiveListing = true;
      else if (arg.equals(">") || arg.equals(">>"))
        this.parsedArgs.add(arg);
      else
        this.parsedArgs.add((new FullPathGiver()).getFullPath(arg));
    }
  }

  private void storeContentOfCurrentDirectory() {
    /*
     * Modifies output so it contains files and subdirectories of current
     * directory
     */
    for (String key : FileSystem.getOuterMap().get(JShell.getCurrentPath())
        .keySet())
      this.output += key.substring(key.lastIndexOf("/") + 1) + "\n";
  }

  private String getRelativePath(String fullPath, String parentPath) {
    /*
     * Returns name of directory/file when given full path to it or returned /
     * if / given.
     */
    if (!fullPath.equals("/")) {
      if (!fullPath.equals(parentPath) && fullPath.contains(parentPath)) {
        return fullPath
            .substring(fullPath.indexOf(parentPath) + parentPath.length())
            .replaceAll("^/+", "");
      } else {
        return fullPath.substring(fullPath.lastIndexOf("/")).replaceAll("^/+",
            "");
      }
    }
    return fullPath;
  }

  private void storeContentOfMultipleDirectoriesRecursively() {
    /*
     * Modifies output so it contains files and subdirectories contained in the
     * multiple directories in parsedArgs
     */
    boolean directoryContainsElements = false;
    for (String arg : this.parsedArgs) {
      if (validateElement.validateDirectory(arg)) {
        ListOfContents.beginningDirForRecursiveListing = arg;
        // /directory1/a -> /directory1/a/a1/a3, /directory1/a/a2
        this.output += arg + ":" + "\n";
        // recursive listing
        for (String x : this) {
          directoryContainsElements = (boolean) true;
          if (!x.equals(ListOfContents.beginningDirForRecursiveListing))
            this.output +=
                getRelativePath(x, beginningDirForRecursiveListing) + " ";
        }
        if (directoryContainsElements)
          this.output += "\n";
      } else {
        this.output +=
            getRelativePath(arg, beginningDirForRecursiveListing) + "\n";
      }
      this.output += "\n";
    }
  }


  private void storeContentOfDirectoryRecursively() {
    /*
     * Modifies output so it contains all subdirectories and files recursively
     * of directory given if one argument stored in this.parsedArgs or of
     * current directory if no argument stored in this.parsedArgs
     */
    // to check if valid file
    ValidFileSysElement validFileE = new ValidFileSysElement();
    // no path given
    if (this.parsedArgs.isEmpty()) // recursive listing on current directory
      ListOfContents.beginningDirForRecursiveListing = JShell.getCurrentPath();
    // one file given
    else if (validFileE.validateFile(this.parsedArgs.get(0)) != null) {
      this.output += getRelativePath(this.parsedArgs.get(0),
          beginningDirForRecursiveListing) + "\n";
      return;

    } else { // one directory given
      ListOfContents.beginningDirForRecursiveListing = this.parsedArgs.get(0);
    }
    // dont include directory being recursively listed as one of the contents
    for (String x : this) {
      if (!x.equals(ListOfContents.beginningDirForRecursiveListing)) {
        this.output +=
            getRelativePath(x, beginningDirForRecursiveListing) + "\n";
      }
    }
  }

  private void storeContentOfMultipleDirectories() {
    /*
     * Modifies output so it contains files and subdirectories of all
     * directory/files given
     */
    boolean directoryContainsElements = false;
    for (String arg : this.parsedArgs) {
      if (this.parsedArgs.size() > 1) {
        if (validateElement.validateDirectory(arg)) { // arg is a directory
          this.output += arg + ":" + "\n";
          if (FileSystem.getOuterMap().containsKey(arg)) {
            directoryContainsElements = true;
            for (String key : FileSystem.getOuterMap().get(arg).keySet())
              this.output += getRelativePath(key, arg) + " ";
          }
          if (directoryContainsElements)
            this.output += "\n";
        } else // arg is a file
          this.output += getRelativePath(arg, arg) + "\n";
        this.output += "\n";
      } else {
        if (FileSystem.getOuterMap().containsKey(arg)) { // arg is a directory
          for (String key : FileSystem.getOuterMap().get(arg).keySet())
            this.output += getRelativePath(key, arg) + "\n";
        } else { // file given
          this.output += getRelativePath(arg, arg);
          return;
        }
      }
    }
  }

  /**
   * Outputs or redirects contents of directory/files based on whether
   * redirection is invoked by args or not
   * 
   * @param args Argument whose contents will be displayed, if valid
   * @return Output to be printed or redirected
   */
  @Override
  public String run(String args) {
    this.output = ""; // empty this.output before running
    this.redirectOperator = "";
    this.redirectFilePath = "";
    this.recursiveListing = false;
    if (validateArgs(args)) {
      // store file/directory in current directory if no args given OR 2 args
      // given for redirection
      if (args.trim().equals("") || (!this.redirectOperator.equals("")
          && this.parsedArgs.size() == 0 && !recursiveListing))
        storeContentOfCurrentDirectory();
      // recursive listing if -R is first argument and no path of 1 path given
      else if (recursiveListing
          && (this.parsedArgs.size() == 0 || this.parsedArgs.size() == 1))
        storeContentOfDirectoryRecursively();
      // recursive listing if -R is first argument and multiple paths given
      else if (recursiveListing && this.parsedArgs.size() > 1)
        storeContentOfMultipleDirectoriesRecursively();
      else
        storeContentOfMultipleDirectories();
      // remove all trailing white lines
      this.output = this.output.replaceAll("([\\n\\r]+\\s*)*$", "");
      // handling redirection if there user invoked redirection
      if (!this.redirectOperator.isEmpty())
        redirectObj.redirect(this.output, redirectOperator, redirectFilePath);
      // printing to shell if no redirection and output is non empty
      else if (!this.output.isEmpty())
        printMessage.printString(this.output);
    }
    return this.output;
  }

  /**
   * Returns an iterator to search through the tree
   * 
   * @return A TreeIterator that iterates through the file system tree
   */
  @Override
  public Iterator<String> iterator() {
    return new TreeIterator(beginningDirForRecursiveListing);
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of ls command
   * 
   * @return formatted information about ls command
   */
  public String toString() {
    String command = "LS";
    String name = "\n\tls - list contents";
    String synopsis = "\n\tls [-R] [PATH ...] [>/>> OUTFILE]";
    String description =
        "\n\tGiven no arguments, lists the contents of the working directory, "
            + "each on a new line."
            + "\n\tGiven more than one path, for each path:"
            + "\n\tGiven a directory, print its full path, followed by a colon,"
            + " and its contents. " + "\n\tGiven a file, print its name."
            + "\n\tAdd a new line."
            + "\n\tIf -R is the first argument provided then print the "
            + "relative path of its contents recursively.";
    String errors =
        "\n\tThe first argument must be -R or an existing file and/or "
            + "directory paths."
            + "\n\tAll following arguments must be existing file and/or "
            + "directory paths. The last two arguments can"
            + "\n\tbe > or >> followed by a valid path to OUTFILE.";

    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors + redirectObj.toString();
  }
}
