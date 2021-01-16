package a2a;

/**
 * Represents ValidFileSystemElement object, ensuring valid file system element
 * names, and valid paths for existing directories and file paths
 * 
 * @author Ananya Poddar
 *
 */
public class ValidFileSysElement {

  private FullPathGiver pathGiver;
  private ShellOutput printMessage;

  /**
   * Creates ValidFileSysElement object, initializes ShellOutput and
   * FullPathGiver collaborators
   */
  public ValidFileSysElement() {
    pathGiver = new FullPathGiver();
    printMessage = new ShellOutput();
  }

  /**
   * Validates that new directory or file name contains only valid characters
   * 
   * @param dirOrFileName Name of new file or directory to be check for valid
   *        characters
   * @return true if file or directory contains only valid characters, false
   *         otherwise
   */
  public boolean containsValidCharacters(String dirOrFileName) {
    String[] invalidChars = {"/", ".", " ", "!", "@", "#", "$", "%", "^", "&",
        "*", "(", ")", "{", "}", "~", "|", "<", ">", "?"};

    for (int i = 0; i < invalidChars.length; i++) {
      if (dirOrFileName.contains(invalidChars[i])) {
        printMessage.printInvalidCharError();
        return false;
      }
    }
    return true;
  }

  /**
   * Validates that file object with given path exists in File System
   * 
   * @param filePath The path to the file to be validated as existing
   * @return File object given fileName if it exists, null otherwise
   */
  public File validateFile(String filePath) {

    /*
     * If the only "/" is at the first index, validate that file is a value for
     * root directory "/"
     */
    if (filePath.lastIndexOf("/") == 0) {
      if (FileSystem.getOuterMap().get("/").get(filePath) != null
          && FileSystem.getOuterMap().get("/").get(filePath) instanceof File)
        return (File) FileSystem.getOuterMap().get("/").get(filePath);
    }
    /*
     * Get path to directory until last "/" before file name, and check file
     * with that name is a value under that directory (ie it is a file in the
     * given directory path)
     */
    else {
      if ((FileSystem.getOuterMap()
          .get(filePath.substring(0, filePath.lastIndexOf("/")))) != null) {
        File innerFile = ((File) FileSystem.getOuterMap()
            .get(filePath.substring(0, filePath.lastIndexOf("/")))
            .get(filePath.substring(filePath.lastIndexOf("/"))));
        if (innerFile != null)
          return innerFile;
      }
    }
    return null;
  }

  /**
   * Validates that directory object with given path exists in File System
   * 
   * @param directoryPath The path to the directory to be validated as existing
   * @return True if directory exists, false otherwise
   */
  public boolean validateDirectory(String directoryPath) {
    if (FileSystem.getOuterMap()
        .containsKey(pathGiver.getFullPath(directoryPath)))
      return true;
    return false;
  }
}
