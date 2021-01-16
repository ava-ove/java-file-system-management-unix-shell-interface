package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import a2a.File;
import a2a.History;
import driver.JShell;
import mockObjects.MockFileSystemModifier;

public class HistoryTest {

  History newHistory;
  JShell newShell;
  String expectedHistoryOutput;
  MockFileSystemModifier modifier;

  @Before
  public void setup() {
    newHistory = new History.Builder().build();
    newShell = new JShell();
    expectedHistoryOutput = "";
    modifier = new MockFileSystemModifier();
    modifier.addFile("/File1");
  }

  @After
  public void tearDown() {
    // Clears input log after every test
    JShell.getInputLog().clear();
  }

  /*
   * The following tests are for the method "validateArgs"
   */
  @Test
  public void testValidateArgs1() {
    // no arguments
    assertTrue(newHistory.validateArgs(""));
  }

  @Test
  public void testValidateArgs2() {
    // one valid int argument
    assertTrue(newHistory.validateArgs("5"));
  }

  @Test
  public void testValidateArgs3() {
    // one negative integer argument
    assertFalse(newHistory.validateArgs("-5"));
  }

  @Test
  public void testValidateArgs4() {
    // one non-integer argument
    assertFalse(newHistory.validateArgs("five"));
  }

  @Test
  public void testValidateArgs5() {
    // two valid arguments for redirect and overwrite
    assertTrue(newHistory.validateArgs("> file1"));
  }

  @Test
  public void testValidateArgs6() {
    // two valid arguments for redirect and append
    assertTrue(newHistory.validateArgs(">> file1"));
  }

  @Test
  public void testValidateArgs7() {
    // two arguments, not redirection
    assertFalse(newHistory.validateArgs("file7 file1"));
  }

  @Test
  public void testValidateArgs8() {
    // three valid arguments for redirect and overwrite
    assertTrue(newHistory.validateArgs("5 > file1"));
  }

  @Test
  public void testValidateArgs9() {
    // three valid arguments for redirect and append
    assertTrue(newHistory.validateArgs("5 >> file1"));
  }

  @Test
  public void testValidateArgs10() {
    // three arguments for redirect and overwrite, negative int
    assertFalse(newHistory.validateArgs("-5 > file1"));
  }

  @Test
  public void testValidateArgs11() {
    // three arguments, no redirection
    assertFalse(newHistory.validateArgs("file1 file17 file1"));
  }

  @Test
  public void testValidateArgs12() {
    // three arguments, no integer
    assertFalse(newHistory.validateArgs("five > file1"));
  }

  /*
   * The following tests are for the "run" method and run under the assumption
   * that the "validateArgs" method has executed correctly
   */

  @Test
  public void testRun1() {
    // empty log
    JShell.getInputLog().add("history");
    expectedHistoryOutput = "1. history";
    assertEquals(expectedHistoryOutput, newHistory.run("").trim());
  }


  @Test
  public void testRun2() {
    // integer argument less than length of input log
    JShell.getInputLog().add("mkdir d1");
    JShell.getInputLog().add("hello 1");
    JShell.getInputLog().add("history 1");
    expectedHistoryOutput = "2. hello 1\n3. history 1";
    assertEquals(expectedHistoryOutput, newHistory.run("2").trim());
  }

  @Test
  public void testRun3() {
    // int argument greater than length of log
    JShell.getInputLog().add("mkdir d1");
    JShell.getInputLog().add("hello 1");
    JShell.getInputLog().add("history 200");
    expectedHistoryOutput = "1. mkdir d1\n2. hello 1\n3. history 200";
    assertEquals(expectedHistoryOutput, newHistory.run("200").trim());
  }

  @Test
  public void testRun4() {
    // redirect append to empty file
    JShell.getInputLog().add("mkdir d1");
    JShell.getInputLog().add("hello 1");
    JShell.getInputLog().add("history 200 >> /File1");
    expectedHistoryOutput = "1. mkdir d1\n2. hello 1\n3. history 200 >> /File1";
    assertEquals(expectedHistoryOutput, newHistory.run("200 >> /File1").trim());
  }

  @Test
  public void testRun5() {
    // redirect append to non empty file
    File f1 = (File) modifier.getNewOuterMap().get("/").get("/File1");
    f1.setContent("This is the original content of File1");

    JShell.getInputLog().add("mkdir d1");
    JShell.getInputLog().add("hello 1");
    JShell.getInputLog().add("history 200 >> /File1");
    newHistory.run("200 >> /File1");
    expectedHistoryOutput =
        "This is the original content of File1\n1. mkdir d1\n2. hello 1\n3. "
            + "history 200 >> /File1";
    assertEquals(expectedHistoryOutput, f1.getContent().trim());
  }

  @Test
  public void testRun6() {
    // redirect and overwrite
    File f1 = (File) modifier.getNewOuterMap().get("/").get("/File1");
    f1.setContent("This is the original content of File1");

    JShell.getInputLog().add("mkdir d1");
    JShell.getInputLog().add("hello 1");
    JShell.getInputLog().add("history 3 > /File1");
    newHistory.run("3 > /File1");
    expectedHistoryOutput = "1. mkdir d1\n2. hello 1\n3. history 3 > /File1";
    assertEquals(expectedHistoryOutput, f1.getContent().trim());
  }

}
