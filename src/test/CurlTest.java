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
import a2a.Curl;
import a2a.Directory;
import a2a.File;
import a2a.FileSystem;
import a2a.FileSystemElement;
import driver.JShell;
import mockObjects.MockFileSystemModifier;
import mockObjects.MockRemoteDataFetcher;

public class CurlTest {

  private Command curl;
  private MockFileSystemModifier modifier;
  private static Map<String, TreeMap<String, FileSystemElement>> expectedMap;

  private MockRemoteDataFetcher rdfetch;

  @Before
  public void setUp() {
    modifier = new MockFileSystemModifier();
    modifier.addDir("/", "/Directory1");
    rdfetch = new MockRemoteDataFetcher();
    curl = new Curl.Builder().fileSystem(modifier).remoteDataFetcher(rdfetch)
        .build();
    expectedMap = new TreeMap<>();
    expectedMap.put("/", new TreeMap<>());
    expectedMap.put("/Directory1", new TreeMap<>());
    expectedMap.get("/").put("/Directory1", new Directory("/Directory1"));
    JShell.setCurrentPath("/");
  }


  @After
  public void tearDown() {
    modifier.clear();
  }

  /*
   * The following tests are for the "validateArgs" method
   */
  @Test
  public void testValidateArgs1() {
    // test one valid arg
    assertTrue(curl.validateArgs("https://www.w3.org/TR/PNG/iso_8859-1.txt"));
  }

  @Test
  public void testValidateArgs2() {
    // test one invalid arg
    assertFalse(curl.validateArgs("http://google.ca/askdlfj"));
  }


  @Test
  public void testValidateArgs3() {
    // test two args
    assertFalse(curl.validateArgs("a b"));
  }

  @Test
  public void testValidateArgs4() {
    // test no args
    assertFalse(curl.validateArgs(""));
  }

  /*
   * The following tests are for the "run" method and assume that the preceding
   * "validateArgs" method has run correctly
   */
  @Test
  public void testRun1() {
    // test adding file when file of same name doesn't exist in file system
    // checks that the filesystem has the correct file
    // checks that the file has correct content
    curl.run("https://www.w3.org/TR/PNG/iso_8859-1.txt");
    File F1 = (File) modifier.getNewOuterMap().get("/").get("/iso_8859-1");
    expectedMap.get("/").put("/iso_8859-1", new File("/iso_8859-1",
        "<body>Hello! This is the mock content!</body>"));
    assertTrue(FileSystem.equals(expectedMap));
    assertEquals(((File) expectedMap.get("/").get("/iso_8859-1")).getContent(),
        F1.getContent());

  }


  @Test
  public void testRun2() {
    // test adding file when file of same name doesn't exist in file system
    // it needs to overwrite the file that already exists
    // add file iso_8859-1 and set it to a different string
    // after running curl, the file should be overwritten with content at url

    modifier.addFile("/iso_8859-1");
    File F1 = (File) modifier.getNewOuterMap().get("/").get("/iso_8859-1");
    F1.setContent("this is not what is at the url");
    expectedMap.get("/").put("/iso_8859-1", new File("/iso_8859-1",
        "<body>Hello! This is the mock content!</body>"));

    curl.run("https://www.w3.org/TR/PNG/iso_8859-1.txt");
    F1 = (File) modifier.getNewOuterMap().get("/").get("/iso_8859-1");
    assertTrue(FileSystem.equals(expectedMap));
    assertEquals(((File) expectedMap.get("/").get("/iso_8859-1")).getContent(),
        F1.getContent());
  }

  @Test
  public void testRun3() {
    // checks curl adds file to current directory (even when it's not root)
    JShell.setCurrentPath("/Directory1");
    curl.run("https://www.w3.org/TR/PNG/iso_8859-1.txt");
    File F1 =
        (File) modifier.getNewOuterMap().get("/Directory1").get("/iso_8859-1");
    expectedMap.get("/Directory1").put("/iso_8859-1", new File("/iso_8859-1",
        "<body>Hello! This is the mock content!</body>"));
    assertTrue(FileSystem.equals(expectedMap));
    assertEquals(
        ((File) expectedMap.get("/Directory1").get("/iso_8859-1")).getContent(),
        F1.getContent());
  }



}


