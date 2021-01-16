package interfaces;

public interface IElementCopier {
  
  public boolean validateArgs(String args);
  
  public void copyFileToFile(String oldPath, String newPath);
  
  public boolean copyDirToDirErrors(String oldPath, String newPath);
  
  public void copyDirToDir(String oldPath, String newPath);
  
  public String run(String args);
  
}
