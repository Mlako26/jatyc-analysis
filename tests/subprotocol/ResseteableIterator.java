import java.util.*;
import jatyc.lib.Typestate;
import jatyc.lib.Nullable;

@Typestate("ResseteableIterator")
public class ResseteableIterator extends BaseIterator {
    protected List<Object> items;

    public ResseteableIterator(String[] items) {
        super(items);
        this.items = Util.toList(items);
    }

    public @Nullable Object next() { 
        return this.items.get(this.index++); 
    }

    public boolean hasNext() { return this.index < this.items.size(); }
    public void print() {System.out.printf("%d",this.index);}
}
