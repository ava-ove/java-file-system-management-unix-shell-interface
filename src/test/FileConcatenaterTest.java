package test;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import a2a.Command;
import a2a.File;
import a2a.FileConcatenater;
import driver.JShell;
import mockObjects.MockFileSystemModifier;

public class FileConcatenaterTest {

  private Command fileCat;
  private MockFileSystemModifier modifier;
  private File F1;
  private File F2;

  @Before
  public void setUp() {
    modifier = new MockFileSystemModifier();
    JShell.setCurrentPath("/");
    modifier.addDir("/", "/Directory1");
    modifier.addFile("/File1");
    F1 = (File) modifier.getNewOuterMap().get("/").get("/File1");
    F1.setContent("This is File 1 in root.");
    modifier.addFile("/File2");
    F2 = (File) modifier.getNewOuterMap().get("/").get("/File2");
    F2.setContent("This is File 2 in root.");
    modifier.addFile("/Directory1/File1");
    File D1F1 =
        (File) modifier.getNewOuterMap().get("/Directory1").get("/File1");
    D1F1.setContent("This is File 1 in Directory 1.");
    fileCat = new FileConcatenater.Builder().fileSystem(modifier).build();

  }


  @After
  public void tearDown() {
    modifier.clear();
  }



  @Test
  public void testValidateNumArgs1() {
    // test no args
    assertFalse(((FileConcatenater) fileCat).validateNumArgs(""));
  }

  @Test
  public void testValidateNumArgs2() {
    // test 1 arg
    assertTrue(((FileConcatenater) fileCat).validateNumArgs("File1"));
  }

  @Test
  public void testValidateNumArgs3() {
    // test more than one arg
    assertTrue(((FileConcatenater) fileCat).validateNumArgs("File1 File2"));
  }


  @Test
  public void testValidateArgs1() {
    // test a valid existing file in root
    assertTrue(fileCat.validateArgs("/File1"));
  }

  @Test
  public void testValidateArgs2() {
    // test a valid existing file in a directory
    assertTrue(fileCat.validateArgs("/Directory1/File1"));
  }

  @Test
  public void testValidateArgs3() {
    // test a non-existent file
    assertFalse(fileCat.validateArgs("/File12"));
  }



  /*
   * The following tests are for the "run" method and assume that the preceding
   * "validateNumArgs" and "validateArgs" methods have run correctly
   */

  @Test
  public void testRun1() {
    // test concatenating one file
    String expected = "This is File 1 in root.";
    assertEquals(expected, fileCat.run("File1"));
  }

  @Test
  public void testRun2() {
    // test concatenating two files
    String expected = "This is File 1 in root.\n\n\nThis is File 2 in root.";
    System.out.println("Expected:\n" + fileCat.run("/File1 /File2"));
    assertEquals(expected, fileCat.run("/File1 /File2"));
  }

  @Test
  public void testRun3() {
    // test concatenating one file inside a directory
    String expected = "This is File 1 in Directory 1.";
    assertEquals(expected, fileCat.run("Directory1/File1"));
  }

  @Test
  public void testRun4() {
    // test concatenating files in different directories
    String expected = "This is File 1 in root.\n\n\nThis is File 2 in root."
        + "\n\n\nThis is File 1 in Directory 1.";
    assertEquals(expected, fileCat.run("File1 File2 /Directory1/File1"));
  }

  @Test
  public void testRun5() {
    // test different files in different directories given relative paths
    JShell.setCurrentPath("/Directory1");
    String expected = "This is File 1 in root.\n\n\nThis is File 2 in root."
        + "\n\n\nThis is File 1 in Directory 1.";
    assertEquals(expected, fileCat.run("../File1 /File2 File1"));
  }

  @Test
  public void testRun6() {
    // test trying to concatenate an invalid file
    String expected = "Invalid file(s).";
    assertEquals(expected, fileCat.run("File17"));
  }

  @Test
  public void testRun7() {
    // test multiple files where one is an invalid file
    String expected = "Invalid file(s).";
    assertEquals(expected, fileCat.run("File1 /File12 File2"));
  }

  @Test
  public void testRun8() {
    // checks that it calls redirection when argument ends with > file
    fileCat.run("File1 > File2");
    assertEquals(F1.getContent(), F2.getContent());
  }

  @Test
  public void testRun9() {
    // checks that it calls redirection when argument ends with >> file
    fileCat.run("File1 Directory1/File1 >> File2");
    String expected =
        "This is File 2 in root.\nThis is File 1 in root.\n\n\nThis is File 1 in Directory 1.";
    assertEquals(expected, F2.getContent());
  }

  @Test
  public void testRun10() {

    // checks that it doesn't call redirection when file to be concatenated is
    // invalid even if argument ends with > file
    fileCat.run("File3 >> File2");
    String expected = "This is File 2 in root.";
    assertEquals(expected, F2.getContent());
  }


}
