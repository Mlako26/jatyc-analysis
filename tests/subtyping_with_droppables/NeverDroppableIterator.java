import jatyc.lib.Typestate;
import jatyc.lib.Nullable;

@Typestate("NeverDroppableIterator")
public class NeverDroppableIterator extends Iterator {
  private int[] items;
  protected int index;

  public NeverDroppableIterator(int[] items) {
    super(items);
    this.items = items;
    this.index = 0;
  }
}
