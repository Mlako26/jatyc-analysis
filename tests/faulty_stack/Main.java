public class Main {
    public static void main(String args[]) throws Exception {
        Stack stack = new Stack(1);
        while (!stack.isFull()) {
            stack.push(5);
        }
    }
}
