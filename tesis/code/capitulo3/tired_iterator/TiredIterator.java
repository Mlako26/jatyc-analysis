import java.util.*;
import jatyc.lib.Typestate;
import jatyc.lib.Nullable;

@Typestate("TiredIterator")
public class TiredIterator implements Iterator {
    private Object[] items;
    private int index;

    public TiredIterator(Object[] items) {
        this.items = items;
        this.index = 0;
    }

    public @Nullable Object next() throws RuntimeException { 
        return this.items[this.index++];
    }

    public boolean hasNext() { return this.index < this.items.length; }
    public void rest() {return;}
}
