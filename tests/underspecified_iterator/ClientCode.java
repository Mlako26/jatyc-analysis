import jatyc.lib.*;

public class ClientCode {
    public static void main(String[] args) {
		int[] array = {2,3};
        UnderspecifiedIterator it = new UnderspecifiedIterator(array);
        while (true) {
			it.next();
		}
	}
}
