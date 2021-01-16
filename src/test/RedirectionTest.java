package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Map;
import java.util.TreeMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import a2a.File;
import a2a.FileSystem;
import a2a.FileSystemElement;
import a2a.Redirection;
import mockObjects.MockFileSystemModifier;

public class RedirectionTest {
  private Redirection redirect;
  private MockFileSystemModifier modifier;
  private Map<String, TreeMap<String, FileSystemElement>> expectedMap;

  @Before
  public void setup() {
    // making a builder
    a2a.Command.Builder b = new a2a.Command.Builder();
    redirect = new Redirection(b);
    modifier = new MockFileSystemModifier();
    expectedMap = new TreeMap<>();
    expectedMap.put("/", new TreeMap<>());
  }

  @After
  public void tearDown() {
    modifier.clear();
  }

  /*
   * Tests for allowRedirection
   */
  @Test
  public void allowRedirection1() {
    // false returned if invalid redirect operator given
    assertFalse(
        redirect.allowRedirection("some content", "invalidOperator", "/File1"));
  }

  @Test
  public void allowRedirection2() {
    // false returned if STRING is empty
    assertFalse(redirect.allowRedirection("", ">", "/File1"));
  }

  @Test
  public void allowRedirection3() {
    // false if parent path of OUTFILE is invalid
    // invalidDirectory does not exist
    assertFalse(redirect.allowRedirection("some content", ">",
        "/invalidDirectory/File1"));
  }

  @Test
  public void allowRedirection4() {
    // false returned if valid OUTFILE but ending in /
    assertFalse(redirect.allowRedirection("some content", ">", "/File1/"));
  }

  @Test
  public void allowRedirection5() {
    // false returned if OUTFILE contains invalid characters
    assertFalse(redirect.allowRedirection("some content", ">", "/F!1"));
  }

  @Test
  public void allowRedirection6() {
    // true returned if correct redirect operator and OUTFILE given
    assertTrue(redirect.allowRedirection("some content", ">", "/File1"));
  }

  /**
   * Tests for redirect. If redirect works, it can be assumed that
   * redirectAndAppend and redirectAndOverwrite have run correctly (cannot be
   * tested separately because the methods only create new FileAppender and
   * FileOverwriter objects). Assuming allowRedirection, redirectAndAppend and
   * redirectAndOverwrite are all valid. ie all the above tests pass.
   */
  @Test
  public void redirectTest1() {
    // OUTFILE doesnt exist and is created when > given as redirect operator
    redirect.redirect("some content", ">", "/File1");
    // add new file added with its content to expectedMap
    expectedMap.get("/").put("/File1", new File("/File1", "some content"));
    assertTrue(FileSystem.equals(expectedMap));

  }

  // OUTFILE exists and is overwritten when > given as redirect operator
  @Test
  public void redirectTest2() {
    // assuming redirectTest1 is passed
    modifier.addFile("/File1");
    File F1 = (File) modifier.getNewOuterMap().get("/").get("/File1");
    F1.setContent("This will be overwritten.");

    redirect.redirect("some content being overwritten", ">", "/File1");
    // exptectedMap has overwritten file only
    expectedMap.get("/").put("/File1",
        new File("/File1", "some content being overwritten"));
    assertTrue(FileSystem.equals(expectedMap));
  }

  // OUTFILE doesnt exist and is created when >> given as redirect operator
  @Test
  public void redirectTest3() {
    redirect.redirect("some content", ">>", "/File1");
    // add new file added with its content to expectedMap
    expectedMap.get("/").put("/File1", new File("/File1", "some content"));
    assertTrue(FileSystem.equals(expectedMap));
  }

  // OUTFILE is appended if >> given as redirect operator
  @Test
  public void redirectTest4() {
    modifier.addFile("/File1");
    File F1 = (File) modifier.getNewOuterMap().get("/").get("/File1");
    F1.setContent("some content");
    // add file to be overwritten to modifier
    redirect.redirect("some content being appended", ">>", "/File1");
    // exptectedMap has overwritten file only
    expectedMap.get("/").put("/File1",
        new File("/File1", "some content\n" + "some content being appended"));
    assertTrue(FileSystem.equals(expectedMap));
  }

  // map doesnt change if allowRedirection give false
  // testing one case only, since we assumed allowRedirection is working
  // properly if the allowRedirection tests above all pass
  @Test
  public void redirectTest5() {
    redirect.redirect("some content", "invalidOperator", "invalidFile");
    assertTrue(FileSystem.equals(expectedMap));
  }

}
