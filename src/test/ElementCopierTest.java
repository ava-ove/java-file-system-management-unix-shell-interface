package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Map;
import java.util.TreeMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import a2a.Command;
import a2a.Directory;
import a2a.ElementCopier;
import a2a.File;
import a2a.FileSystem;
import a2a.FileSystemElement;
import mockObjects.MockElementRemover;
import mockObjects.MockFileSystemModifier;

public class ElementCopierTest {

  private Command cp;
  private MockFileSystemModifier modifier;
  private MockElementRemover remover;
  private static Map<String, TreeMap<String, FileSystemElement>> expectedMap;
  private File F1;

  @Before
  public void setUp() {
    modifier = new MockFileSystemModifier();
    remover = new MockElementRemover();

    modifier.addDir("/", "/Directory1");
    modifier.addDir("/Directory1", "/Directory2");
    modifier.addDir("/", "/Directory3");
    modifier.addDir("/", "/Directory4");
    modifier.addFile("/Directory1/Directory2/File1");
    modifier.addFile("/File1");
    F1 = (File) modifier.getNewOuterMap().get("/").get("/File1");
    F1.setContent("This will be copied.");
    cp = new ElementCopier.Builder().fileSystem(modifier)
        .elementRemover(remover).build();


    expectedMap = new TreeMap<>();
    expectedMap.put("/", new TreeMap<>());
    expectedMap.put("/Directory1", new TreeMap<>());
    expectedMap.put("/Directory3", new TreeMap<>());
    expectedMap.put("/Directory4", new TreeMap<>());
    expectedMap.get("/").put("/Directory1", new Directory("/Directory1"));
    expectedMap.get("/").put("/Directory3", new Directory("/Directory3"));
    expectedMap.get("/").put("/Directory4", new Directory("/Directory4"));
    expectedMap.put("/Directory1/Directory2", new TreeMap<>());
    expectedMap.get("/Directory1").put("/Directory1/Directory2",
        new Directory("/Directory1/Directory2"));
    expectedMap.get("/").put("/File1",
        new File("/File1", "This will be copied."));
    expectedMap.get("/Directory1/Directory2").put("/File1",
        new File("/File1", ""));

  }

  @After
  public void tearDown() {
    modifier.clear();
  }

  @Test
  public void testValidateArgs1() {
    // test no args
    assertFalse(cp.validateArgs(""));
  }

  @Test
  public void testValidateArgs2() {
    // test one arg
    assertFalse(cp.validateArgs("oneArg"));
  }

  @Test
  public void testValidateArgs3() {
    // test more than 2 args
    assertFalse(cp.validateArgs("Many many many arguments"));
  }

  @Test
  public void testValidateArgs4() {
    // test two args where argument 1 is invalid because File3 is not in the
    // file system and so can't be copied
    assertFalse(cp.validateArgs("File3 File2"));
  }

  @Test
  public void testValidateArgs5() {
    // test two args where both are valid
    // File1 is already in file system
    assertTrue(cp.validateArgs("File1 File2"));
  }

  /*
   * The following tests are for the "run" method and assume that the preceding
   * "validateNumArgs" and "validateArgs" methods have run correctly
   */

  @Test
  public void testRun1() {
    // Copy file to a file that does not yet exist; must first create the file
    F1.setContent("This is File 1 in root.");
    cp.run("File1 File2");

    expectedMap.get("/").put("/File1",
        new File("/File1", "This is File 1 in root."));
    expectedMap.get("/").put("/File2",
        new File("/File2", "This is File 1 in root."));
    File F2 = (File) modifier.getNewOuterMap().get("/").get("/File2");

    // checks the actual and expected maps are equal
    // checks that File1's content was correctly copied to File2
    assertTrue(FileSystem.equals(expectedMap));
    assertEquals(F1.getContent(), F2.getContent());

  }

