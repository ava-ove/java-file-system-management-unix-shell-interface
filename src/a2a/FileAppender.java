package a2a;

/**
 * Represents FileAppender object for redirect and append, to be used for
 * appending output to valid files
 * 
 * @author Ananya Poddar
 */
public class FileAppender extends Command {

  private File oldFile;

  /**
   * Creates FileAppender object by setting default instance variable values
   * from Command constructor and initializing File object.
   * 
   * @param oldFile File to which content will be appended
   * @param b Builder object to construct FileAppender object
   */
  public FileAppender(File oldFile, Builder b) {
    super(b);
    this.oldFile = oldFile;
  }

  /**
   * Appends string to oldFile
   * 
   * @param toBeAdded String that will be appended
   * @return new content of file with appended string
   */
  @Override
  public String run(String toBeAdded) {
    String oldContent = oldFile.getContent();

    if (!oldContent.isEmpty())
      toBeAdded = "\n" + toBeAdded;

    String newContent = oldContent + toBeAdded;
    oldFile.setContent(newContent);
    return newContent;
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors for redirect and append
   * 
   * @return formatted information about redirect and append
   */
  public String toString() {
    String synopsis = "\n\t >> OUTFILE - append output to file";
    String description = "\n\tAppend output to the end of OUTFILE.";

    return "\nSYNOPSIS" + synopsis + "\nDESCRIPTION" + description;
  }

}
