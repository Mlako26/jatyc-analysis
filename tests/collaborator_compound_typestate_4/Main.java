public class Main {
    // public static void main(String args[]) throws Exception {
	// 	Robot robot = new Robot();
	// 	RobotController controller = new RobotController(robot);

	// 	if (controller.canMoveRobot()) {
	// 		controller.run();
	// 	}

	// 	controller.turnOff();

	// 	robot.dance();
	// 	robot.moveLeft();
    // }

    public static void main(String args[]) throws Exception {
		RobotController controller = new RobotController(new Robot());

		controller.turnOff();
	}
}
