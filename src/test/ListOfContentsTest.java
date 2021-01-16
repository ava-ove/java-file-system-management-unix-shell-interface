package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import a2a.Command.Builder;
import a2a.File;
import a2a.FileSystem;
import a2a.FileSystemElement;
import a2a.ListOfContents;
import mockObjects.MockFileSystemModifier;

public class ListOfContentsTest {
  private ListOfContents ls;
  private static Builder build;
  private MockFileSystemModifier modifier;
  private ArrayList<String> expectedParsedArgs;
  private ArrayList<String> actualParsedArgs;
  private String expectedOutput;
  private String actualOutput;
  private String toBeRedirected;
  private Map<String, TreeMap<String, FileSystemElement>> expectedMap;

  @Before
  public void setup() {
    modifier = new MockFileSystemModifier();
    modifier.addDir("/", "/Directory1");
    modifier.addFile("/Directory1/File1");
    modifier.addDir("/Directory1", "/Directory2");
    modifier.addFile("/File1");
    modifier.addFile("/Directory1/Directory2/File2");

    ls = new ListOfContents(build);
    expectedParsedArgs = new ArrayList<String>();
    actualParsedArgs = new ArrayList<String>();
    expectedOutput = "";
    actualOutput = "";
    expectedMap = modifier.getNewOuterMap();
    toBeRedirected = "";
  }

  @After
  public void tearDown() {
    modifier.clear();
  }

  /**
   * Testing validateArgs
   */
  // true returned if no ahalsey grarguments given
  @Test
  public void validateArgsTest1() {

    assertTrue(ls.validateArgs(" "));
  }

  // true returned if only argument is -R
  @Test
  public void validateArgsTest2() {
    assertTrue(ls.validateArgs("-R"));
  }

  // true returned if -R and file given
  @Test
  public void validateArgsTest3() {
    assertTrue(ls.validateArgs("-R /File1"));
  }

  // true returned if -R and directory given
  @Test
  public void validateArgsTest4() {
    assertTrue(ls.validateArgs("-R /Directory1"));
  }

  // true returned if -R and a list of valid files and directories given
  @Test
  public void validateArgsTest5() {
    assertTrue(ls.validateArgs("-R /File1 /Directory1"));
  }

  // true returned if valid file given
  @Test
  public void validateArgsTest6() {
    assertTrue(ls.validateArgs("/File1"));
  }

  // true returned if valid directory given
  @Test
  public void validateArgsTest7() {
    assertTrue(ls.validateArgs("/Directory1/Directory2"));
  }

  // true returned if a list of valid files and directories given
  @Test
  public void validateArgsTest8() {
    assertTrue(ls.validateArgs("/File1 /Directory1"));
  }

  // false returned if invalid file/directory given
  @Test
  public void validateArgsTest9() {
    assertFalse(ls.validateArgs("/invalidfile"));
  }

  /**
   * Testing parseArgs
   */
  // no args given
  @Test
  public void parseArgsTest1() {
    ls.parseArgs("");
    actualParsedArgs = ls.parsedArgs;
    // parsed args is empty
    assertTrue(expectedParsedArgs.equals(actualParsedArgs));
  }

  // -R given
  @Test
  public void parseArgsTest2() {
    ls.parseArgs("-R");
    actualParsedArgs = ls.parsedArgs;
    // -R just changes the state of recursiveListing so parsed args stays empty
    assertTrue(expectedParsedArgs.equals(actualParsedArgs));
  }

  // -R, files and directories given
  // checks if full path of file/directory stored
  @Test
  public void parseArgsTest3() {
    ls.parseArgs("-R /Directory1 /File1");
    actualParsedArgs = ls.parsedArgs;
    // -R just changes the state of recursiveListing
    expectedParsedArgs.add("/Directory1");
    expectedParsedArgs.add("/File1");
    assertTrue(expectedParsedArgs.equals(actualParsedArgs));
  }

  // files and directories with > and OUTFILE path given
  @Test
  public void parseArgsTest4() {
    ls.parseArgs("/File1 /Directory1 > /File2");
    actualParsedArgs = ls.parsedArgs;
    expectedParsedArgs.add("/File1");
    expectedParsedArgs.add("/Directory1");
    expectedParsedArgs.add(">");
    expectedParsedArgs.add("/File2");
    assertTrue(expectedParsedArgs.equals(actualParsedArgs));
  }

