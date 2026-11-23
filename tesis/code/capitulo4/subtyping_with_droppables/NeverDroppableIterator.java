import jatyc.lib.Typestate;
import jatyc.lib.Nullable;

@Typestate("NeverDroppableIterator")
public class NeverDroppableIterator implements Iterator {
  private Object[] items;
  protected int index;

  public NeverDroppableIterator(Object[] items) {
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
