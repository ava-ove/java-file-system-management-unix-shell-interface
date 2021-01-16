package a2a;

/**
 * Represents FileOverwriter object for redirect and overwrite, to be used for
 * overwriting content of valid files
 * 
 * @author Ananya Poddar
 */
public class FileOverwriter extends Command {

  private FileAppender appendFile;
  private File oldFile;

  /**
   * Creates FileOverwriter object by setting default instance variable values
   * from Command constructor and initializing collaborators FileAppender and
   * File objects
   * 
   * @param oldFile File whose content will be overwritten
   * @param b Builder object to construct FileOverwriter object
   */
  public FileOverwriter(File oldFile, Builder b) {
    super(b);
    appendFile = new FileAppender(oldFile, b);
    this.oldFile = oldFile;
  }

  /**
   * Overwrites contents of oldFile with newContent
   * 
   * @param newContent content that will replace content of oldFile
   * @return new content of file with overwritten string
   */
  @Override
  public String run(String newContent) {
    oldFile.setContent("");
    return appendFile.run(newContent);
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors about redirect and overwrite
   * 
   * @return formatted information about redirect and overwrite
   */
  public String toString() {
    String synopsis = "\n\t > OUTFILE - overwrite contents of file with output";
    String description =
        "\n\tOverwrite contents of OUTFILE with STRING if OUTFILE " + "exists.";
    return "\nSYNOPSIS" + synopsis + "\nDESCRIPTION" + description;
  }

}
