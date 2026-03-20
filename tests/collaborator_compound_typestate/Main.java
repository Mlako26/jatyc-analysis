public class Main {
    public static void main(String args[]) throws Exception {
		RobotController controller = new RobotController();

		if (controller.canMoveRobot()) {
			controller.move("right");
		}
    }
}
