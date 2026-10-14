public class Main {
  public static void main(String args[]) throws Exception {
    Stack s1 = new Stack(5);
    Stack s2 = s1;
    s2.push(5);
    s1.push(5);
  }
}
