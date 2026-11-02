import jatyc.lib.*;
import jatyc.lib.Requires;

@Typestate("RobotControllerProtocol")
public class RobotController {
  private Robot robot;

	public RobotController() {
		this.robot = new Robot();
	}

	public void dance() {
		this.robot.dance();
    this.robot.rest();
	}
}