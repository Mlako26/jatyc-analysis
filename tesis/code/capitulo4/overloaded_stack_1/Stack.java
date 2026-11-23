import java.io.FileReader;
import java.io.IOException;
import jatyc.lib.Typestate;
import jatyc.lib.Nullable;

@Typestate("StackProtocol")
public class Stack {
    private int[] stack;
	private int size;
	private int top;

	public Stack(int size) {
		this.size = size;
		this.stack = new int[this.size];
		this.top = -1;
  	}

	public boolean isEmpty() {
		return this.top == -1;
	}

	public boolean isFull() {
		return this.top == this.size - 1;
	}

	public void push(int newValue) {
		this.top++;
		this.stack[this.top] = newValue;
	}

	public void push(float newValue) {
		this.top++;
		this.stack[this.top] = (int) newValue;
	}

	public int pop() {
		this.top--;
		return this.stack[this.top + 1];
	}
}