package interfaces;

import a2a.File;

public interface IFileSystemModifier {


  public void addDir(String inDir, String newDirName);

  public File addFile(String filePath);

}
