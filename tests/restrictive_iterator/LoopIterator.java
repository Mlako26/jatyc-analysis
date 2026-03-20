import java.util.*;
import jatyc.lib.Typestate;
import jatyc.lib.Nullable;

@Typestate("LoopIterator")
public class LoopIterator extends BaseIterator {
    protected List<Object> items;

    public LoopIterator(String[] items) {
        super(items);
        this.items = Util.toList(items);
    }

    public @Nullable Object next() { 
        if (this.index == this.items.size()) {
          this.index = 0;
        }
        return this.items.get(this.index++); 
    }

    public boolean hasNext() { return this.index < this.items.size(); }
    public void remove() { this.items.remove(--this.index); }
    public int remainingItems() { return this.items.size() - this.index; }
}