  @Test
  public void testRun2() {
    // Copy file to a file named /File2 which already exists with content inside
    // must overwrite contents of /File2
    modifier.addFile("/File2");
    File F2 = (File) modifier.getNewOuterMap().get("/").get("/File2");
    F2.setContent("This is File 1 in root.");

    // runs copy and checks that the content is updated
    cp.run("File1 /File2");
    expectedMap.get("/").put("/File1",
        new File("/File1", "This will be copied."));
    expectedMap.get("/").put("/File2",
        new File("/File2", "This will be copied."));
    F2 = (File) modifier.getNewOuterMap().get("/").get("/File2");
    assertTrue(FileSystem.equals(expectedMap));
    assertEquals(F1.getContent(), F2.getContent());
  }

  @Test
  public void testRun3() {
    // copies file to existing directory
    // adds /Directory2 to which existing /File1 will be copied
    modifier.addDir("/", "/Directory2");
    cp.run("/File1 /Directory2");

    expectedMap.put("/Directory2", new TreeMap<>());
    expectedMap.get("/").put("/Directory2", new Directory("/Directory2"));
    expectedMap.get("/").put("/File1",
        new File("/File1", "This will be copied."));
    expectedMap.get("/Directory2").put("/File1",
        new File("/File1", "This will be copied."));
    File D2F1 =
        (File) modifier.getNewOuterMap().get("/Directory2").get("/File1");

    assertTrue(FileSystem.equals(expectedMap));
    assertEquals(F1.getContent(), D2F1.getContent());
  }


  @Test
  public void testRun4() {
    // copies file to existing directory and renames the file
    // adds /Directory2 to which existing /File1 will be copied
    modifier.addDir("/", "/Directory2");
    cp.run("/File1 /Directory2/File2");

    expectedMap.put("/Directory2", new TreeMap<>());
    expectedMap.get("/").put("/Directory2", new Directory("/Directory2"));
    expectedMap.get("/").put("/File1",
        new File("/File1", "This will be copied."));
    expectedMap.get("/Directory2").put("/File2",
        new File("/File2", "This will be copied."));
    File D2F2 =
        (File) modifier.getNewOuterMap().get("/Directory2").get("/File2");
    assertTrue(FileSystem.equals(expectedMap));
    assertEquals(F1.getContent(), D2F2.getContent());
  }


  @Test
  public void testRun5() {
    // test trying to copy a directory to an existing file
    // nothing should be changed
    cp.run("/Directory1 /File1");
    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testRun6() {
    // copying a directory to another directory
    // should copy Directory3 and place it in Directory4
    cp.run("/Directory3 /Directory4");

    expectedMap.put("/Directory4/Directory3", new TreeMap<>());
    expectedMap.get("/Directory4").put("/Directory4/Directory3",
        new Directory("/Directory4/Directory3"));

    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testRun7() {
    // copying a directory to a directory that doesn't yet exist should copy
    // Directory3 and place it in root and rename it Directory5
    cp.run("/Directory3 /Directory5");

    expectedMap.put("/Directory5", new TreeMap<>());
    expectedMap.get("/").put("/Directory5", new Directory("/Directory5"));

    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testRun8() {
    /*
     * copying a directory to another directory which contains a directory of
     * the same name should copy /Directory2 and replace the Directory2 in
     * Directory1
     */
    modifier.addDir("/", "/Directory2");
    expectedMap.put("/Directory2", new TreeMap<>());
    expectedMap.get("/").put("/Directory2", new Directory("/Directory2"));

    cp.run("/Directory2 /Directory1");

    expectedMap.get("/Directory1/Directory2").remove("/File1");
    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testRun9() {
    /*
     * copying a nested directory and placing it in the root should copy
     * Directory2 in Directory1 and place it in root new /Directory2 should have
     * a file in it still
     */
    cp.run("/Directory1/Directory2 /");

    expectedMap.put("/Directory2", new TreeMap<>());
    expectedMap.get("/").put("/Directory2", new Directory("/Directory2"));
    expectedMap.get("/Directory2").put("/File1", new File("/File1", ""));

    assertTrue(FileSystem.equals(expectedMap));
  }

}