  /*
   * Note: the only job for run() is to invoke appropriate methods either from
   * ListOfContents or redirect or ShellOutput. The following are tests for
   * run(). There is a comment on top of tests indicating which method which run
   * is invoking is being tested. So the testing for all the methods run is
   * invoking are in the tests for run()
   */

  /**
   * Testing run along with storeContentOfCurrentDirectory
   * storeContentOfDirectoryRecursively storeContentOfMultipleDirectories
   * getDirOrFileName
   * 
   * We are assuming validateArgs is valid from above tests
   */

  // run calls storeContentOfCurrentDirectory(); no argument given
  @Test
  public void runTest1() {
    actualOutput = ls.run("");
    expectedOutput = "Directory1\n" + "File1";
    assertEquals(expectedOutput, actualOutput);
  }

  // run calls storeContentOfMultipleDirectories(); file given
  @Test
  public void runTest2() {
    actualOutput = ls.run("/File1");
    expectedOutput = "File1";
    assertEquals(expectedOutput, actualOutput);
  }

  // run calls storeContentOfMultipleDirectories(); directory given
  @Test
  public void runTest3() {
    actualOutput = ls.run("/Directory1");
    expectedOutput = "Directory2\n" + "File1";
    assertEquals(expectedOutput, actualOutput);
  }

  // run calls storeContentOfMultipleDirectories();
  // multiple files and directory given
  @Test
  public void runTest4() {
    actualOutput = ls.run("/Directory1 /File1");;
    expectedOutput = "/Directory1:\n" + "Directory2 File1 \n\n" + "File1";
    assertEquals(expectedOutput, actualOutput);
  }

  // run calls storeContentOfDirectoryRecursively(); -R given
  @Test
  public void runTest5() {
    actualOutput = ls.run("-R");
    System.out.println("**" + actualOutput + "**");
    expectedOutput = "Directory1\n" + "File1\n" + "Directory1/Directory2\n"
        + "Directory1/File1";
    System.out.println("ACTUAL:" + actualOutput + "END");
    assertEquals(expectedOutput, actualOutput);
  }

  // run calls storeContentOfMultipleDirectoriesRecursively(); -R and file
  @Test
  public void runTest6() {
    actualOutput = ls.run("-R /File1"); // only single file given
    expectedOutput = "File1";
    assertEquals(expectedOutput, actualOutput);
  }

  // run calls storeContentOfMultipleDirectoriesRecursively();
  // -R and directory given
  @Test
  public void runTest7() {
    actualOutput = ls.run("-R /Directory1"); // only single file given
    expectedOutput = "Directory2\n" + "File1";
    assertEquals(expectedOutput, actualOutput);
  }

  // run calls storeContentOfMultipleDirectoriesRecursively()
  // -R and multiple files directories given
  @Test
  public void runTest8() {
    actualOutput = ls.run("-R /Directory1 /File1"); // only single file given
    expectedOutput = "/Directory1:\nDirectory2 File1 \n\nFile1";
    assertEquals(expectedOutput, actualOutput);
  }


  /*
   * Note: only one invalid test case, since assuming validateArgs is working
   * based on the above test cases
   */
  @Test
  public void runTest9() {
    actualOutput = ls.run("invalidFile"); // only single file given
    expectedOutput = "";
    assertEquals(expectedOutput, actualOutput);
  }

  /*
   * NOTE: assuming ListOfContent is valid based on above tests, these are the
   * tests for listofcontent's redirection
   */
  // OUTFILE is created with ls content if > and OUTFILE given
  @Test
  public void runTest11() {
    toBeRedirected = ls.run("> /File2");
    expectedMap.get("/").put("/File2",
        new File("/File2", "Directory1\n" + "File1"));
    File F1 = (File) expectedMap.get("/").get("/File2");
    assertTrue(FileSystem.equals(expectedMap));
    assertEquals(toBeRedirected, F1.getContent());
  }

  // OUTFILE is appended with ls content if >> and OUTFILE given
  @Test
  public void runTest12() {
    toBeRedirected = ls.run("> /File2") + "\n" + ls.run(">> /File2");
    expectedMap.get("/").put("/File2", new File("/File2",
        "Directory1\n" + "File1\n" + "Directory1\n" + "File1\nFile2"));
    File F1 = (File) expectedMap.get("/").get("/File2");
    assertTrue(FileSystem.equals(expectedMap));
    assertEquals(F1.getContent(), toBeRedirected);
  }


}
