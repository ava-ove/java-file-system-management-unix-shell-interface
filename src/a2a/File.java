package a2a;

/**
 * Represents File elements in the file system. File contains its name and
 * content.
 * 
 * @author Fariha Fyrooz
 *
 */
public class File extends FileSystemElement {

  private String content;

  /**
   * Creates file object, sets name and content of the file object
   * 
   * @param name The name of the file
   * @param content The content of the file
   */
  public File(String name, String content) {
    super(name);
    this.content = content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }

}
