import jatyc.lib.Typestate;
import jatyc.lib.Nullable;

@Typestate("Iterator")
public class Iterator {
  private int[] items;
  protected int index;

  public Iterator(int[] items) {
    this.items = items;
    this.index = 0;
  }
  public boolean hasNext() {
    return this.index < this.items.length;
  }
  public @Nullable int next() { return this.items[this.index++]; }
}
