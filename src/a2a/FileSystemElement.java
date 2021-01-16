package a2a;

import java.io.Serializable;

/**
 * Represents elements in the file system. Contains the element's name.
 * 
 * @author Fariha Fyrooz
 */
public class FileSystemElement implements Serializable {
  /**
   * name of FileSystemElement
   */
  protected String name;

  /**
   * Creates new FileSystemElement object, sets its name
   */
  public FileSystemElement(String name) {
    this.name = name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
