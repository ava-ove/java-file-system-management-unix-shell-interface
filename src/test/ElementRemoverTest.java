package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Map;
import java.util.TreeMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import a2a.Command;
import a2a.Command.Builder;
import a2a.Directory;
import a2a.ElementRemover;
import a2a.File;
import a2a.FileSystem;
import a2a.FileSystemElement;
import driver.JShell;
import mockObjects.MockFileSystemModifier;

public class ElementRemoverTest {

  private Command elementRm;
  private static Builder build;
  private MockFileSystemModifier modifier;
  private static Map<String, TreeMap<String, FileSystemElement>> expectedMap;

  @Before
  public void setup() {
    modifier = new MockFileSystemModifier();
    modifier.addDir("/", "/Directory1");
    modifier.addDir("/Directory1", "/Directory2");
    modifier.addDir("/", "/Directory3");
    modifier.addFile("/Directory1/Directory2/File1");
    modifier.addFile("/File1");

    elementRm = new ElementRemover(build);
    expectedMap = new TreeMap<>();

    expectedMap.put("/", new TreeMap<>());
    expectedMap.put("/Directory1", new TreeMap<>());
    expectedMap.put("/Directory3", new TreeMap<>());
    expectedMap.put("/Directory1/Directory2", new TreeMap<>());

    expectedMap.get("/").put("/Directory1", new Directory("/Directory1"));
    expectedMap.get("/").put("/Directory3", new Directory("/Directory3"));
    expectedMap.get("/Directory1").put("/Directory1/Directory2",
        new Directory("/Directory1/Directory2"));
    expectedMap.get("/").put("/File1", new File("/File1", ""));
    expectedMap.get("/Directory1/Directory2").put("/File1",
        new File("/File1", ""));
  }

  @After
  public void tearDown() {
    JShell.setCurrentPath("/");
  }

  /*
   * The following tests are for the "validateArgs" method
   */
  @Test
  public void testValidateArgs1() {
    // rm requires an argument
    assertFalse(elementRm.validateArgs(" "));
  }

  @Test
  public void testValidateArgs2() {
    // rm only allows one argument
    assertFalse(elementRm.validateArgs("Directory1 Directory3"));
  }

  @Test
  public void testValidateArgs3() {
    /*
     * directory exists in file system and is not current or parent of current
     * path
     */
    assertTrue(elementRm.validateArgs("/Directory1"));
  }

  @Test
  public void testValidateArgs4() {
    // file exists in file system and can be removed
    assertTrue(elementRm.validateArgs("/File1"));
  }

  @Test
  public void testValidateArgs5() {
    // file exists nested inside some sub directory of root and it can be
    // removed
    assertTrue(elementRm.validateArgs("/Directory1/Directory2/File1"));
  }

  @Test
  public void testValidateArgs6() {
    // attempting to remove root directory which cannot be removed
    assertFalse(elementRm.validateArgs("/"));
  }

  @Test
  public void testValidateArgs7() {
    // working directory cannot be removed
    JShell.setCurrentPath("/Directory1");
    assertFalse(elementRm.validateArgs("/Directory1"));
  }

  /*
   * The following tests are for the "run" method and assume that the preceding
   * "validateArgs" method has run correctly
   */
  @Test
  public void testRun1() {
    // removing a directory that exists in the root
    elementRm.run("/Directory3");
    expectedMap.remove("/Directory3");
    expectedMap.get("/").remove("/Directory3");
    assertTrue(FileSystem.equals(expectedMap)); // use this
  }

  @Test
  public void testRun2() {
    // removing a directory nested inside a directory that exists in the root
    elementRm.run("/Directory1/Directory2");
    expectedMap.remove("/Directory1/Directory2");
    expectedMap.get("/Directory1").remove("/Directory1/Directory2");
    // should also remove /Directory1/Directory2 from outermap
    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testRun3() {
    // removing a file that exists in the root directory
    elementRm.run("/File1");
    expectedMap.get("/").remove("/File1");
    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testRun4() {
    // removing a file nested inside a directory that exists in the root
    elementRm.run("/Directory1/Directory2/File1");
    expectedMap.get("/Directory1/Directory2").remove("/File1");

    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testRun5() {
    // Directory2 is nested inside Directory1, so it should be removed from
    // outerMap as well
    elementRm.run("/Directory1");
    expectedMap.remove("/Directory1/Directory2");
    expectedMap.get("/Directory1").remove("/Directory1/Directory2");
    expectedMap.remove("/Directory1");
    expectedMap.get("/").remove("/Directory1");
    // should also remove /Directory1/Directory2 from outermap
    assertTrue(FileSystem.equals(expectedMap));
  }

}
