import java.util.*;
import jatyc.lib.Typestate;
import jatyc.lib.Nullable;

public class UnderspecifiedIterator {
  private int[] items;
  protected int index;

  public UnderspecifiedIterator(int[] items) {
    this.items = items;
    this.index = 0;
  }
  public boolean hasNext() {
    return this.index < this.items.length;
  }
  public @Nullable int next() { return this.items[this.index++]; }
  public int remainingItems() { return this.items.length - this.index; }
}
