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
import a2a.Command.Builder;
import a2a.File;
import a2a.FileSystem;
import a2a.FileSystemElement;
import a2a.WorkingDirectory;
import driver.JShell;
import mockObjects.MockFileSystemModifier;

public class WorkingDirectoryTest {

  private Command newWorkingDir;
  private MockFileSystemModifier modifier;
  private static Builder build;
  private static Map<String, TreeMap<String, FileSystemElement>> expectedMap;

  @Before
  public void SetUp() {
    modifier = new MockFileSystemModifier();
    JShell.setCurrentPath("/");
    newWorkingDir = new WorkingDirectory(build);
    expectedMap = new TreeMap<>();
    expectedMap.put("/", new TreeMap<>());
  }

  @After
  public void tearDown() {
    modifier.clear();
  }

  @Test
  public void testValidArgs1() {
    // tests one arg
    assertFalse(newWorkingDir.validateArgs("Dir1"));
  }

  @Test
  public void testValidArgs2() {
    // tests no args
    assertTrue(newWorkingDir.validateArgs(""));
  }

  @Test
  public void testValidArgs3() {
    // tests two args which calls redirection
    assertTrue(newWorkingDir.validateArgs("> File1"));
  }

  @Test
  public void testValidArgs4() {
    // tests two args which don't call for redurection
    assertFalse(newWorkingDir.validateArgs("File2 File1"));
  }

  @Test
  public void testValidArgs5() {
    // tests three args which tries calls redirection
    assertFalse(newWorkingDir.validateArgs("sdlkf > File1"));
  }

  /*
   * The following tests are for the "run" method and assume that the preceding
   * "validateArgs" method has run correctly
   */
  @Test
  public void testRun1() {
    // checks that it prints working directory when it's root
    assertEquals("/", newWorkingDir.run(""));
  }

  @Test
  public void testRun2() {
    // checks that it prints working directory when it's not root
    modifier.addDir("/", "/Directory1");
    JShell.setCurrentPath("/Directory1");
    assertEquals("/Directory1", newWorkingDir.run(""));
  }

  @Test
  public void testRun3() {
    // checks that it calls redirection when argument is > file
    expectedMap.get("/").put("/File1", new File("/File1", "/"));
    File F1 = (File) expectedMap.get("/").get("/File1");
    assertEquals(F1.getContent(), newWorkingDir.run("> File1"));
    assertTrue(FileSystem.equals(expectedMap));
  }

  @Test
  public void testRun4() {
    // checks that it calls redirection when argument is >> file
    modifier.addFile("/File1");
    File F1 = (File) modifier.getNewOuterMap().get("/").get("/File1");
    F1.setContent("Not current path");
    expectedMap.get("/").put("/File1",
        new File("/File1", "Not current path\n/"));
    File F2 = (File) expectedMap.get("/").get("/File1");
    newWorkingDir.run(">> File1");
    assertTrue(FileSystem.equals(expectedMap));
    assertEquals(F2.getContent(), F1.getContent());
  }



}
