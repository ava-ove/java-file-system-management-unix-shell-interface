package test;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import a2a.FullPathGiver;
import driver.JShell;

public class FullPathGiverTest {

  private FullPathGiver pathGiver;

  @Before
  public void setUp() {
    pathGiver = new FullPathGiver();
    JShell.setCurrentPath("/");
  }

  @Test
  public void testgetFullPath1() {
    // argument is a full path and current path is the root
    String fullPath = pathGiver.getFullPath("/Dir1/Dir2");
    assertEquals("/Dir1/Dir2", fullPath);
  }

  @Test
  public void testgetFullPath2() {
    // argument is a relative path and current path is the root
    String fullPath = pathGiver.getFullPath("Dir1/Dir2");
    assertEquals("/Dir1/Dir2", fullPath);
  }

  @Test
  public void testgetFullPath3() {
    // argument is a full path from the root and the current path is /Dir1
    JShell.setCurrentPath("/Dir1");
    String fullPath = pathGiver.getFullPath("/Dir2/Dir3");
    assertEquals("/Dir2/Dir3", fullPath);
  }

  @Test
  public void testgetFullPath4() {
    // argument is relative to the current path, /Dir1
    JShell.setCurrentPath("/Dir1");
    String fullPath = pathGiver.getFullPath("Dir2/Dir3");
    assertEquals("/Dir1/Dir2/Dir3", fullPath);
  }

  @Test
  public void testgetFullPath5() {
    /*
     * current path is some path in the file system and the argument is relative
     * to it with every .. meaning one directory up from the current (.. twice
     * reaches the root)
     */
    JShell.setCurrentPath("/Dir1/Dir2");
    String fullPath = pathGiver.getFullPath("../../Dir3");
    assertEquals("/Dir3", fullPath);
  }

  @Test
  public void testgetFullPath6() {
    /*
     * current path is some path in the file system and the argument is relative
     * to it with every .. meaning one directory up from the current and .
     * meaning stay where you are
     */
    JShell.setCurrentPath("/Dir1/Dir2");
    String fullPath = pathGiver.getFullPath("../../Dir1/./../Dir3/./Dir4");
    assertEquals("/Dir3/Dir4", fullPath);
  }

}
