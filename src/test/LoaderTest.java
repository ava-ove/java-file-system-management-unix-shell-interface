package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.TreeMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import a2a.Command;
import a2a.DirStackPusher;
import a2a.Directory;
import a2a.File;
import a2a.FileSystem;
import a2a.FileSystemElement;
import a2a.Loader;
import driver.JShell;
import mockObjects.MockFileFetcher;

public class LoaderTest {

  private Command load;

  @Before
  public void setup() throws IOException {
    FileSystem.createFileSystemInstance();
    load = new Loader.Builder().fileFetcher(new MockFileFetcher()).build();
  }

  @After
  public void tearDown() {
    // resets all fields to their appropriate default values
    JShell.setCurrentPath("/");
    DirStackPusher.getDirectoryStack().clear();
    JShell.getInputLog().clear();
  }

  /*
   * The following tests are for the "validateArgs" method in load, but work
   * under the assumption that the getFile method in FileFetcher work correctly
   * to determine a valid file on your local fileSystem as this cannot be tested
   * here
   */
  @Test
  public void testValidateArgs1() {
    // load not called at the beginning of the session
    JShell.getInputLog().add("asdfasdfasdf");
    JShell.getInputLog().add("load File1.txt");
    assertFalse(load.validateArgs("File1.txt"));
  }

  @Test
  public void testValidateArgs2() {
    // no arguments is invalid
    assertFalse(load.validateArgs(""));
  }

  @Test
  public void testValidateArgs3() {
    // more than 1 argument is invalid
    assertFalse(load.validateArgs("File1.txt     hello hi"));
  }

  @Test
  public void testValidateArgs4() {
    // one valid argument
    assertTrue(load.validateArgs("File1.txt"));
  }

  /*
   * The following tests test the "run" method in load, and ensure that the
   * appropriate fields are being set correctly. These tests work under the
   * assumption that "validateArgs" has executed correctly
   */
  @Test
  public void testRun1() {
    // checks that it correctly sets current path
    load.run("File1.txt");
    assertEquals("/Directory1", JShell.getCurrentPath());
  }

  @Test
  public void testRun2() {
    // check that it correctly sets input log
    load.run("File1.txt");
    ArrayList<String> expectedLog = new ArrayList<>();
    expectedLog.addAll(Arrays.asList("mkdir /Directory1", "asdfasdfasdf",
        "cd /Directory1", "save File1.txt", "load File1.txt"));
    assertEquals(expectedLog, JShell.getInputLog());
  }

  @Test
  public void testRun3() {
    // check that it correctly sets directory stack
    load.run("File1.txt");
    Stack<String> expectedDirStack = new Stack<>();
    expectedDirStack.push("/Directory1");
    assertEquals(expectedDirStack, DirStackPusher.getDirectoryStack());
  }

  @Test
  public void testRun4() {
    // check that it correctly sets file system
    load.run("File1.txt");
    TreeMap<String, TreeMap<String, FileSystemElement>> expectedFileSys =
        new TreeMap<>();
    expectedFileSys.put("/", new TreeMap<>());
    expectedFileSys.get("/").put("/Directory1", new Directory("/Directory1"));
    expectedFileSys.get("/").put("/File1", new File("/File1", ""));
    expectedFileSys.put("/Directory1", new TreeMap<>());
    assertTrue(FileSystem.equals(expectedFileSys));
  }

}
