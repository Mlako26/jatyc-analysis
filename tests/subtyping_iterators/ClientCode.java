import jatyc.lib.*;

public class ClientCode {
    public static void main(String[] args) {
		int[] array = {2,3};
        BaseIterator it = new BaseIterator(array);
        while (it.hasNext()) {
			it.next();
		}
	}
}
