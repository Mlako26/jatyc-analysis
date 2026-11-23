import jatyc.lib.*;

public class ClientCode {
  public static void main(String[] args) {
    Object[] arr = {"Hello, World!"};
    LoopIterator it = new LoopIterator(arr);
    while (it.hasNext()) {
			it.next();
		}
		it.next();
	}
}
