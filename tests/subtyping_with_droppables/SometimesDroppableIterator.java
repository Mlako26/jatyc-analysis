import jatyc.lib.Typestate;
import jatyc.lib.Nullable;

@Typestate("SometimesDroppableIterator")
public class SometimesDroppableIterator extends Iterator {
  private int[] items;
  protected int index;

  public SometimesDroppableIterator(int[] items) {
    super(items);
    this.items = items;
    this.index = 0;
  }

  public void doNothing() {return;}
}
