import jatyc.lib.*;

@Typestate("RobotProtocol")
public class Robot {
  private int x;
  private int y;

  public Robot() {
    this.x = 0;
    this.y = 0;
  }

  public void moveLeft() {
    this.x--;
    if (x < 0) {
      throw new RuntimeException("I fell off the map!");
    }
  }

  public void moveRight() {...}
  public void moveUp() {...}
  public void moveDown() {...}
}