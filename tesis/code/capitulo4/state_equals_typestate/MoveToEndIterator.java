import jatyc.lib.*;

@Typestate("MoveToEndIterator")
public class MoveToEndIterator {
    private Object[] items;
    private int index;

    public MoveToEndIterator(Object[] items) {
        this.items = items;
        this.index = 0;
    }

    public boolean hasNext() {return this.index < this.items.length;}
    public @Nullable Object next() { return this.items[this.index++];}
    public void moveToEnd() {this.index = this.items.length;}
}
