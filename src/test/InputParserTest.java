package test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import a2a.InputParser;

public class InputParserTest {

  private InputParser parser;

  @Test
  public void testParser1() {
    // empty string
    parser = new InputParser("   ");
    assertEquals("", parser.getCommand());
    assertEquals("", parser.getArguments());
  }

  @Test
  public void testParser2() {
    // command but no argument
    parser = new InputParser("someCommand");
    assertEquals("someCommand", parser.getCommand());
    assertEquals("", parser.getArguments());
  }

  @Test
  public void testParser3() {
    // one argument one space
    parser = new InputParser("man someArg");
    assertEquals("man", parser.getCommand());
    assertEquals("someArg", parser.getArguments());
  }

  @Test
  public void testParser4() {
    // one argument with many spaces in between
    parser = new InputParser("     man                          someArg");
    assertEquals("man", parser.getCommand());
    assertEquals("someArg", parser.getArguments());
  }

  @Test
  public void testParser5() {
    // multiple arguments, each with one space
    parser = new InputParser("mkdir arg1 arg2 arg3");
    assertEquals("mkdir", parser.getCommand());
    assertEquals("arg1 arg2 arg3", parser.getArguments());
  }

  @Test
  public void testParser6() {
    // multiple arguments, each with different spacing
    parser = new InputParser("mkdir    arg1     arg2             arg3");
    assertEquals("mkdir", parser.getCommand());
    assertEquals("arg1 arg2 arg3", parser.getArguments());
  }
}
