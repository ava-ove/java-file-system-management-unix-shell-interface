package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import a2a.FileSystem;
import a2a.ValidFileSysElement;
import mockObjects.MockFileSystemModifier;

public class ValidFileSysElementTest {

  private ValidFileSysElement validElement;

  private MockFileSystemModifier modifier;

  @Before
  public void setup() {
    FileSystem.createFileSystemInstance();
    validElement = new ValidFileSysElement();
    modifier = new MockFileSystemModifier();
  }

  @After
  public void tearDown() {
    modifier.clear();
  }

  /*
   * The following tests are for the "containsValidCharacters" method
   */
  @Test
  public void testContainsValidCharacters1() {
    // contains a space, which is an invalid character
    assertFalse(validElement.containsValidCharacters("File 1"));
  }

  @Test
  public void testContainsValidCharacters2() {
    // contains a "." which is an invalid character
    assertFalse(validElement.containsValidCharacters("File1.txt"));
  }

  @Test
  public void testContainsValidCharacters3() {
    // contains multiple invalid characters
    assertFalse(validElement.containsValidCharacters("File$1@"));
  }

  @Test
  public void testContainsValidCharacters4() {
    // valid file/directory name
    assertTrue(validElement.containsValidCharacters("File123"));
  }

  /*
   * The following tests are for the "validateFile" method.
   */
  @Test
  public void testValidateFile1() {
    // test valid existing file in root
    modifier.addFile("/File1");
    // validateFile should return a reference to a file, not null
    assertNotNull(validElement.validateFile("/File1"));
  }

  @Test
  public void testValidateFile2() {
    // test invalid file in root
    assertNull(validElement.validateFile("/File1"));
  }

  @Test
  public void testValidateFile3() {
    // test valid existing file in another directory
    modifier.addDir("/", "/Directory1");
    modifier.addFile("/Directory1/File1");
    // validateFile should return a reference to a file
    assertNotNull(validElement.validateFile("/Directory1/File1"));

  }

  @Test
  public void testValidateFile4() {
    // test invalid file in another directory
    modifier.addFile("/Directory/File1");
    assertNull(validElement.validateFile("/File1"));
  }

  /*
   * The following tests are for the "validateDirectory" method
   */
  @Test
  public void testValidateDirectory1() {
    // test valid existing directory in root
    modifier.addDir("/", "/Directory1");
    assertTrue(validElement.validateDirectory("/Directory1"));
  }

  @Test
  public void testValidateDirectory2() {
    // test invalid directory in root
    assertFalse(validElement.validateDirectory("/Directory1"));
  }

  @Test
  public void testValidateDirectory3() {
    // test valid existing subdirectory
    modifier.addDir("/", "/Directory1");
    modifier.addDir("/Directory1", "/Directory1/Directory2");
    assertTrue(validElement.validateDirectory("/Directory1/Directory2"));
  }

  @Test
  public void testValidateDirectory4() {
    // test invalid subdirectory
    modifier.addDir("/", "/Directory1");
    assertFalse(validElement.validateDirectory("/Directory1/Directory2"));
  }

}
