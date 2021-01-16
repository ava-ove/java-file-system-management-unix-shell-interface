package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Map;
import java.util.TreeMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import a2a.Command.Builder;
import a2a.File;
import a2a.FileSystem;
import a2a.FileSystemElement;
import a2a.Tree;
import mockObjects.MockFileSystemModifier;

public class TreeTest {
  private Tree treeIterator;
  private static Builder build;
  private MockFileSystemModifier modifier;
  private String expectedOutput;
  private String actualOutput;
  private Map<String, TreeMap<String, FileSystemElement>> expectedMap;

  @Before
  public void setUp() {
    modifier = new MockFileSystemModifier();
    treeIterator = new Tree(build);
    actualOutput = "";
    expectedOutput = "";
    expectedMap = new TreeMap<>();
    expectedMap.put("/", new TreeMap<>());
  }

  @After
  public void tearDown() {
    modifier.clear();
  }

  /*
   * Testing validateArgs
   */
  @Test
  public void validateArgsTest1() {
    // true returned if no argument given
    assertTrue(treeIterator.validateArgs(" "));
  }

  @Test
  public void validateArgsTest2() {
    // true returned if two arguments given (for redirection)
    // doesnt matter if > or OUTFILE are valid, Redirection checks for that
    assertTrue(treeIterator.validateArgs("> /File3"));
  }

  @Test
  public void validateArgsTest3() {
    // false returned if other than 0 or 2 arguments given
    assertFalse(treeIterator.validateArgs("singleArgument"));
  }

  /*
   * Testing run
   */
  @Test
  public void runTest1() {
    // single directory
    modifier.addDir("/", "/Directory1");
    actualOutput = treeIterator.run("");
    expectedOutput = "/\n  Directory1";
    assertEquals(actualOutput, expectedOutput);
  }

  @Test
  public void runTest2() {
    // two nested directories
    modifier.addDir("/", "/Directory1");
    modifier.addDir("/Directory1", "/Directory2");

    actualOutput = treeIterator.run("");
    expectedOutput = "/\n  Directory1\n    Directory2";
    assertEquals(actualOutput, expectedOutput);
  }

  @Test
  public void runTest3() {
    // multiple nested file/directories
    modifier.addDir("/", "/Directory1");
    modifier.addDir("/Directory1", "/Directory2");
    modifier.addFile("/Directory1/Directory2/File1");
    modifier.addDir("/", "/Directory3");
    modifier.addFile("/File2");

    actualOutput = treeIterator.run("");
    expectedOutput = "/\n  File2\n  Directory3\n  Directory1\n    Directory2\n"
        + "      File1";
    assertEquals(actualOutput, expectedOutput);
  }

  @Test
  public void runTest4() {
    // empty file system
    actualOutput = treeIterator.run("");
    expectedOutput = "/";
    assertEquals(actualOutput, expectedOutput);
  }

  /*
   * NOTE: assuming treeIterator is valid based on above tests, these are the
   * tests for tree's redirection
   */
  // OUTFILE is created with tree content if > and OUTFILE given
  @Test
  public void redirectTest4() {
    treeIterator.run("> /File1");
    expectedMap.get("/").put("/File1", new File("/File1", "/"));
    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void redirectTest5() {
    // OUTFILE is appended with tree content if >> and OUTFILE given
    modifier.addFile("/File1");
    File F1 = (File) modifier.getNewOuterMap().get("/").get("/File1");
    F1.setContent("/");

    treeIterator.run(">> /File1");
    expectedMap.get("/").put("/File1", new File("/File1", "/\n/\n  File1"));
    assertTrue(FileSystem.equals(expectedMap));
  }

}
