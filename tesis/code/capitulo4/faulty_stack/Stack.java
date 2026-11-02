import jatyc.lib.*;

@Typestate("StackProtocol")
public class Stack {
  private int[] stack;
	private int top;

	public Stack(int size) {
		this.stack = new int[size];
		this.top = 0;
  }

	public boolean isEmpty() {
    return this.top == 0;
  }

	public boolean isFull() {
    return false;
  }

	public void push(int newValue) {
		this.stack[this.top++] = newValue;
	}

	public int pop() {
		return this.stack[--this.top];
	}
}