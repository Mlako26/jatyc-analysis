import jatyc.lib.Typestate;
import jatyc.lib.Ensures;

@Typestate("DroppedList")
public class DroppedList {
    private String s;

    private DroppedList(String s) {
        this.s = s;
    }

    @SuppressWarnings("all")
    public static DroppedList of(String s) {
        return new DroppedList(s);
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
// DroppedList.java:15: warning: Object [new DroppedList] with type State{DroppedList, Mutable} will be dropped
//         return new DroppedList(s);
//         ^
// ClientCode.java:6: error: Cannot call [add] on Shared{DroppedList}
//                 list.add("bar");
//                         ^
// 1 error
// 1 warning
// ```

// We get one warning which basically tells us that we are returning a protocoled object but will be dropped instead
// (since we are not using the Ensures annotation). This is a fine warning, and we will omit it with the surpress annotation.
// Then, the error gives us what we want, a cannot call add on shared{list} error.
// It is important to note that in the protocol we HAD to include the add method, otherwise it would've been an
// anytime method, and it potentially could've been called on the shared variable.
// Also notice how other operations, such as get(), since they are not mentioned in the protocol they can still
// be used from shared variables.
