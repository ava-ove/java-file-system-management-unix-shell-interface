package mockObjects;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import a2a.Directory;
import a2a.File;
import a2a.FileSystem;
import a2a.FileSystemElement;
import a2a.TreeIterator;
import interfaces.IFileSystemModifier;

/**
 * Represents a mock modifier object for FileSystem which adds files and
 * directories only in specific cases
 * 
 * @author Ananya Poddar
 * @author Fariha Fyrooz
 */
public class MockFileSystemModifier
    implements IFileSystemModifier, Iterable<String> {

  private Map<String, TreeMap<String, FileSystemElement>> newOuterMap;

  /**
   * Creates MockFileSystemModifier object by creating FileSystem instance and
   * setting the new outer map to be equal to file system's initial outer map
   */
  public MockFileSystemModifier() {
    FileSystem.createFileSystemInstance();
    newOuterMap = FileSystem.getOuterMap();
  }

  /**
   * Adds directory to outerMap for the following cases:
   * 
   * 1) the directory being added is /Directory1 to /Directory5 or /Element1 and
   * is being added to the root dir
   * 
   * 2) the directory being added is /Directory2 to /Directory5 and it is being
   * added as a subdirectory of /Directory1
   * 
   * 3) the directory being added is /Directory3 and it is being added as a
   * subdirectory of /Directory4
   * 
   * @param inDir the directory inside which newDir will be added
   * @param newDir the directory to be added
   */
  @Override
  public void addDir(String inDir, String newDir) {
    if (inDir.equals("/")
        && (newDir.equals("/Directory1") || newDir.equals("/Element1")
            || newDir.equals("/Directory2") || newDir.equals("/Directory3")
            || newDir.equals("/Directory4") || newDir.equals("/Directory5"))) {
      newOuterMap.put(newDir, new TreeMap<String, FileSystemElement>());
      newOuterMap.get("/").put(newDir, new Directory(newDir));
    }
    if (inDir.equals("/Directory1") && newOuterMap.containsKey("/Directory1")
        && ((newDir.equals("/Directory2") || newDir.equals("/Directory5")
            || newDir.equals("/Directory1/Directory2")
            || newDir.equals("/Directory1/Directory5")
            || newDir.equals("/Directory6")
            || newDir.equals("/Directory1/Directory6")))) {
      newDir = newDir.substring(newDir.lastIndexOf("/"));
      newOuterMap.put("/Directory1" + newDir,
          new TreeMap<String, FileSystemElement>());
      newOuterMap.get("/Directory1").put("/Directory1" + newDir,
          new Directory("/Directory1" + newDir));
    }
    if (inDir.equals("/Directory4") && newOuterMap.containsKey("/Directory4")
        && (newDir.equals("/Directory3")
            || newDir.equals("/Directory4/Directory3"))) {
      newOuterMap.put("/Directory4/Directory3",
          new TreeMap<String, FileSystemElement>());
      newOuterMap.get("/Directory4").put("/Directory4/Directory3",
          new Directory(newDir));
    }
    setOuterMap();
  }

  private void setOuterMap() {
    try {
      Field outerMap = FileSystem.class.getDeclaredField("outerMap");
      outerMap.setAccessible(true);
      outerMap.set(null, newOuterMap);
    } catch (NoSuchFieldException | SecurityException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

  }

  /**
   * Adds file to outerMap for the following cases:
   * 
   * 1) the file being added is /File, /File2, /iso_8859-1, or /Element1 and is
   * being added to the root dir
   * 
   * 2) the file being added is /iso_8859-1 or /File1 and are being added as a
   * file inside /Directory1
   * 
   * 3) the file being added is /File1 or /File2 and it is being added inside
   * /Directory2
   *
   * 4) the file being added is /File1 and it is being added inside
   * /Directory1/Directory2
   * 
   * @param filePath The path where the file will be added
   */
  @Override
  public File addFile(String filePath) {
    if ((filePath.equals("/File1") || filePath.equals("/Element1")
        || filePath.equals("/iso_8859-1") || filePath.equals("/File2"))
        && !newOuterMap.containsKey(filePath)) {
      File newFile = new File(filePath, "");
      newOuterMap.get("/").put(filePath, newFile);
      return newFile;
    }
    if ((filePath.equals("/Directory1/iso_8859-1")
        || filePath.equals("/Directory1/File1"))
        && newOuterMap.containsKey("/Directory1")) {
      File newFl = new File(filePath.substring(filePath.lastIndexOf("/")), "");
      newOuterMap.get("/Directory1")
          .put(filePath.substring(filePath.lastIndexOf("/")), newFl);
      return newFl;
    }
    if ((filePath.equals("/Directory2/File1")
        || filePath.equals("/Directory2/File2"))
        && newOuterMap.containsKey("/Directory2")) {
      File newFile = new File("/File1", "");
      newOuterMap.get("/Directory2")
          .put(filePath.substring(filePath.lastIndexOf("/")), newFile);
      return newFile;
    }
    if (filePath.equals("/Directory1/Directory2/File1")
        && newOuterMap.containsKey("/Directory1/Directory2")) {
      File newFile = new File("/File1", "");
      newOuterMap.get("/Directory1/Directory2").put("/File1", newFile);
    }
    setOuterMap();
    return null;
  }

  public Map<String, TreeMap<String, FileSystemElement>> getNewOuterMap() {
    return newOuterMap;
  }

  /**
   * Clears the new outer map, and only keeps the root directory "/"
   */
  public void clear() {
    newOuterMap.clear();
    newOuterMap.put("/", new TreeMap<String, FileSystemElement>());
  }

  @Override
  public Iterator<String> iterator() {
    return new TreeIterator("/");
  }
}
