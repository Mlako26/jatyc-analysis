import jatyc.lib.*;

public class ClientCode {
    public static void main(String[] args) {
		Object[] array = {2,3};
        Iterator it = new SafeIterator(array);
        while (it.hasNext()) {
			it.next();
		}
	}
}
