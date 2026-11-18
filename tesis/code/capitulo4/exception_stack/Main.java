public class Main {
  public static void main(String args[]) throws Exception {
    Stack stack = new Stack(10);
    try {
      stack.pop();
    } catch (InvalidOperationException err) {
      // Hacer algo con el stack en su nuevo typestate
    }
  }
}
