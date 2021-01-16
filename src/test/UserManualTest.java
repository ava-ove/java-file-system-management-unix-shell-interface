package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import a2a.Command;
import a2a.Command.Builder;
import a2a.File;
import a2a.Redirection;
import a2a.UserManual;
import mockObjects.MockFileSystemModifier;

public class UserManualTest {

  private Command userMan;
  private static Builder build;
  private MockFileSystemModifier modifier;
  private Redirection redirect;

  @Before
  public void setUp() {
    userMan = new UserManual(build);
    modifier = new MockFileSystemModifier();
    modifier.addFile("/File1");
    redirect = new Redirection(build);
  }

  /*
   * The following tests are for the "validateArgs" method
   */
  @Test
  public void testValidateArgs1() {
    // man expects 1 (a command) or 3 arguments (for redirection)
    assertFalse(userMan.validateArgs(""));
  }

  @Test
  public void testValidateArgs2() {
    // man expects 1 (a command) or 3 arguments (for redirection)
    assertFalse(userMan.validateArgs("man cd"));
  }

  @Test
  public void testValidateArgs3() {
    // the argument must be a valid command
    assertFalse(userMan.validateArgs("cmd"));
  }

  @Test
  public void testValidateArgs4() {
    // mkdir is a valid command
    assertTrue(userMan.validateArgs("mkdir"));
  }

  @Test
  public void testValidateArgs5() {
    /*
     * man checks if first arg (popd) is a valid command and redirection will
     * validate the remaining args
     */
    assertTrue(userMan.validateArgs("popd > /File1"));
  }

  @Test
  public void testValidateArgs6() {
    /*
     * man checks if first arg (ls) is a valid command and redirection will
     * validate the remaining args
     */
    assertTrue(userMan.validateArgs("ls >> /File1"));
  }

  /*
   * The following tests are for the "run" method and assume that the preceding
   * "validateArgs" method has run correctly
   */
  @Test
  public void testRun1() {
    // man outputs the user manual for the command mkdir
    String command = "MKDIR";
    String name = "\n\tmkdir - make directories";
    String synopsis = "\n\tmkdir DIR1 [DIR2...]";
    String description =
        "\n\tCreate new directories in specified locations if a directory or "
            + "file with same path does not already exist."
            + "\n\tThe directory path may be a full path or a relative path.";
    String errors =
        "\n\tAll directory names must be valid.\n\tDirectory names must not "
            + "contain invalid characters.\n\tDirectory or file with same name "
            + "must not already exist in specified locations.\n\tFull or "
            + "relative paths must be valid.";
    String mkdirManual = command + "\nNAME" + name + "\nSYNOPSIS" + synopsis
        + "\nDESCRIPTION" + description + "\nERRORS" + errors;
    assertEquals(mkdirManual, userMan.run("mkdir"));
  }

  @Test
  public void testRun2() {
    // man outputs the user manual for the command man
    String command = "MAN";
    String name = "\n\tman - print command manual";
    String synopsis = "\n\tman COMMAND [>/>> OUTFILE]";
    String description = "\n\tPrint the user manual for the specified command.";
    String errors =
        "\n\tFirst argument must be valid command.\n\tCan be followed by valid "
            + "redirection arguments.";
    String manManual =
        command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
            + description + "\nERRORS" + errors + redirect.toString();
    assertEquals(manManual, userMan.run("man"));
  }

  @Test
  public void testRun3() {
    // Overwriting a file with the user manual of command cd
    userMan.run("cd > /File1");
    File F1 = (File) modifier.getNewOuterMap().get("/").get("/File1");
    String actual = F1.getContent();

    String command = "CD";
    String name = "\n\tcd - change directory";
    String synopsis = "\n\tcd DIRECTORY";
    String description =
        "\n\tChange the current working directory to the specified directory."
            + "\n\tThe directory path may be a full path or a relative path.";
    String errors = "\n\tOnly 1 argument valid.\n\tDirectory must exist in "
        + "the file system.\n\tFull or relative path must be valid.";
    String expected = command + "\nNAME" + name + "\nSYNOPSIS" + synopsis
        + "\nDESCRIPTION" + description + "\nERRORS" + errors;

    assertEquals(expected, actual);
  }

  @Test
  public void testRun4() {
    // adding content to a file in the file system
    File F1 = (File) modifier.getNewOuterMap().get("/").get("/File1");
    F1.setContent("this will be appended to");
    userMan.run("pwd >> /File1");
    String actual = F1.getContent();
    // appending the user manual of command pwd to a file with existing content
    String command = "PWD";
    String name = "\n\tpwd - output name of current working directory";
    String synopsis = "\n\tpwd [>/>> OUTFILE]";
    String description =
        "\n\tOutput the full path of the current working directory.";
    String errors =
        "\n\tTakes 0 arguments, or 2 valid arguments for redirection.";
    String expected =
        command + "\nNAME" + name + "\nSYNOPSIS" + synopsis + "\nDESCRIPTION"
            + description + "\nERRORS" + errors + redirect.toString();

    assertEquals("this will be appended to\n" + expected, actual);
  }

}
