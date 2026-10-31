import jatyc.lib.*;

@Typestate("StackProtocol")
public class Stack {
  private int size;
  private int[] stack;
	private int top;

	public Stack(int size) {
    this.size = size;
		this.stack = new int[size];
		this.top = 0;
  }

	public boolean isFull() {
    return this.top == size;
  }

	public boolean isEmpty() {...}
	public void push(int newValue) {...}
	public int pop() {...}
}
