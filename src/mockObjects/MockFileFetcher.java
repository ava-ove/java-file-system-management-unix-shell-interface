package mockObjects;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.TreeMap;
import a2a.Directory;
import a2a.FileSystemElement;
import interfaces.IFileFetcher;

/**
 * Represents Mock FileFetcher object, to mock retrieving a file and its preset
 * content if its name is "File1.txt"
 * 
 * @author Ananya Poddar
 *
 */
public class MockFileFetcher implements IFileFetcher {

  /**
   * Returns a file object at specified file path, if the name is "File1.txt"
   * 
   * @param filePath the path at which the file is to be created
   * @return File File1.txt object, or null if other file path specified
   */
  @Override
  public File getFile(String filePath) {
    if (!filePath.equals("File1.txt"))
      return null;
    return new File("File1.txt");
  }

  /**
   * Returns the content in the file, if the file is File1.txt
   * 
   * @param retrievedFile The file whose content will be retrieved
   * @return ArrayList Object of preset contents including current working
   *         directory, all inputs, directory stack, and file system
   */
  public Object getFileContent(File retrievedFile) {
    if (retrievedFile.getName().equals("File1.txt")) {
      ArrayList<Object> allInfo = new ArrayList<>();

      allInfo.add("/Directory1");

      ArrayList<String> inputLog = new ArrayList<>();
      inputLog.addAll(Arrays.asList("load File1.txt", "mkdir /Directory1",
          "asdfasdfasdf", "cd /Directory1", "save File1.txt"));

      allInfo.add(inputLog);

      Stack<String> dirStack = new Stack<>();
      dirStack.push("/Directory1");
      allInfo.add(dirStack);

      TreeMap<String, TreeMap<String, FileSystemElement>> newFileSys =
          new TreeMap<>();
      newFileSys.put("/", new TreeMap<>());
      newFileSys.get("/").put("/Directory1", new Directory("/Directory1"));
      newFileSys.get("/").put("/File1", new a2a.File("/File1", ""));
      newFileSys.put("/Directory1", new TreeMap<>());
      allInfo.add(newFileSys);

      return allInfo;
    }
    return null;
  }

}
