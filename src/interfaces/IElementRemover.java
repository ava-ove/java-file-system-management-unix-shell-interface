package interfaces;

public interface IElementRemover {

  /**
   * Checks that argument is a valid file or directory that can be removed from
   * file system.
   * 
   * @param args Argument to be validated
   * @return True if argument is valid and can be removed. Otherwise, return
   *         false.
   */
  public boolean validateArgs(String args);

  /**
   * Removes file or directory from file system given a valid argument.
   * 
   * @param args Argument for which, if valid, element will be removed from file
   *        system.
   * @return Output to shell
   */
  public String run(String args);

}
