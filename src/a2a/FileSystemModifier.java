package a2a;

import java.util.TreeMap;
import interfaces.IFileSystemModifier;

/**
 * Represents a modifier object for FileSystem which adds files and directories.
 * 
 * @author Ava Oveisi
 */
public class FileSystemModifier
    implements IFileSystemModifier {

  private FullPathGiver pathGiver;
  private ValidFileSysElement validateElement;
  private ShellOutput printMessage;

  /**
   * Creates initial FileSystemModifier object and initializes collaborators
   */
  public FileSystemModifier() {
    pathGiver = new FullPathGiver();
    validateElement = new ValidFileSysElement();
    printMessage = new ShellOutput();
  }

  /**
   * Adds a directory with the name newDirName as value for inDir key in file
   * system TreeMap. Adds directory with name newDirName as key in file system
   * outerMap.
   * 
   * @param inDir Directory in which a new directory will be added
   * @param newDirName The new directory to be added
   */
  @Override
  public void addDir(String inDir, String newDirName) {
    TreeMap<String, FileSystemElement> newDir = new TreeMap<>();
    FileSystem.outerMap.put(newDirName, newDir);
    FileSystem.outerMap.get(inDir).put(newDirName, new Directory(newDirName));

  }

  /**
   * Adds file at given file path, if valid.
   * 
   * @param filePath name of file or full/relative path to file being created
   * @return newly created file or already existing file or null if fileName is
   *         same as existing directory name
   */
  @Override
  public File addFile(String filePath) {
    // store full path and file name
    String fullPath = pathGiver.getFullPath(filePath);
    // directory that the file is being added in
    File originalFile = validateElement.validateFile(fullPath);
    // cannot create file if invalid filePath given
    if (originalFile == null) {
      originalFile =
          addNewFile(fullPath.substring(fullPath.lastIndexOf("/")), fullPath);
    }
    return originalFile;
  }

  /**
   * Creates new file and adds it at given filePath, if valid.
   * 
   * @param fileName name of file to be added
   * @param fullPath full path to file being added
   * @return new file created, or null if it wasn't created
   */
  private File addNewFile(String fileName, String fullPath) {
    if (fileName.endsWith("/")
        || !validateElement.containsValidCharacters(fileName.substring(1)))
      return null;
    String inDir;
    // path leading to file
    if (fullPath.lastIndexOf("/") == 0)
      inDir = "/";
    else
      inDir = fullPath.substring(0, fullPath.lastIndexOf("/"));
    if (!validateElement.validateDirectory(inDir)) {
      printMessage.printInvalidDirectoryError();
      return null;
    }
    // filePath is already path to existing directory: file not added
    if (FileSystem.outerMap.get(inDir).containsKey(fullPath)
        && FileSystem.outerMap.get(inDir).get(fullPath) instanceof Directory) {

      printMessage.printExistingDirOrFileError();
      return null;
    }
    // file does not already exist or if it exists it is replaced by a new file
    if (FileSystem.outerMap.get(inDir).containsKey(fileName)
        && FileSystem.outerMap.get(inDir).get(fileName) instanceof File
        || !FileSystem.outerMap.get(inDir).containsKey(fileName)) {
      FileSystem.outerMap.get(inDir).put(fileName, new File(fileName, ""));
    }
    return validateElement.validateFile(fullPath);
  }

}
