import java.util.*;
import jatyc.lib.Typestate;
import jatyc.lib.Nullable;

@Typestate("SafeIterator")
public class SafeIterator extends BaseIterator {
    private int[] items;

    public SafeIterator(int[] items) {
        super(items);
        this.items = items;
    }

    public int next() throws RuntimeException { 
        int item = this.items[this.index++];
        if (item % 2 == 1) {
            throw new RuntimeException("Numbers cannot be odd!");
        }
        return item;
    }

    public boolean hasNext() { return this.index < this.items.length; }
    public boolean iAmSure() { return true; }
}
