import jatyc.lib.*;

public class ClientCode2 {
    public static void main(String[] args) {
        BaseIterator it = new LoopIterator(args);
        while (it.hasNext()) {
			it.next();
		}
		it.next();
	}
}
