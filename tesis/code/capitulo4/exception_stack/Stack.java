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

	public boolean isEmpty() {return this.top == 0;}
	public boolean isFull() {return this.top == this.size;}

	public void push(int newValue) throws InvalidOperationException {
    if (this.top == this.size) {
      throw new InvalidOperationException();
    }
		this.stack[this.top++] = newValue;
	}

	public int pop() throws InvalidOperationException {
    if (this.top == 0) {
      throw new InvalidOperationException();
    }
		return this.stack[--this.top];
	}
}
