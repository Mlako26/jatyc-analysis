import java.util.*;
import jatyc.lib.Typestate;
import jatyc.lib.Nullable;

@Typestate("LoopIterator")
public class LoopIterator implements Iterator {
    private Object[] items;
    private int index;

    public LoopIterator(Object[] items) {
        this.items = items;
        this.index = 0;
    }

    public @Nullable Object next() { 
        Object ans = this.items[this.index++];
        if (this.index == this.items.length) {
            this.index = 0;
        }
        return ans;
    }

    public boolean hasNext() { return this.index < this.items.length; }
}
