package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import a2a.ValidCommand;

public class ValidCommandTest {

  private ValidCommand commandObj;

  @Before
  public void setup() {
    commandObj = new ValidCommand();
  }

  /*
   * The following test the validate method in validCommand
   */
  @Test
  public void testValidate1() {
    // tests multiple valid commands
    boolean valid = commandObj.validate("cat");
    assertTrue(valid);
    valid = commandObj.validate("save");
    assertTrue(valid);
    valid = commandObj.validate("man");
    assertTrue(valid);
  }

  @Test
  public void testValidate2() {
    // contains a valid command at the beginning of string but is not valid
    boolean valid = commandObj.validate("catABC");
    assertFalse(valid);
  }

  @Test
  public void testValidate3() {
    // contains a valid command in the middle of string but is not valid
    boolean valid = commandObj.validate("Aspeak23");
    assertFalse(valid);
  }

  public void testValidate4() {
    // tests multiple invalid commands
    boolean valid = commandObj.validate("asldjfasdf");
    assertFalse(valid);
    valid = commandObj.validate("123519hello");
    assertFalse(valid);
  }

}
