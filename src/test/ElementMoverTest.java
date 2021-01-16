package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Map;
import java.util.TreeMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import a2a.Command;
import a2a.Directory;
import a2a.ElementCopier;
import a2a.ElementMover;
import a2a.File;
import a2a.FileSystem;
import a2a.FileSystemElement;
import interfaces.IElementCopier;
import interfaces.IElementRemover;
import mockObjects.MockElementRemover;
import mockObjects.MockFileSystemModifier;

public class ElementMoverTest {

  // private IElementCopier copier;
  private IElementRemover remover;
  private Command copier;
  private Command elementMover;
  private MockFileSystemModifier modifier;
  private static Map<String, TreeMap<String, FileSystemElement>> expectedMap;
  private File F1;

  @Before
  public void setUp() throws Exception {
    modifier = new MockFileSystemModifier();
    modifier.addDir("/", "/Directory1");
    modifier.addDir("/Directory1", "/Directory2");
    modifier.addDir("/", "/Directory3");
    modifier.addDir("/", "/Directory4");
    modifier.addFile("/Directory1/Directory2/File1");
    modifier.addFile("/File1");

    remover = new MockElementRemover();
    copier = new ElementCopier.Builder().elementRemover(remover)
        .fileSystem(modifier).build();
    elementMover = new ElementMover.Builder()
        .elementCopier((IElementCopier) copier).elementRemover(remover).build();

    F1 = (File) modifier.getNewOuterMap().get("/").get("/File1");
    F1.setContent("This will be copied.");

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

  /*
   * The following tests are for the "run" method
   */
  @Test
  public void testRun1() {
    // Move file to a file that does not yet exist; must first create the file
    // remove File1 from map
    F1.setContent("This is File 1 in root.");
    elementMover.run("File1 File2");

    expectedMap.get("/").remove("/File1");
    expectedMap.get("/").put("/File2",
        new File("/File2", "This is File 1 in root."));

    File F2 = (File) modifier.getNewOuterMap().get("/").get("/File2");

    // checks the actual and expected maps are equal
    assertTrue(FileSystem.equals(expectedMap));
    // checks that File1's content was correctly copied to File2
    assertEquals("This is File 1 in root.", F2.getContent());

  }


  @Test
  public void testRun2() {
    // Move file to a file named /File2 which already exists with content inside
    // must overwrite contents of /File2 and remove File1
    modifier.addFile("/File2");
    elementMover.run("File1 /File2");

    expectedMap.get("/").remove("/File1");
    expectedMap.get("/").put("/File2",
        new File("/File2", "This will be copied."));

    File F2 = (File) modifier.getNewOuterMap().get("/").get("/File2");

    assertTrue(FileSystem.equals(expectedMap));
    assertEquals("This will be copied.", F2.getContent());
  }

  @Test
  public void testRun3() {
    // moves file to existing directory adds /Directory2 to which existing
    // /File1 will be moved and /File1 will be removed from root
    modifier.addDir("/", "/Directory2");

    elementMover.run("/File1 /Directory2");

    expectedMap.put("/Directory2", new TreeMap<>());
    expectedMap.get("/").put("/Directory2", new Directory("/Directory2"));
    expectedMap.get("/").remove("/File1");
    expectedMap.get("/Directory2").put("/File1",
        new File("/File1", "This will be copied."));

    File D2F1 =
        (File) modifier.getNewOuterMap().get("/Directory2").get("/File1");

    assertTrue(FileSystem.equals(expectedMap));
    assertEquals("This will be copied.", D2F1.getContent());
  }

  @Test
  public void testRun4() {
    // moves file to existing directory and renames the file and /File1
    // will be removed from root
    modifier.addDir("/", "/Directory2");
    elementMover.run("/File1 /Directory2/File2");

    expectedMap.put("/Directory2", new TreeMap<>());
    expectedMap.get("/").put("/Directory2", new Directory("/Directory2"));
    expectedMap.get("/").remove("/File1");
    expectedMap.get("/Directory2").put("/File2",
        new File("/File2", "This will be copied."));
    File D2F2 =
        (File) modifier.getNewOuterMap().get("/Directory2").get("/File2");

    assertTrue(FileSystem.equals(expectedMap));
    assertEquals(F1.getContent(), D2F2.getContent());
  }

  @Test
  public void testRun5() {
    // test trying to move a directory to an existing file
    // nothing should be changed
    elementMover.run("/Directory1 /File1");
    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testRun6() {
    // moving a directory to another directory should move Directory3 and place
    // it in Directory4 and remove Directory3 from root
    elementMover.run("/Directory3 /Directory4");

    expectedMap.get("/").remove("/Directory3");
    expectedMap.remove("/Directory3");
    expectedMap.put("/Directory4/Directory3", new TreeMap<>());
    expectedMap.get("/Directory4").put("/Directory4/Directory3",
        new Directory("/Directory4/Directory3"));

    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testRun7() {
    /*
     * moving a directory to a directory that doesn't yet exist should move
     * Directory3 and place it in root and rename it Directory5 remove
     * Directory3 from root
     */
    elementMover.run("/Directory3 /Directory5");

    expectedMap.get("/").remove("/Directory3");
    expectedMap.remove("/Directory3");
    expectedMap.put("/Directory5", new TreeMap<>());
    expectedMap.get("/").put("/Directory5", new Directory("/Directory5"));

    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testRun8() {
    /*
     * moving a directory to another directory which contains a directory of the
     * same name should move /Directory2 and replace the Directory2 in
     * Directory1 and remove the Directory2 in root
     */
    modifier.addDir("/", "/Directory2");
    elementMover.run("/Directory2 /Directory1");

    expectedMap.get("/Directory1/Directory2").remove("/File1");

    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testRun9() {
    /*
     * moving a nested directory and placing it in the root should copy
     * Directory2 in Directory1 and replace the one in root new /Directory2
     * should have a file in it still and Directory2 should be removed from
     * inside Directory1
     */
    elementMover.run("/Directory1/Directory2 /");

    expectedMap.get("/Directory1").remove("/Directory1/Directory2");
    expectedMap.remove("/Directory1/Directory2");
    expectedMap.put("/Directory2", new TreeMap<>());
    expectedMap.get("/").put("/Directory2", new Directory("/Directory2"));
    expectedMap.get("/Directory2").put("/File1", new File("/File1", ""));

    assertTrue(FileSystem.equals(expectedMap));
  }

}
