import jatyc.lib.*;
import jatyc.lib.Requires;
import jatyc.lib.Ensures;

@Typestate("RobotControllerProtocol")
public class RobotController {
    private Robot robot;

	public RobotController(@Requires("TopLeft") Robot robot) {
		this.robot = robot;
	}

	public boolean canMoveRobot() {
		return true;
	}

	public void run() {
		this.robot.moveRight();
		this.robot.moveDown();
		this.robot.moveLeft();
		this.robot.moveUp();
	}

	public @Ensures("TopLeft") Robot getRobot() {
		return this.robot;
	}
}