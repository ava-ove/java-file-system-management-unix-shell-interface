package test;

import static org.junit.Assert.*;
import java.util.LinkedList;
import java.util.TreeMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import a2a.FileSystemElement;
import a2a.TreeIterator;
import mockObjects.MockFileSystemModifier;

public class TreeIteratorTest {

  private TreeIterator mapIterator;
  private MockFileSystemModifier modifier;
  private LinkedList<String> actualElements;
  private LinkedList<String> expectedElements;

  @Before
  public void setUp() {
    modifier = new MockFileSystemModifier();
    mapIterator = new TreeIterator(modifier.getNewOuterMap(), "/");
    actualElements = new LinkedList<>();
    expectedElements = new LinkedList<>();
    if (modifier.getNewOuterMap().isEmpty())
      modifier.getNewOuterMap().put("/",
          new TreeMap<String, FileSystemElement>());
  }

  @After
  public void tearDown() {
    modifier.getNewOuterMap().clear();
  }
  
  /*
   * The following tests test the TreeIterator object and use both methods
   * hasNext and next as they cannot be tested in isolation (hasNext will 
   * always return true if next doesn't remove items from the list and next
   * calls hasNext in its code).
   */
  @Test
  public void nextTest1() {
    // iterator iterates through file system map which consists of one element
    while (mapIterator.hasNext())
      actualElements.add(mapIterator.next());
    expectedElements.add("/");
    assertEquals(expectedElements, actualElements);
  }

  @Test
  public void nextTest2() {
    // adding multiple directories, files and nested elements to the file system
    modifier.addDir("/", "/Directory1");
    modifier.addDir("/", "/Directory3");
    modifier.addDir("/", "/Directory4");
    modifier.addDir("/Directory1", "/Directory2");
    modifier.addDir("/Directory1", "/Directory5");
    modifier.addFile("/File1");
    modifier.addFile("/Directory1/Directory2/File1");

    /*
     * iterator iterates through file system map consisting of multiple elements
     * and nested elements using bfs, starting at root, and adds elements to
     * list
     */
    while (mapIterator.hasNext())
      actualElements.add(mapIterator.next());

    // elements added to list by level, in alphabetical order
    expectedElements.add("/");
    expectedElements.add("/Directory1");
    expectedElements.add("/Directory3");
    expectedElements.add("/Directory4");
    expectedElements.add("/File1");
    expectedElements.add("/Directory1/Directory2");
    expectedElements.add("/Directory1/Directory5");
    expectedElements.add("/Directory1/Directory2/File1");

    assertEquals(expectedElements, actualElements);
  }

  @Test
  public void nextTest3() {
    // adding multiple directories, files and nested elements to the file system
    modifier.addDir("/", "/Directory1");
    modifier.addDir("/", "/Directory3");
    modifier.addDir("/", "/Directory4");
    modifier.addDir("/Directory1", "/Directory2");
    modifier.addDir("/Directory1", "/Directory5");
    modifier.addFile("/File1");

    // new map iterator which starts at Directory1
    mapIterator = new TreeIterator(modifier.getNewOuterMap(), "/Directory1");

    /*
     * iterator iterates through file system map starting at /Directory1 and
     * adds elements nested in /Directory1 to list
     */
    while (mapIterator.hasNext())
      actualElements.add(mapIterator.next());

    // elements added to list by level, in alphabetical order
    expectedElements.add("/Directory1");
    expectedElements.add("/Directory1/Directory2");
    expectedElements.add("/Directory1/Directory5");

    assertEquals(expectedElements, actualElements);
  }

  @Test
  public void nextTest4() {
    // add elements to file system map
    modifier.addDir("/", "/Directory1");
    modifier.addFile("/File1");

    // new iterator starts at file /File1
    mapIterator = new TreeIterator(modifier.getNewOuterMap(), "/File1");

    /*
     * iterator iterates through map starting at a file (which contains no sub
     * directories or files) so only the starting file is added to the list
     */
    while (mapIterator.hasNext())
      actualElements.add(mapIterator.next());

    expectedElements.add("/File1");
    assertEquals(expectedElements, actualElements);
  }

}
