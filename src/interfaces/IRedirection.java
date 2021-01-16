package interfaces;

import a2a.File;

public interface IRedirection {
  /**
   * Validates conditions for which output redirection allowed. ie. checks for
   * valid  redirection operator and valid outfile
   * 
   * @return true if redirection conditions met, false otherwise
   */
  public boolean allowRedirection(String content, String redirectOperator,
	      String outfile);

  /**
   * Redirect to appropriate methods based on conditions
   * {@link redirectAndAppend} and {@link redirectAndOverwrite}
   */
  public void redirect(String content, String redirectOperator, String outfile);

  /**
   * Redirects to file and appends given content to specified file
   * 
   * @param originalFile File to which content will be appended
   * @param toBeAdded String to be appended to file
   */
  public void redirectAndAppend(File originalFile, String toBeAdded);

  /**
   * Redirects to file and overwrites content of specified file
   * 
   * @param originalFile File whose content will be overwritten
   * @param toBeAdded String to replace content of originalFile
   */
  public void redirectAndOverwrite(File originalFile, String toBeAdded);
}
