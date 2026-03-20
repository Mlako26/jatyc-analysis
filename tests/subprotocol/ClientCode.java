import jatyc.lib.*;

public class ClientCode {
    public static void main(String[] args) {
		String[] array = {"hello", "world"};
        ResseteableIterator it = new ResseteableIterator(array);
        while (it.hasNext()) {
			it.next();
		}

		if (!it.hasNext()) {
			it.next();
		}
	}
}
