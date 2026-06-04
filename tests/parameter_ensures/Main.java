import jatyc.lib.Requires;

public class Main {
    public static void main(String args[]) throws Exception {
		  Stack stack = new Stack(5);
      pushToStack(stack, 2);
      if (!stack.isFull()) {
        stack.push(3);
      }
    }

    public static void pushToStack(@Requires("Init") Stack stack, int e) {
      if (!stack.isFull()) {
        stack.push(e);
      }
    }
}
