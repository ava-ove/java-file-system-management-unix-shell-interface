package a2a;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import interfaces.IRemoteDataFetcher;

/**
 * Represents RemoteDataFetcher object, to fetch a file's content from a URL
 * 
 * @author Fariha Fyrooz
 *
 */
public class RemoteDataFetcher implements IRemoteDataFetcher {


  private ShellOutput printMessage;

  /**
   * Creates RemoteDataFetcher object, and initializes ShellOutput collaborator
   */
  public RemoteDataFetcher() {
    printMessage = new ShellOutput();
  }

  /**
   * Returns content at URL.
   * 
   * @param arg URL which leads to desired file
   * @return content of file
   */
  @Override
  public String getRemoteDataFromUrl(String arg, File newFile) {
    String content = "";
    try {
      URL txtFile = new URL(arg);
      BufferedReader in =
          new BufferedReader(new InputStreamReader(txtFile.openStream()));
      String line;
      if (newFile != null) { // reads content of file at URL
        while ((line = in.readLine()) != null) {
          content += line + "\n";
        }
      }
      in.close();
    } catch (FileNotFoundException fnfe) {
      fnfe.getStackTrace();
      printMessage.printInvalidURLError();
    } catch (Exception e) {
      e.getStackTrace();
    }
    return content;
  }

  // REFERENCE for Curl code
  // https://docs.oracle.com/javase/tutorial/networking/urls/readingURL.html
}

