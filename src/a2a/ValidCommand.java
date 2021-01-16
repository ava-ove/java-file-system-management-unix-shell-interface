package a2a;

import java.util.HashMap;
import interfaces.IElementCopier;

/**
 * Represents ValidCommands object, containing all valid commands and their
 * corresponding constructors
 * 
 * @author Ananya Poddar
 *
 */
public class ValidCommand {

  private static HashMap<String, Command> validCommands =
      new HashMap<String, Command>();

  private ShellOutput printMessage;
  private static Command.Builder build; // the Builder from superclass Command
  private static FileSystemModifier modifier = new FileSystemModifier();

  // initializes validCommands hashmap with all valid commands
  static {
    validCommands.put("exit", new ProgramQuitter(build));
    validCommands.put("speak", new AudioOutput.Builder().build());
    validCommands.put("mkdir",
        new DirectoryMaker.Builder().fileSystem(modifier).build());
    validCommands.put("cd", new DirectoryChanger(build));
    validCommands.put("ls", new ListOfContents(build));
    validCommands.put("pwd", new WorkingDirectory(build));
    validCommands.put("pushd", new DirStackPusher(build));
    validCommands.put("popd", new DirStackPopper(build));
    validCommands.put("history", new History(new History.Builder()));
    validCommands.put("cat",
        new FileConcatenater.Builder().fileSystem(modifier).build());
    validCommands.put("echo", new Echo.Builder().build());
    validCommands.put("man", new UserManual(build));
    validCommands.put("rm", new ElementRemover(build));
    validCommands.put("save",
        new Saver.Builder().fileFetcher(new FileFetcher()).build());
    validCommands.put("load",
        new Loader.Builder().fileFetcher(new FileFetcher()).build());
    validCommands.put("cp",
        new ElementCopier.Builder().elementRemover(new ElementRemover(build))
            .fileSystem(modifier).build());
    validCommands.put("mv",
        new ElementMover.Builder()
            .elementCopier((IElementCopier) validCommands.get("cp"))
            .elementRemover(new ElementRemover(build)).fileSystem(modifier)
            .build());
    validCommands.put("find", new ElementFinder(build));
    validCommands.put("tree", new Tree(build));
    validCommands.put("curl", new Curl.Builder().fileSystem(modifier)
        .remoteDataFetcher(new RemoteDataFetcher()).build());
  }

  /**
   * Creates ValidCommand object and initializes collaborator ShellOutput
   */
  public ValidCommand() {
    printMessage = new ShellOutput();
  }

  /**
   * Validate whether command is a valid command name
   * 
   * @param command Command to be validated as a valid command
   * @return true if valid command, false otherwise
   */
  public boolean validate(String command) {
    if (validCommands.containsKey(command))
      return true;
    else {
      printMessage.printCommandError();
      return false;
    }
  }

  public HashMap<String, Command> getValidCommands() {
    return validCommands;
  }

}
