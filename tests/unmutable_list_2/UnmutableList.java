import jatyc.lib.Typestate;
import jatyc.lib.Ensures;

@Typestate("UnmutableList")
public class UnmutableList {
    private String s;

    private UnmutableList(String s) {
        this.s = s;
    }

    @SuppressWarnings("all")
    @Ensures("Unmutable") public static UnmutableList of(String s) {
        UnmutableList u = new UnmutableList(s);
        u.unmutable();
        return u;
    }

    public void unmutable() {
        return;
    }

    public void add(String s) {
        // do something
        return;
    }

    public String get() {
        return this.s;
    }
}

// ```
// ClientCode.java:15: error: Cannot call [add] on State{UnmutableList, Unmutable}
//                         list2.add("pepe");
//                                  ^
// ClientCode.java:13: error: Cannot call [unmutable] on State{UnmutableList, Unmutable}
//                         list2.unmutable();
//                                        ^
// ClientCode.java:7: error: Cannot call [add] on Shared{DroppedList}
//                 list.add("bar");
//                         ^
// 3 errors
// ```

// Good! Even though the implementation is not clean at all, since we are making a public method just to change the state
// of the object and does nothing else, this method can no longer be called from the Unmutable state, and the static class method
// ensures that the object returned is unmutable.