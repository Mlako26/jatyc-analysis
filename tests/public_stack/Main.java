public class Main {
	public static void main(String args[]) throws Exception {
		Stack stack = new Stack(1);
		stack.size = 2; 
		while (!stack.isFull()) {
			stack.push(2);
		}
	}
}
