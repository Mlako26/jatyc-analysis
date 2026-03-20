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
				this.moveUp(this.robot);
				break;
			case "down":
				this.moveDown(this.robot);
				break;
			case "left":
				this.moveLeft(this.robot);
				break;
			case "right":
				this.moveRight(this.robot);
				break;
			default:
				throw new RuntimeException("Invalid direction of movement");
		}
	}

	private void moveLeft(@Requires({"TopRight", "BotRight"}) Robot robot) {
		robot.moveLeft();
	}

	private void moveRight(@Requires({"TopLeft", "BotLeft"}) Robot robot) {
		robot.moveRight();
	}

	private void moveDown(@Requires({"TopLeft", "TopRight"}) Robot robot) {
		robot.moveDown();
	}
	
	private void moveUp(@Requires({"BotLeft", "BotRight"}) Robot robot) {
		robot.moveUp();
	}
}