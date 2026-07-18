public class Main {
	// public static void main(String args[]) throws Exception {
	// 	RobotController controller = new RobotController(new Robot());

	// 	Robot robot = controller.getRobot();
	// 	robot.moveRight();
	// }

	public static void main(String args[]) throws Exception {
		RobotController controller = new RobotController(new Robot());

		Robot robot = controller.getRobot();
		robot.rest();
	}
}
