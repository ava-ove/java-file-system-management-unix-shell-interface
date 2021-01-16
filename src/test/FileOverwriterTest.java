package test;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import a2a.Command.Builder;
import a2a.File;
import a2a.FileOverwriter;

public class FileOverwriterTest {

  private FileOverwriter overwrite;
  File oldFile;
  String newConents;

  @Before
  public void setup() {
    oldFile = new File("File1", "");
    overwrite = new FileOverwriter(oldFile, new Builder());
  }

  @Test
  public void testRun1() {
    // overwrite empty file with empty string
    assertEquals("", overwrite.run(""));
  }

  @Test
  public void testRun2() {
    // overwrite empty file with one string
    assertEquals("hello", overwrite.run("hello"));
  }

  @Test
  public void testRun3() {
    // overwrite empty file content multiple times
    String content = overwrite.run("Hello");
    content = overwrite.run("World");
    assertEquals("World", content);
  }

  @Test
  public void testRun4() {
    // overwrite non-empty file with one string
    oldFile = new File("File1", "This is for A2B");
    overwrite = new FileOverwriter(oldFile, new Builder());
    assertEquals("Overwritten", overwrite.run("Overwritten"));
  }

  @Test
  public void testRun5() {
    // overwrite non-empty file content multiple times
    oldFile = new File("File1", "This is for A2B");
    overwrite = new FileOverwriter(oldFile, new Builder());
    String content = overwrite.run("Hello");
    content = overwrite.run("World");
    content = overwrite.run("Mark: A+");
    assertEquals("Mark: A+", content);
  }

  @Test
  public void testRun6() {
    // overwrite non-empty file with multiline string
    oldFile = new File("File1", "This is for A2B");
    overwrite = new FileOverwriter(oldFile, new Builder());
    assertEquals("Hello\nWorld\nSeparate\nLines",
        overwrite.run("Hello\nWorld\nSeparate\nLines"));
  }

}
