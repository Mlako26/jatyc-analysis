import jatyc.lib.*;

public class ClientCode {
    public static void main(String[] args) {
		DroppedList list = DroppedList.of("foo");
		String s = list.get();
		list.add("bar");

		UnmutableList list2 = UnmutableList.of("foo");
		list2.get();
		boolean b = true;
		if (b) {
			list2.unmutable();
		} else {
			list2.add("pepe");
		}
	}
}
