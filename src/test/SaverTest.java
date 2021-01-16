package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import a2a.Command;
import a2a.DirStackPusher;
import a2a.FileSystem;
import a2a.Saver;
import driver.JShell;
import mockObjects.MockFileFetcher;
import mockObjects.MockFileSystemModifier;

public class SaverTest {

  private Command save;
  private MockFileFetcher fetch;
  private MockFileSystemModifier modifier;

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Before
  public void setup() throws IOException {
    FileSystem.createFileSystemInstance();
    modifier = new MockFileSystemModifier();

    // creates a temporary file with the name "File1.txt"
    tempFolder.newFile("File1.txt");
    fetch = new MockFileFetcher();
    save = new Saver.Builder().fileFetcher(fetch).build();
  }

  @After
  public void tearDown() {
    JShell.setCurrentPath("/");
    modifier.clear();
  }

  /*
   * The following tests are for the "validateArgs" method in save, but work
   * under the assumption that the getFile method in FileFetcher work correctly
   * to determine a valid file on your local fileSystem as this cannot be tested
   * here
   */
  @Test
  public void testValidateArgs1() {
    // no argument is invalid
    assertFalse(save.validateArgs(""));
  }

  @Test
  public void testValidateArgs2() {
    // any more than 1 argument is invalid
    assertFalse(save.validateArgs("File1.txt hello"));
  }

  @Test
  public void testValidateArgs3() {
    // one valid argument
    assertTrue(save.validateArgs("File1.txt"));
  }

  /*
   * The following tests are for the "run" method for save and work under the
   * assumption that the file validation process has run correctly
   */
  @Test
  public void testRun1() {
    // save default/root current path
    String output = save.run("File1.txt");
    assertEquals("/", output.trim().split("\\r?\\n")[0]);
  }

  @Test
  public void testRun2() {
    // save non-default current path
    JShell.setCurrentPath("/Directory1");
    String output = save.run("File1.txt");
    assertEquals("/Directory1", output.trim().split("\\r?\\n")[0]);
  }


  @Test
  public void testRun3() {
    // save empty input log
    String output = save.run("File1.txt");
    assertEquals("[]", output.trim().split("\\r?\\n")[1]);
  }

  @Test
  public void testRun4() {
    // save non-empty input log
    JShell.getInputLog().add("asdfasdfasdf");
    JShell.getInputLog().add("save File1.txt");
    String output = save.run("File1.txt");
    assertEquals("[asdfasdfasdf, save File1.txt]",
        output.trim().split("\\r?\\n")[1]);
  }

  @Test
  public void testRun5() {
    // save empty directory stack
    String output = save.run("File1.txt");
    assertEquals("[]", output.trim().split("\\r?\\n")[2]);
  }

  @Test
  public void testRun6() {
    // save non-empty directory stack
    DirStackPusher.getDirectoryStack().add("/Directory1");
    DirStackPusher.getDirectoryStack().add("/Directory1/Directory2");
    String output = save.run("File1.txt");
    assertEquals("[/Directory1, /Directory1/Directory2]",
        output.trim().split("\\r?\\n")[2]);
  }

  @Test
  public void testRun7() {
    // save root/default file system
    String output = save.run("File1.txt");
    assertEquals("{/={}}", output.trim().split("\\r?\\n")[3]);
  }

  @Test
  public void testRun8() {
    // save file system with directories and files in it
    modifier.addDir("/", "/Directory1");
    String output = save.run("File1.txt");

    String map = output.trim().split("\\r?\\n")[3];
    // replaces the directory reference with an empty string
    map =
        map.replace(map.substring(map.indexOf("=a2a."), map.indexOf("}")), "");
    assertEquals("{/={/Directory1}, /Directory1={}}", map);
  }

}
