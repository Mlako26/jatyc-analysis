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

	public void moveRight() {
		this.robot.moveRight();
	}

	public void moveDown() {
		this.robot.moveDown();
	}

	public void moveLeft() {
		this.robot.moveLeft();
	}

	public void moveUp() {
		this.robot.moveUp();
	}
}