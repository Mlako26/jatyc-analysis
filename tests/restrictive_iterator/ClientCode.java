import jatyc.lib.*;

public class ClientCode {
    public static void main(String[] args) {
        LoopIterator it = new LoopIterator(args);
        while (it.hasNext()) {
			it.next();
		}
		it.next();
	}
}
