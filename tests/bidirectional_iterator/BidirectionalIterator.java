import java.util.*;
import jatyc.lib.Typestate;
import jatyc.lib.Nullable;

@Typestate("BidirectionalIterator")
public class BidirectionalIterator implements Iterator {
    private Object[] items;
    private int index;

    public BidirectionalIterator(Object[] items) {
        this.items = items;
        this.index = 0;
    }

    public @Nullable Object next() { return this.items[this.index++]; }
    public @Nullable Object prev() { return this.items[this.index--]; }

    public boolean hasNext() { return this.index < this.items.length; }
    public boolean hasPrev() { return this.index > 0  && this.items.length > 0; }

}
