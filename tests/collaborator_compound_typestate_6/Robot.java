import java.io.FileReader;
import java.io.IOException;
import jatyc.lib.Typestate;
import jatyc.lib.Nullable;

@Typestate("RobotProtocol")
public class Robot {
    private int x;
    private int y;

	public Robot() {
		this.x = 0;
      	this.y = 0;
  	}

	public void moveLeft() {
		this.x--;
		if (x < 0) {
			throw new RuntimeException("I fell off the map!");
		}
	}

	public void moveRight() {
		this.x++;
		if (x > 1) {
			throw new RuntimeException("I fell off the map!");
		}
	}

	public void moveDown() {
		this.y++;
		if (y > 1) {
			throw new RuntimeException("I fell off the map!");
		}
	}
	
	public void moveUp() {
		this.y--;
		if (y < 0) {
			throw new RuntimeException("I fell off the map!");
		}
	}

	public void rest() {
		return;
	}

	public void dance() {
		return;
	}
}