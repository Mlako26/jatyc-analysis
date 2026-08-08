import java.util.*;
import jatyc.lib.Typestate;
import jatyc.lib.Nullable;

@Typestate("MoveToEndIterator")
public class MoveToEndIterator {
    private String[] items;
    private int index;

    public MoveToEndIterator(String[] items) {
        this.items = items;
        this.index = 0;
    }
    public boolean hasNext() {
        return this.index < this.items.length;
    }
    public @Nullable String next() { return this.items[this.index++]; }
    public void moveToEnd() {this.index = this.items.length;}
    public void print() {
        System.out.printf("%d",this.index);
    }
}
