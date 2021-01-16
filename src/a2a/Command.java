package a2a;

import java.util.ArrayList;
import interfaces.IElementCopier;
import interfaces.IElementRemover;
import interfaces.IFileFetcher;
import interfaces.IFileSystemModifier;
import interfaces.IRemoteDataFetcher;

/**
 * Represents general Command object, that validates arguments and runs the
 * command
 * 
 * @author Ava Oveisi
 * @author Ananya Poddar
 */
public class Command {

  /**
   * stores arguments split at spaces
   */
  public ArrayList<String> parsedArgs = new ArrayList<String>();
  /**
   * empty String argument
   */
  protected String args = "";
  /**
   * ShellOutput object to be used for printing to shell
   */
  protected ShellOutput printMessage;
  /**
   * ValidFileSysElement object to be used for validating files and directories
   */
  protected ValidFileSysElement validateElement;
  /**
   * FullPathGiver object to be used for getting full path
   */
  protected final FullPathGiver pathGiver;

  /**
   * Optional IFileSystemModifier object to be used for modifying file system
   */
  protected static IFileSystemModifier modifier;
  /**
   * Optional IFileFetcher object to be used for getting files from local file
   * system
   */
  protected static IFileFetcher fetcher;
  /**
   * Optional IRemoteDataFetcher object to be used for getting remote data
   */
  protected static IRemoteDataFetcher rdFetcher;
  /**
   * Optional IElementCopier object to be used for copying file system elements
   */
  protected static IElementCopier copier;
  /**
   * Optional IElementRemover object to be used for removing file system
   * elements
   */
  protected static IElementRemover remover;

  /**
   * Creates Command object by initializing collaborators ShellOutput,
   * ValidFileSysElement and FullPathGiver
   * 
   * @param b Builder object to construct Command object
   */
  protected Command(Builder b) {
    printMessage = new ShellOutput();
    validateElement = new ValidFileSysElement();
    pathGiver = new FullPathGiver();
  }

  /**
   * Represents Builder object to create Command object with optional parameters
   * 
   * @author Ananya Poddar
   *
   */
  public static class Builder {
    /**
     * Returns Builder with added IFileSystemModifier collaborator object
     * 
     * @param fs IFileSystemModifier object to set modifier variable
     * @return Command Builder object
     */
    public Builder fileSystem(IFileSystemModifier fs) {
      modifier = fs;
      return this;
    }

    /**
     * Returns Builder with added IFileFetcher collaborator object
     * 
     * @param fetch IFileFetcher object to set fetcher variable
     * @return Command Builder object
     */
    public Builder fileFetcher(IFileFetcher fetch) {
      fetcher = fetch;
      return this;
    }

    /**
     * Returns Builder with added IRemoteDataFetcher collaborator object
     * 
     * @param rd IRemoteDataFetcher object to set rdFetcher variable
     * @return Command Builder object
     */
    public Builder remoteDataFetcher(IRemoteDataFetcher rd) {
      rdFetcher = rd;
      return this;
    }

    /**
     * Returns Builder with added IElementCopier collaborator object
     * 
     * @param cp IElementCopier object to set copier variable
     * @return Command Builder object
     */
    public Builder elementCopier(IElementCopier cp) {
      copier = cp;
      return this;
    }

    /**
     * Returns Builder with added IElementRemover collaborator object
     * 
     * @param rm IElementRemover object to set remover variable
     * @return Command Builder object
     */
    public Builder elementRemover(IElementRemover rm) {
      remover = rm;
      return this;
    }

    /**
     * Builds and returns Command object
     * 
     * @return Command object
     */
    public Command build() {
      return new Command(this);
    }

  }

  /**
   * Parse the given string of arguments by splitting at spaces and stores as
   * arraylist in parsedArgs.
   * 
   * @param args Argument of command to be parsed
   */
  public void parseArgs(String args) {
    for (String arg : args.trim().split("\\s+"))
      this.parsedArgs.add(arg);
  }

  /**
   * Checks that no argument is given and outputs error otherwise.
   * 
   * @return true if no argument given, false otherwise
   */
  public boolean validateArgs(String args) {
    if (args.trim().isEmpty())
      return true;

    printMessage.printNumArgsError();
    return false;
  }

  /**
   * Executes command if args is valid.
   * 
   * @param args Argument used to execute the command
   * @return Output of command, empty string by default
   */
  public String run(String args) {
    return "";
  }

  /**
   * Returns user manual of command
   * 
   * @return user manual
   */
  public String toString() {
    return "";
  }
}
