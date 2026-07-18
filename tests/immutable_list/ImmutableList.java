import jatyc.lib.Typestate;
import jatyc.lib.Ensures;

@Typestate("ImmutableList")
public class ImmutableList {
    private String s;

    public ImmutableList(String s) {
        this.s = s;
    }

    @SuppressWarnings("all")
    public @Ensures("Immutable")static ImmutableList of(String s) {
        ImmutableList list= new ImmutableList(s);
        list.immutable();
        return list;
    }

    public void add(String s) {
        // do something
        return;
    }

    public void immutable() {
        return;
    }

    public String get() {
        return this.s;
    }
}
