package interfaces;

import java.io.File;

public interface IFileFetcher {

  /**
   * Returns a file object at specified file path, if valid
   * 
   * @param filePath the path at which the file is to be created
   * @return File object if valid, null otherwise
   */
  public File getFile(String filePath);

  /**
   * Returns the content in the file, if it is a valid existing file
   * 
   * @param retrievedFile the file whose content will be retrieved
   * @return Object of contents from file, null if file does not exist
   */
  public Object getFileContent(File retrievedFile);
}
