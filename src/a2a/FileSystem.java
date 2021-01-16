package a2a;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents the file system where all directories and files are contained.
 * 
 * @author Fariha Fyrooz
 *
 */
public class FileSystem {

  private static FileSystem singleReference = null;

  /**
   * Contains all file system elements
   */
  protected static Map<String, TreeMap<String, FileSystemElement>> outerMap;

  private FileSystem(String rootDir) {
    FileSystem.outerMap = new TreeMap<>();
    outerMap.put(rootDir, new TreeMap<String, FileSystemElement>());
  }

  /**
   * Creates new FileSystem object or returns the existing one
   * 
   * @return FileSystem object
   */
  public static FileSystem createFileSystemInstance() {
    if (singleReference == null)
      singleReference = new FileSystem("/");
    return singleReference;
  }

  public static Map<String, TreeMap<String, FileSystemElement>> getOuterMap() {
    return outerMap;
  }

  /**
   * Returns true of the given maps are equal (if all file system element names,
   * types and content are equal). Otherwise returns false.
   * 
   * @param mapToCheck map to be compared with
   * @return true if maps are equal, false otherwise
   */
  public static boolean equals(
      Map<String, TreeMap<String, FileSystemElement>> mapToCheck) {
    TreeIterator mapToCheckIterator = new TreeIterator(mapToCheck, "/");
    TreeIterator outerMapIterator = new TreeIterator(outerMap, "/");
    LinkedList<String> actual = new LinkedList<>();
    LinkedList<String> expected = new LinkedList<>();
    while (outerMapIterator.hasNext()) {
      String fse = outerMapIterator.next();
      if (outerMap.containsKey(fse))
        actual.add(fse + "+dir");
      else {
        String parent = fse.substring(0, Math.max(1, fse.lastIndexOf("/")));
        String fName = fse.substring(fse.lastIndexOf("/"));
        String content = ((File) outerMap.get(parent).get(fName)).getContent();
        actual.add(fse + "+file" + content);
      }
    }
    while (mapToCheckIterator.hasNext()) {
      String expElem = mapToCheckIterator.next();
      if (mapToCheck.containsKey(expElem))
        expected.add(expElem + "+dir");
      else {
        String parent =
            expElem.substring(0, Math.max(1, expElem.lastIndexOf("/")));
        String fName = expElem.substring(expElem.lastIndexOf("/"));
        String content =
            ((File) mapToCheck.get(parent).get(fName)).getContent();
        expected.add(expElem + "+file" + content);
      }
    }
    return expected.equals(actual);
  }

}
