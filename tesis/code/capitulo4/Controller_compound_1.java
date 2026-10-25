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
				this.moveUp();
				break;
			case "down":
				this.moveDown();
				break;
			case "left":
				this.moveLeft();
				break;
			case "right":
				this.moveRight();
				break;
			default:
				throw new RuntimeException("Invalid direction of movement");
		}
	}
}