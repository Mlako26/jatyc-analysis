public class Main {
    public static void main(String args[]) throws Exception {
      Stack stack = new Stack(5);
      if (stack.canPushFloat()) {
        stack.push((float) 3.4);
      }

      if (stack.canPushInt()) {
        stack.push(3);
      }

      if (stack.canPushInt()) {
        stack.push((float) 3.4);
      }

      if (stack.canPushFloat()) {
        stack.push(3);
      }
    }
}
