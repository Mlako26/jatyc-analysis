import jatyc.lib.*;

public class ClientCode {
    public static void main(String[] args) {
		String[] array = {"hello", "world"};
        MoveToEndIterator it = new MoveToEndIterator(array);
        if (it.hasNext()) {
			it.moveToEnd();
			it.next();
		}
	}
}
