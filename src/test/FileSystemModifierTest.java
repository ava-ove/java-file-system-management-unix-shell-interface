package test;

import static org.junit.Assert.assertTrue;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import a2a.Directory;
import a2a.File;
import a2a.FileSystem;
import a2a.FileSystemElement;
import a2a.FileSystemModifier;

public class FileSystemModifierTest {

  private FileSystemModifier modifier;
  private static Map<String, TreeMap<String, FileSystemElement>> expectedMap;

  /*
   * The following tests modify the actual FileSystem object (and not a mock)
   * and collaborate with its getter as all fields and methods in the FileSystem
   * class are static. As the FileSystem class only creates an instance of
   * FileSystem, holds and returns the map, these tests run under the assumption
   * that all of this is working as desired.
   */
  @Before
  public void setup() {
    // creates new FileSystem, with root directory "/"
    FileSystem.createFileSystemInstance();
    modifier = new FileSystemModifier();

    // adds the root directory to expectedMap
    expectedMap = new TreeMap<>();
    expectedMap.put("/", new TreeMap<>());
  }

  @After
  public void tearDown() {
    try {
      Field singleReference =
          FileSystem.class.getDeclaredField("singleReference");
      singleReference.setAccessible(true);
      singleReference.set(null, null);
    } catch (NoSuchFieldException | SecurityException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  /*
   * The following tests test the "addDir" method and work under the assumption
   * that the directory names and paths have been validated in the classes in
   * charge of directory creating (only mkdir)
   */
  @Test
  public void testAddDirToRoot() {
    modifier.addDir("/", "/Directory1");
    // adds /Directory1 to expectedMap outermap, then as a value for root
    expectedMap.put("/Directory1", new TreeMap<>());
    expectedMap.get("/").put("/Directory1", new Directory("/Directory1"));
    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testAddSecondDirToRoot() {
    modifier.addDir("/", "/Directory1");
    modifier.addDir("/", "/Element1");
    // adds /Directory1 and /Element1 to expectedMap outermap, then as values
    // for root
    expectedMap.put("/Directory1", new TreeMap<>());
    expectedMap.put("/Element1", new TreeMap<>());
    expectedMap.get("/").put("/Directory1", new Directory("/Directory1"));
    expectedMap.get("/").put("/Element1", new Directory("/Element1"));
    assertTrue(FileSystem.equals(expectedMap));;
  }

  @Test
  public void testAddSubdir() {
    modifier.addDir("/", "/Directory1");
    modifier.addDir("/Directory1", "/Directory1/Directory2");

    expectedMap.put("/Directory1", new TreeMap<>());
    expectedMap.put("/Directory1/Directory2", new TreeMap<>());
    expectedMap.get("/").put("/Directory1", new Directory("/Directory1"));
    expectedMap.get("/Directory1").put("/Directory1/Directory2",
        new Directory("/Directory1/Directory2"));
    assertTrue(FileSystem.equals(expectedMap));
  }

  /*
   * The following tests are for the method "addFile" and validation of a valid
   * file reference is done within this method as multiple classes must create
   * files. For files created in subdirectories, these tests work under the
   * assumption that "addDir" method is working correctly.
   */

  @Test
  public void testAddValidNewFileToRoot() {
    modifier.addFile("/File1");
    // files aren't added to the outer map of fileSystem
    expectedMap.get("/").put("/File1", new File("/File1", ""));
    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testAddValidNewFileToDifferentDir() {
    modifier.addDir("/", "/Directory1");
    modifier.addFile("/Directory1/File1");

    expectedMap.put("/Directory1", new TreeMap<>());
    expectedMap.get("/").put("/Directory1", new Directory("/Directory1"));
    expectedMap.get("/Directory1").put("/File1", new File("/File1", ""));

    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testAddValidFileToNestedDir() {
    modifier.addDir("/", "/Directory1");
    modifier.addFile("/Directory1/File1");
    modifier.addDir("/Directory1", "/Directory1/Directory2");
    modifier.addFile("/Directory1/Directory2/File1");

    expectedMap.put("/Directory1", new TreeMap<>());
    expectedMap.get("/").put("/Directory1", new Directory("/Directory1"));
    expectedMap.get("/Directory1").put("/File1", new File("/File1", ""));
    expectedMap.get("/Directory1").put("/Directory1/Directory2",
        new Directory("/Directory1/Directory2"));
    expectedMap.put("/Directory1/Directory2", new TreeMap<>());
    expectedMap.get("/Directory1/Directory2").put("/File1",
        new File("/File1", ""));

    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testAddFileWithSamePathAsDir() {
    modifier.addDir("/", "/Element1");
    modifier.addFile("/Element1");
    // as the directory should not be overriden by the file, the directory
    // should be in the outermap
    expectedMap.put("/Element1", new TreeMap<>());
    expectedMap.get("/").put("/Element1", new Directory("/Element1"));
    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testAddFileWithInvalidChar1() {
    modifier.addFile("/File$1");
    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testAddFileWithInvalidChar2() {
    modifier.addFile("/File1.txt");
    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testAddFileInsideFile() {
    modifier.addFile("/File1");
    // cannot add File inside File, should not be added to outermap
    modifier.addFile("/File1/File2");
    expectedMap.get("/").put("/File1", new File("/File1", ""));
    assertTrue(FileSystem.equals(expectedMap));
  }

}
