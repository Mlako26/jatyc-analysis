import jatyc.lib.*;

@Typestate("StackProtocol")
public class Stack {
  private int[] stack;
	private int top;
  public int size;

	public Stack(int size) {
    this.size = size;
		this.stack = new int[size];
		this.top = 0;
	}

	public boolean isEmpty() {
    return this.top == 0;
  }

	public boolean isFull() {
    return this.top == this.size;
  }

	public void push(int newValue) {
		this.stack[this.top++] = newValue;
	}

	public int pop() {
		return this.stack[--this.top];
	}
}