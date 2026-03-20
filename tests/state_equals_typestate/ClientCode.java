import jatyc.lib.*;

public class ClientCode {
    public static void main(String[] args) {
		String[] array = {"hello", "world"};
        ResseteableIterator it = new ResseteableIterator(array);
        if (it.hasNext()) {
			it.next();
		}
		it.print();
		it.reset();
		it.print();
	}
}
