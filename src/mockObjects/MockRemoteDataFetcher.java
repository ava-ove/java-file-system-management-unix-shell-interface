package mockObjects;

import a2a.File;
import interfaces.IRemoteDataFetcher;

/**
 * Represents Mock RemoteDataFetcher object, to get preset content regardless of
 * the file
 * 
 * @author Fariha Fyrooz
 *
 */
public class MockRemoteDataFetcher implements IRemoteDataFetcher {

  /**
   * Gets file and its content from argument URL and adds it to working
   * directory. Returns file's content.
   * 
   * @param arg URL which leads to desired file
   * @param modifier allows method to add File to the fileSystem
   * @return Returns content of file
   */
  @Override
  public String getRemoteDataFromUrl(String args, File newFile) {
    
    return "<body>Hello! This is the mock content!</body>";

  }
}

