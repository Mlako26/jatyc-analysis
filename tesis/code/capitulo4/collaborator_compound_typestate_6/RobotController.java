import jatyc.lib.*;

@Typestate("RobotControllerProtocol")
public class RobotController {
  private Robot robot;

	public RobotController(@Requires("TopLeft") Robot robot) {
		this.robot = robot;
	}

	public @Ensures("TopLeft") Robot getRobot() { return this.robot; }
	public boolean canMoveRobot() { return true; }
	public void run() { 
    this.robot.moveRight();
		this.robot.moveLeft();
  }
}