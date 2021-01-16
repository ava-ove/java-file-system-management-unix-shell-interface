package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Map;
import java.util.TreeMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import a2a.Command;
import a2a.Directory;
import a2a.DirectoryMaker;
import a2a.File;
import a2a.FileSystem;
import a2a.FileSystemElement;
import driver.JShell;
import mockObjects.MockFileSystemModifier;

public class DirectoryMakerTest {

  private Command mkdir;
  private MockFileSystemModifier modifier;
  private static Map<String, TreeMap<String, FileSystemElement>> expectedMap;

  @Before
  public void setUp() {
    modifier = new MockFileSystemModifier();
    JShell.setCurrentPath("/");
    modifier.addFile("/File1");
    mkdir = new DirectoryMaker.Builder().fileSystem(modifier).build();
    expectedMap = new TreeMap<>();
    expectedMap.put("/", new TreeMap<>());
    expectedMap.get("/").put("/File1", new File("/File1", ""));
  }


  @After
  public void tearDown() {
    modifier.clear();
  }

  @Test
  public void testValidateNumArgs1() {
    // test no args
    assertFalse(((DirectoryMaker) mkdir).validateNumArgs(""));
  }

  @Test
  public void testValidateNumArgs2() {
    // tests one arg
    assertTrue(((DirectoryMaker) mkdir).validateNumArgs("File1"));
  }

  @Test
  public void testValidateNumArgs3() {
    // tests 2 args
    assertTrue(((DirectoryMaker) mkdir).validateNumArgs("File1 File2"));
  }

  @Test
  public void testValidateArgs1() {
    // tests existing dir
    assertFalse(mkdir.validateArgs("/"));
  }

  @Test
  public void testValidateArgs2() {
    // tests existing file
    assertFalse(mkdir.validateArgs("/File1"));
  }

  @Test
  public void testValidateArgs3() {
    // tests valid dir that doesn't yet exist
    assertTrue(mkdir.validateArgs("/Directory112"));
  }

  @Test
  public void testValidateArgs4() {
    // test dir containin invalid characters
    assertFalse(mkdir.validateArgs("/Directory!!1"));
  }


  /*
   * The following tests are for the "run" method and assume that the preceding
   * "validateNumArgs" and "validateArgs" methods have run correctly
   */
  @Test
  public void testRun1() {
    // tests adding one valid directory
    mkdir.run("/Directory1");
    expectedMap.put("/Directory1", new TreeMap<>());
    expectedMap.get("/").put("/Directory1", new Directory("/Directory1"));
    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testRun2() {
    // tests adding multiple valid directories
    // adds /Directory1 to the map before calling mkdir
    modifier.addDir("/", "/Directory1");
    mkdir.run("Directory1/Directory2 Directory3");

    expectedMap.put("/Directory1", new TreeMap<>());
    expectedMap.get("/").put("/Directory1", new Directory("/Directory1"));
    expectedMap.put("/Directory3", new TreeMap<>());
    expectedMap.get("/").put("/Directory3", new Directory("/Directory3"));
    expectedMap.put("/Directory1/Directory2", new TreeMap<>());
    expectedMap.get("/Directory1").put("/Directory1/Directory2",
        new Directory("/Directory1/Directory2"));

    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testRun3() {
    // tests adding multiple valid directories given relative paths
    // adds some directories to the map before calling mkdir
    modifier.addDir("/", "/Directory1");
    modifier.addDir("/Directory1", "/Directory2");
    modifier.addDir("/", "/Directory3");

    JShell.setCurrentPath("/Directory1");
    mkdir.run("../Directory4 ./../Directory1/Directory5");

    expectedMap.put("/Directory1", new TreeMap<>());
    expectedMap.get("/").put("/Directory1", new Directory("/Directory1"));
    expectedMap.put("/Directory3", new TreeMap<>());
    expectedMap.get("/").put("/Directory3", new Directory("/Directory3"));
    expectedMap.put("/Directory4", new TreeMap<>());
    expectedMap.get("/").put("/Directory4", new Directory("/Directory4"));
    expectedMap.put("/Directory1/Directory2", new TreeMap<>());
    expectedMap.get("/Directory1").put("/Directory1/Directory2",
        new Directory("/Directory1/Directory2"));
    expectedMap.put("/Directory1/Directory5", new TreeMap<>());
    expectedMap.get("/Directory1").put("/Directory1/Directory5",
        new Directory("/Directory1/Directory5"));
    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testRun4() {
    // tests multiple directories arg given at least one invalid path
    // adds some directories to the map before calling mkdir
    modifier.addDir("/", "/Directory1");
    modifier.addDir("/Directory1", "/Directory2");
    modifier.addDir("/Directory1", "/Directory5");
    expectedMap.put("/Directory1", new TreeMap<>());
    expectedMap.get("/").put("/Directory1", new Directory("/Directory1"));
    expectedMap.put("/Directory1/Directory2", new TreeMap<>());
    expectedMap.get("/Directory1").put("/Directory1/Directory2",
        new Directory("/Directory1/Directory2"));
    expectedMap.put("/Directory1/Directory5", new TreeMap<>());
    expectedMap.get("/Directory1").put("/Directory1/Directory5",
        new Directory("/Directory1/Directory5"));

    mkdir.run("Directory1/Directory2/Directory6 Directory#3");
    // nothing should change in file system

    assertTrue(FileSystem.equals(expectedMap));
  }



}
