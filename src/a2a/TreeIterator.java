package a2a;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a file system iterator. Iterates through a file system map from a
 * given starting directory or file.
 * 
 * @author Raisa Haque
 *
 */
public class TreeIterator implements Iterator<String> {

  private ValidFileSysElement validateElement;
  private LinkedList<String> elementsList;
  private Map<String, TreeMap<String, FileSystemElement>> mapToIterate;

  /**
   * Creates initial TreeIterator object by setting instance variables
   * 
   * @param firstElement Directory/File to start iterating from in the file
   *        system tree
   */
  public TreeIterator(String firstElement) {
    this(FileSystem.getOuterMap(), firstElement);
  }

  /**
   * Creates initial TreeIterator object by setting instance variables
   * 
   * @param mapToIterate File System map that is to be iterated
   * @param firstElement Directory/File to start iterating from in the file
   *        system tree
   */
  public TreeIterator(
      Map<String, TreeMap<String, FileSystemElement>> mapToIterate,
      String firstElement) {
    validateElement = new ValidFileSysElement();
    elementsList = new LinkedList<>();
    elementsList.add(firstElement);
    this.mapToIterate = mapToIterate;
  }

  /**
   * Checks that there are elements remaining in the linked list of
   * directories/files to iterate
   * 
   * @return True if there are more elements to iterate. Otherwise, returns
   *         false
   */
  @Override
  public boolean hasNext() {
    return (!elementsList.isEmpty());
  }

  /**
   * Adds sub directories and files of next element to the list of file system
   * elements (given the list is not empty) and removes the next element
   * 
   * @return The next element in the list of file system elements if the list is
   *         not empty. Otherwise, returns null
   */
  @Override
  public String next() {
    if (hasNext()) {
      String current = elementsList.poll();
      String addToPath = current;

      if (validateElement.validateDirectory(current)) {
        if (!this.mapToIterate.get(current).isEmpty()) {
          for (String key : this.mapToIterate.get(current).keySet()) {
            if (current.contentEquals("/"))
              addToPath = "";
            elementsList.add(addToPath + key.substring(key.lastIndexOf("/")));
          }
        }
      }
      return current;
    }
    return null;
  }

}
