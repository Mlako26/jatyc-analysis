import jatyc.lib.*;

@Typestate("RobotControllerProtocol")
public class RobotController {
    private Robot robot;

	public RobotController() {
		this.robot = new Robot();
	}

	public void turnOff() {return;}
}