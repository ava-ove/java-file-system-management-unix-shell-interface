package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import a2a.Command;
import a2a.Command.Builder;
import driver.JShell;
import mockObjects.MockFileSystemModifier;
import a2a.ElementFinder;
import a2a.File;

public class ElementFinderTest {

  private Command find;
  private static Builder build;
  private MockFileSystemModifier modifier;

  @Before
  public void setUp() {
    modifier = new MockFileSystemModifier();
    modifier.addDir("/", "/Directory1");
    modifier.addDir("/", "/Directory3");
    modifier.addDir("/", "/Directory4");
    modifier.addDir("/Directory1", "/Directory2");
    modifier.addDir("/Directory1", "/Directory5");
    modifier.addDir("/Directory1", "/Directory6");
    modifier.addFile("/File1");
    find = new ElementFinder(build);
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
    // if no args are passed to find command
    assertFalse(find.validateArgs(" "));
  }

  @Test
  public void testValidateArgs2() {
    // if the argument syntax is wrong ie. missing dashes and quotes
    assertFalse(find.validateArgs("/ type d name Directory1"));
  }

  @Test
  public void testValidateArgs3() {
    // if the argument syntax is wrong ie. no quotes around expression
    assertFalse(find.validateArgs("/ -type d -name Directory1"));
  }

  @Test
  public void testValidateArgs4() {
    /*
     * if the name of the element contain invalid characters, there's no way for
     * it to exist in the file system
     */
    assertFalse(find.validateArgs("/ -type d -name \"Directory@1\""));
  }

  @Test
  public void testValidateArgs5() {
    // searching for a dir in one path with correct arg syntax and valid name
    assertTrue(find.validateArgs("/ -type d -name \"Directory1\""));
  }

  @Test
  public void testValidateArgs6() {
    /*
     * searching for a dir in multiple paths with correct arg syntax and valid
     * name
     */
    assertTrue(find.validateArgs(
        "/ /Directory1 /Directory3 -type d -name \"Directory1\""));
  }

  @Test
  public void testValidateArgs7() {
    /*
     * searching for a file in multiple paths with correct arg syntax and valid
     * name
     */
    assertTrue(
        find.validateArgs("/ /Directory1 /Directory3 -type f -name \"File1\""));
  }

  @Test
  public void testValidateArgs8() {
    /*
     * one path doesn't exist in the file system but the other does so it can
     * still be searched
     */
    assertTrue(
        find.validateArgs("/ /Directory7 -type f -name \"File1\""));
  }

  @Test
  public void testValidateArgs9() {
    /*
     * none of the paths exist in the file system so there is nothing to be
     * searched
     */
    assertFalse(
        find.validateArgs("/Dir7 /Dir8 -type f -name \"File1\""));
  }

  @Test
  public void testValidateArgs10() {
    /*
     * True if everything before > is valid. Redirection will check if OUTFILE
     * is valid.
     */
    assertTrue(find.validateArgs("/ -type f -name \"File1\" > /File2"));
  }

  @Test
  public void testValidateArgs11() {
    /*
     * True if everything before >> is valid. Redirection will check if OUTFILE
     * is valid.
     */
    assertTrue(
        find.validateArgs("/ /Directory1 -type f -name \"File1\" > /File2"));
  }

  /*
   * The following tests are for the "run" method and assume that the preceding
   * "validateArgs" method has run correctly
   */
  @Test
  public void testRun1() {
    // Directory1 exists in the root
    String actual = find.run("/ -type d -name \"Directory1\"");
    assertEquals("/Directory1", actual);
  }

  @Test
  public void testRun2() {
    // Directory5 is nested inside another directory in the root
    String actual = find.run("/ -type d -name \"Directory5\"");
    assertEquals("/Directory1/Directory5", actual);
  }

  @Test
  public void testRun3() {
    // Directory5 exists when searching both given paths
    String actual = find.run("/ /Directory1 -type d -name \"Directory5\"");
    assertEquals("/Directory1/Directory5\n/Directory1/Directory5", actual);
  }

  @Test
  public void testRun4() {
    /*
     * Directory7 doesn't exist in the file system but / does and it contains
     * Directory5 somewhere
     */
    String actual = find.run("/ /Directory7 -type d -name \"Directory5\"");
    assertEquals("/Directory1/Directory5", actual);
  }

  @Test
  public void testRun5() {
    // File1 of type file exists in the given paths
    String actual = find.run("/ /Directory3 -type f -name \"File1\"");
    assertEquals("/File1", actual);
  }

  @Test
  public void testRun6() {
    /*
     * Searching for a directory with the name of an existing file should return
     * nothing
     */
    String actual = find.run("/ /Directory3 -type d -name \"File1\"");
    assertEquals("", actual);
  }

  @Test
  public void testRun7() {
    // Searching for a file in a file with the given name
    String actual = find.run("/File1 -type f -name \"File1\"");
    assertEquals("/File1", actual);
  }

  @Test
  public void testRun8() {
    //Searching for a directory in paths that are relative to the current path
    JShell.setCurrentPath("/Directory1");
    String actual =
        find.run(".. Directory3 ../File1 -type d -name \"Directory3\"");
    assertEquals("/Directory3", actual);
  }

  @Test
  public void testRun9() {
    /*
     * Overwriting a file with the full paths of the elements found matching the
     * arguments passed to find
     */
    find.run("/ -type d -name \"Directory1\" > /File1");
    File F1 = (File) modifier.getNewOuterMap().get("/").get("/File1");
    String actual = F1.getContent();
    assertEquals("/Directory1", actual);
  }

  @Test
  public void testRun10() {
    //adding content to a file in the file system
    File F1 = (File) modifier.getNewOuterMap().get("/").get("/File1");
    F1.setContent("this will be appended to");
    /*
     * appending the full paths of the elements found matching the arguments
     * passed to find to a file with existing content
     */
    find.run("/ /Directory1 -type d -name \"Directory5\" >> /File1");
    String actual = F1.getContent();
    assertEquals("this will be appended to\n/Directory1/Directory5"
        + "\n/Directory1/Directory5", actual);
  }
}
