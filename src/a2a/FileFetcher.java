package a2a;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.InvalidPathException;
import interfaces.IFileFetcher;

/**
 * Represents FileFetcher object, to get a file and its content from local file
 * system
 * 
 * @author Ananya Poddar
 *
 */
public class FileFetcher implements IFileFetcher {

  private ShellOutput printMessage;

  /**
   * Creates FileFetcher object, and initializes ShellOutput collaborator
   */
  public FileFetcher() {
    printMessage = new ShellOutput();
  }

  /**
   * Returns a File object at specified file path, if valid
   * 
   * @param filePath the path at which the file is to be created
   * @return File object if valid, null otherwise
   */
  public File getFile(String filePath) {
    File potentialFile;
    if (filePath.contains("."))
      potentialFile =
          new File(filePath.substring(0, filePath.lastIndexOf(".")) + ".ser");
    else
      potentialFile = new File(filePath + ".ser");
    try {
      // ensure that parent (ie without filename) is valid
      File parent = potentialFile.getParentFile();
      if (parent == null) {
        printMessage.printInvalidFilePath();
        return null;
      }
    } catch (InvalidPathException ipe) {
      printMessage.printInvalidFilePath();
      return null;
    } catch (Exception e) {
      return null;
    }
    try {
      // ensure that file contains valid name
      potentialFile.getCanonicalFile();
    } catch (IOException ioe) {
      printMessage.printInvalidCharError();
      return null;
    }
    return potentialFile;
  }

  /**
   * Returns the content in the file, if it is a valid existing file
   * 
   * @param retrievedFile the file whose content will be retrieved
   * @return Object of contents from file, null if file does not exist
   */
  // @Override
  public Object getFileContent(File retrievedFile) {
    try {
      if (retrievedFile.exists()) {
        ObjectInputStream ois =
            new ObjectInputStream(new FileInputStream(retrievedFile));
        Object readObj = ois.readObject();
        ois.close();
        return readObj;
      }
      return null;
    } catch (IOException e) {
      printMessage.printInvalidCharError();
    } catch (ClassNotFoundException e) {
      printMessage.printInvalidClassError();
    }
    return null;
  }
}
