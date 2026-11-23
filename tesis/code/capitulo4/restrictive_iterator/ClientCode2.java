import jatyc.lib.*;

public class ClientCode2 {
	public static void main(String[] args) {
    Object[] arr = {"Hello, World!"};
		Iterator it = new LoopIterator(arr);
		while (it.hasNext()) {
			it.next();
		}
		it.next();
	}
}
