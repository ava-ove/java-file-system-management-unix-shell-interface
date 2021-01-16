package test;

import static org.junit.Assert.assertEquals;
import java.util.TreeMap;
import org.junit.Before;
import org.junit.Test;
import a2a.FileSystem;

public class FileSystemTest {

  private FileSystem first;

  @Before
  public void setup() {
    first = FileSystem.createFileSystemInstance();
  }

  @Test
  public void testCreateFileSystemInstance1() {
    TreeMap<String, TreeMap<String, ?>> expected = new TreeMap<>();
    expected.put("/", new TreeMap<>());
    // side effect of new instance being created: outerMap has new directory
    assertEquals(expected, FileSystem.getOuterMap());
  }

  @Test
  public void testCreateFileSystemInstance2() {
    // test that creating a second fileSystem object will point to the same
    // object as the first one created
    FileSystem second = FileSystem.createFileSystemInstance();
    assertEquals(first, second);
  }

}
