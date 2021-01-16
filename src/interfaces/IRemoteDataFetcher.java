package interfaces;

import a2a.File;

public interface IRemoteDataFetcher {

  /**
   * Gets file and its content from argument URL and adds it to working
   * directory. Returns file's content.
   * 
   * @param arg URL which leads to desired file
   * @param modifier allows method to add File to the fileSystem
   * @return Returns content of file
   */
  public String getRemoteDataFromUrl(String arg, File newFile);



}
