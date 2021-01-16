package a2a;

import java.util.Iterator;
import java.util.Stack;

/**
 * Represents tree command. Displays list of all files and directories currently
 * stored in file system in tree form.
 * 
 * @author Ava Oveisi
 */
public class Tree extends Command implements Iterable<String> {
  private Redirection redirectOutput;
  private String redirectOperator;
  private String redirectFilePath;
  private String output;

  /**
   * Creates initial Tree object by setting default instance variable values
   * 
   * @param b Builder object to construct Tree object
   */
  public Tree(Builder b) {
    super(b);
    redirectOutput = new Redirection(b);
    redirectOperator = "";
    redirectFilePath = "";
    output = "";
  }

  /**
   * Returns an iterator to search through the tree
   * 
   * @return A TreeTraversalIterator that iterates through the file system tree
   */
  @Override
  public Iterator<String> iterator() {
    return new TreeTraversalIterator();
  }

  /**
   * Represents iterator for entire File System.
   * 
   * @author Ava Oveisi
   */
  private static class TreeTraversalIterator implements Iterator<String> {
    /**
     * Stack that keeps track of all items to be iterated over
     */
    Stack<String> stk;

    /**
     * Pushes / to stack which is from where recursive listing is being started,
     * initializes stack.
     */
    public TreeTraversalIterator() {
      stk = new Stack<>();
      stk.push("/"); // push root directory on stack
    }

    /**
     * Returns name of directory/file when given full path to it or returned /
     * if / given.
     * 
     * @param fullPath full path to file/directory
     * @return name of directory/file
     */
    private String getFileOrDirFromFullPath(String fullPath) {
      if (!fullPath.equals("/"))
        return fullPath.substring(fullPath.lastIndexOf("/") + 1);
      return fullPath;
    }

    /**
     * Returns true is stack is not empty meaning more elements are left to be
     * iterated. Returns false otherwise.
     */
    @Override
    public boolean hasNext() {
      return (!stk.isEmpty());
    }

    /**
     * Returns the top most item of stack if one exists with appropiate spaces
     * which creates the tree form, pushes all values contained in that top most
     * key in the stack to be iterated over.Returns null if stack is empty.
     */
    @Override
    public String next() {
      if (hasNext()) {
        // get top most item of stack
        String stktop = stk.pop();
        // number of leading spaces represents depth
        int depth = stktop.indexOf(stktop.trim());
        // number of leading spaces for netx item is one more than depth
        String oldLeadSpaces = "";
        String newLeadSpaces = "";
        for (int i = 0; i < depth; i++)
          oldLeadSpaces = oldLeadSpaces + "  ";
        for (int i = 0; i < depth + 1; i++)
          newLeadSpaces = newLeadSpaces + " ";

        // if stktop is a directory add its items to stack
        if (FileSystem.getOuterMap().containsKey(stktop.trim())) {
          for (String e : FileSystem.getOuterMap().get(stktop.trim())
              .keySet()) {
            stk.push(newLeadSpaces + e);
          }
        }
        // return top most item on stack
        return oldLeadSpaces + getFileOrDirFromFullPath(stktop);
      }
      return null;
    }
  }

  /**
   * Returns true if no arguments given or if 2 argument given which are
   * potentially for redirection
   * 
   * @param args command arguments
   * @return true if args is a valid argument, false otherwise
   */
  @Override
  public boolean validateArgs(String args) {
    if (args.trim().isEmpty())
      return true;
    if (args.trim().split("\\s+").length == 2) {
      // store redirection operator
      // !!!consider putting these in another function??
      redirectOperator = args.trim().split("\\s+")[0];
      redirectFilePath = args.trim().split("\\s+")[1];
      return true;
    }
    printMessage.printNumArgsError();
    return false;
  }

  /**
   * Prints or redirects tree based on whether redirection is invoked by args or
   * not
   * 
   * @param args Argument whose contents will be displayed, if valid
   * @return Output to be printed or redirected
   */
  @Override
  public String run(String args) {
    this.output = "";
    if (!validateArgs(args))
      return "";
    
    for (String x : this) {
      this.output += x + "\n";
    }
    // remove ending \n
    if (this.output.length() > 0)
      this.output = this.output.substring(0, this.output.length() - 1);
    // handling redirection if args is no empty
    if (!args.isEmpty()) {
      redirectOutput.redirect(this.output, this.redirectOperator,
          this.redirectFilePath);
    }
    // if no args given print to shell
    else {
      printMessage.printString(this.output);
    }
    return this.output;
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of tree command
   * 
   * @return formatted information about tree command
   */
  @Override
  public String toString() {
    String command = "TREE";
    String name = "\n\ttree - print file system tree";
    String synopsis = "\n\ttree [>/>> OUTFILE]";
    String description = "\n\tDisplay file system as a tree.";
    String errors = "\n\tNo arguments allowed (unless for redirection).";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors + redirectOutput.toString();
  }

}
