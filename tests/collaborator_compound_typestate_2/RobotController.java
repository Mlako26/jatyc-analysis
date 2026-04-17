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

	public void run() {
		this.robot.moveRight();
		this.robot.moveDown();
		this.robot.moveLeft();
		this.robot.moveUp();
	}
}