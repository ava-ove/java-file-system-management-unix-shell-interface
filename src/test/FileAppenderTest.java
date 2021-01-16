package test;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import a2a.Command.Builder;
import a2a.File;
import a2a.FileAppender;

public class FileAppenderTest {

  private FileAppender append;
  File oldFile;
  String newConents;

  @Before
  public void setup() {
    oldFile = new File("File1", "");
    append = new FileAppender(oldFile, new Builder());
  }

  /*
   * The following test the "run" method in FileAppender
   */
  @Test
  public void testRun1() {
    // append empty string
    assertEquals(" ", append.run(" "));
  }

  @Test
  public void testRun2() {
    // append one string
    assertEquals("hello", append.run("hello"));
  }

  @Test
  public void testRun3() {
    // append multiple strings
    String content = append.run("Hello");
    content = append.run("World");
    assertEquals("Hello\nWorld", content);
  }

  @Test
  public void testRun4() {
    // append one string to non-empty file
    oldFile = new File("File1", "This is for A2B");
    append = new FileAppender(oldFile, new Builder());
    assertEquals("This is for A2B\nhello", append.run("hello"));
  }

  @Test
  public void testRun5() {
    // append multiple strings to non-empty file
    oldFile = new File("File1", "This is for A2B");
    append = new FileAppender(oldFile, new Builder());
    String content = append.run("Hello");
    content = append.run("World");
    content = append.run("Mark: A+");
    assertEquals("This is for A2B\nHello\nWorld\nMark: A+", content);
  }


}
