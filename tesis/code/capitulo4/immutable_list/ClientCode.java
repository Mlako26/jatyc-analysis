import jatyc.lib.*;

public class ClientCode {
  public static void main(String[] args) {
		DroppedList list = DroppedList.of("foo");
		DroppedList list2 = new DroppedList("foo");
		String s = list.get();
		list.add("bar");
		s = list2.get();
		list2.add("bar");
	}
}
