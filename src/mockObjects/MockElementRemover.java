package mockObjects;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import a2a.FileSystem;
import a2a.FileSystemElement;
import a2a.FullPathGiver;
import driver.JShell;
import interfaces.IElementRemover;

/**
 * Represents mock object of command rm. Removes specific directories and files
 * from the file system.
 * 
 * @author Raisa Haque
 * @author Ananya Poddar
 *
 */
public class MockElementRemover implements IElementRemover {

  private static Map<String, TreeMap<String, FileSystemElement>> newOuterMap;
  private FullPathGiver pathGiver;

  /**
   * Creates initial MockElementRemover object by setting instance variables.
   */
  public MockElementRemover() {
    newOuterMap = FileSystem.getOuterMap();
    pathGiver = new FullPathGiver();
  }

  /**
   * Validates argument to ensure it is a file or directory that is allowed to
   * be deleted by the MockElementRemover object.
   * 
   * @param args The argument to be validated.
   * @return True if args is an element that is allowed to be deleted.
   *         Otherwise, return false.
   */
  @Override
  public boolean validateArgs(String args) {
    args = pathGiver.getFullPath(args);

    if (JShell.getCurrentPath().contains(args))
      return false;

    LinkedList<String> validPaths = new LinkedList<>();
    validPaths.add("/Directory1");
    validPaths.add("/Directory2");
    validPaths.add("/Directory3");
    validPaths.add("/Directory4");
    validPaths.add("/Directory1/Directory2");
    validPaths.add("/Directory1/Directory2/File1");
    validPaths.add("/File1");

    if (!validPaths.contains(args))
      return false;

    if (!args.equals("/File1") && !args.equals("/Directory1/Directory2/File1")
        && !newOuterMap.containsKey(args))
      return false;

    return true;
  }

  /**
   * Removes element from the file system, if argument is valid.
   * 
   * @param args Argument for which, if valid, the file or directory will be
   * removed.
   * @return Output to shell (empty string)
   */
  public String run(String args) {
    if (!validateArgs(args))
      return "";

    args = pathGiver.getFullPath(args);

    if (args.equals("/Directory1") && newOuterMap.containsKey("/Directory1")) {
      newOuterMap.get("/").remove("/Directory1");
      newOuterMap.remove("/Directory1");
      if (newOuterMap.containsKey("/Directory1/Directory2"))
        newOuterMap.remove("/Directory1/Directory2");
    }
    if (args.equals("/Directory1/Directory2")
        && newOuterMap.containsKey("/Directory1/Directory2")) {
      newOuterMap.remove("/Directory1/Directory2");
      newOuterMap.get("/Directory1").remove("/Directory1/Directory2");
    }
    if (args.lastIndexOf("/") == 0 && args.startsWith("/Directory")
        && !args.equals("/Directory1") && newOuterMap.containsKey(args)) {
      newOuterMap.get("/").remove(args);
      newOuterMap.remove(args);
    }
    if (args.equals("/File1"))
      newOuterMap.get("/").remove("/File1");
    if (args.equals("/Directory1/Directory2/File1"))
      newOuterMap.get("/Directory1/Directory2").remove("/File1");

    return "";
  }

}
