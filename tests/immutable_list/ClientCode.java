import jatyc.lib.*;

public class ClientCode {
    // public static void main(String[] args) {
	// 	DroppedList list = DroppedList.of("foo");
	// 	DroppedList list2 = new DroppedList("foo");
	// 	String s = list.get();
	// 	list.add("bar");
	// 	s = list2.get();
	// 	list2.add("bar");
	// }

	public static void main(String[] args) {
		ImmutableList list = ImmutableList.of("foo");
		ImmutableList list2 = new ImmutableList("foo");
		String s = list.get();
		list.add("bar");
		s = list2.get();
		list2.add("bar");
	}
}
