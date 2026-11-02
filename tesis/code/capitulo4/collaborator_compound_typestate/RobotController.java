import jatyc.lib.*;

@Typestate("RobotControllerProtocol")
public class RobotController {
  private Robot robot;

  public RobotController() {
    this.robot = new Robot();
  }

  public boolean canMoveRobot() {
    return true;
  }

  public void move(String direction) {
    switch (direction) {
      case "up":
        this.robot.moveUp(); break;
      case "down":
        this.robot.moveDown(); break;
      case "left":
        this.robot.moveLeft(); break;
      case "right":
        this.robot.moveRight(); break;
      default:
        throw new RuntimeException("Invalid movement direction");
    }
  }
}