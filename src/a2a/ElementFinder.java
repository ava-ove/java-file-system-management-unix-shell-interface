package a2a;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Represents find command. Displays the paths of elements found with the given
 * name in the given paths, if arguments valid.
 * 
 * @author Raisa Haque
 *
 */
public class ElementFinder extends Command implements Iterable<String> {

  private Redirection redirectOutput;
  private String dirToSearch;
  private LinkedList<String> validPaths;

  /**
   * Creates initial ElementFinder object by setting instance variables
   * 
   * @param b Builder object to construct ElementFinder object
   */
  public ElementFinder(Builder b) {
    super(b);
    redirectOutput = new Redirection(b);
    dirToSearch = "";
    validPaths = new LinkedList<>();
  }

  /*
   * Checks arguments passed to command to see if it contains > or >> to see if
   * it needs to be redirected. Returns an array of the arguments passed for
   * redirection if arg contains > or >> and an empty array otherwise.
   */
  private String[] validateRedirection(String args) {
    parsedArgs.clear();
    parseArgs(args);

    int arrayLength = parsedArgs.size();

    if (arrayLength > 2) {
      if (parsedArgs.get(arrayLength - 2).contentEquals(">")
          || parsedArgs.get(arrayLength - 2).contentEquals(">>")) {
        return new String[] {parsedArgs.remove(arrayLength - 2),
            parsedArgs.remove(arrayLength - 2)};
      }
    }
    return new String[] {"", ""};
  }

  /**
   * Checks that the syntax of the argument and the paths to search are valid
   * 
   * @param arguments Argument to be validated
   * @return True if argument syntax and at least one path to search is valid,
   *         false otherwise
   */
  @Override
  public boolean validateArgs(String args) {
    validateRedirection(args);
    int arrayLength = parsedArgs.size();

    if (arrayLength < 5) {
      printMessage.printNumArgsError();
      return false;
    }
    if (!parsedArgs.get(arrayLength - 4).contentEquals("-type")
        || !(parsedArgs.get(arrayLength - 3).contentEquals("d")
            || parsedArgs.get(arrayLength - 3).contentEquals("f"))
        || !parsedArgs.get(arrayLength - 2).contentEquals("-name")
        || !(parsedArgs.get(arrayLength - 1).startsWith("\"")
            && parsedArgs.get(arrayLength - 1).endsWith("\""))) {
      printMessage.printInvalidArgSyntax();
      return false;
    }

    if (!validateElement
        .containsValidCharacters(parsedArgs.get(arrayLength - 1).substring(1,
            Math.max(2, parsedArgs.get(arrayLength - 1).length() - 1))))
      return false;

    validPaths = validateDirArgs(args);
    if (validPaths.isEmpty())
      return false;

    return true;
  }

  /*
   * Iterates through the paths passed as an argument and creates a list of
   * valid paths and provides errors for invalid paths.
   */
  private LinkedList<String> validateDirArgs(String args) {
    LinkedList<String> validPaths = new LinkedList<>();
    for (int i = 0; i < parsedArgs.size() - 4; i++) {
      if (!validateElement.validateDirectory(parsedArgs.get(i))
          && validateElement
              .validateFile(pathGiver.getFullPath(parsedArgs.get(i))) == null)
        printMessage.printSpecificInvalidPath(parsedArgs.get(i));
      else
        validPaths.add(pathGiver.getFullPath(parsedArgs.get(i)));
    }
    return validPaths;
  }

  /*
   * Iterates through list of valid paths and searches them recursively to find
   * elements which match the name and type of the element being searched for
   * and adds them to a string of paths found that do match
   */
  private String findElements(String type, String name) {
    String elementsFound = "";
    for (String element : validPaths) {
      // a file has no sub elements but the file itself may match the element
      // name and type of the argument
      if (validateElement.validateFile(element) != null) {
        if (element.endsWith("/" + name) && type.contentEquals("f"))
          elementsFound += element + "\n";
        continue;
      }
      this.dirToSearch = element;
      for (String subElement : this) {
        if (validateElement.validateDirectory(subElement)
            && subElement.equals(element))
          continue;
        if (subElement.endsWith("/" + name)) {
          if ((type.contentEquals("d")
              && validateElement.validateDirectory(subElement))
              || (type.contentEquals("f")
                  && validateElement.validateFile(subElement) != null))
            elementsFound += pathGiver.getFullPath(subElement) + "\n";
        }
      }
    }
    if (!elementsFound.isEmpty())
      return elementsFound.substring(0, elementsFound.lastIndexOf("\n"));
    return "";
  }

  /**
   * Outputs the paths of the file system elements found, if arguments valid
   * 
   * @param args The paths that will be searched to find elements of the given
   *        type with the given name
   * @return String of the full paths of the elements found, if argument valid.
   *         Empty String otherwise.
   */
  @Override
  public String run(String args) {
    String[] redirectionArgs = validateRedirection(args);

    if (!validateArgs(args))
      return "";

    String elementType = parsedArgs.get(parsedArgs.size() - 3);
    String elementName = parsedArgs.get(parsedArgs.size() - 1).substring(1,
        Math.max(2, parsedArgs.get(parsedArgs.size() - 1).length() - 1));

    String output = findElements(elementType, elementName);

    if (output.isEmpty()) {
      printMessage.printElementNotFound();
      return "";
    }
    if (!redirectionArgs[0].isEmpty() && !redirectionArgs[1].isEmpty()) {
      redirectOutput.redirect(output, redirectionArgs[0], redirectionArgs[1]);
      return "";
    }
    printMessage.printString(output);
    return output;
  }

  /**
   * Returns an iterator to search through the tree
   * 
   * @return A TreeIterator that iterates through the file system tree
   */
  @Override
  public Iterator<String> iterator() {
    return new TreeIterator(dirToSearch);
  }

  /**
   * Provides basic information including name, synopsis, description, and
   * errors of find command
   * 
   * @return formatted information about find command
   */
  @Override
  public String toString() {
    String command = "FIND";
    String name = "\n\tfind - search for files and directories";
    String synopsis =
        "\n\tfind PATH ... -type [f/d] -name expression " + "[>/>> OUTFILE]";
    String description =
        "\n\tFind all elements of the type [f/d] (f for file and d for "
            + "directory) matching the name expression in the specified "
            + "PATH(s). \n\tIf PATH is a file path matching the name expression"
            + " and element type, the full path of the file path is returned."
            + "\n\tElements are searched for recursively in the given paths."
            + "\n\tIf element is found, full path is returned.";
    String errors = "\n\tArgument must follow the given syntax."
        + "\n\tArgument must include at least one path."
        + "\n\tExpression must be encased in quotation marks."
        + "\n\tThe given paths must exist in the file system."
        + "\n\tCan be followed by appropriate redirection arguments.";
    return command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
        + description + "\nERRORS" + errors + redirectOutput.toString();
  }

}
