import jatyc.lib.*;

@Typestate("UnderspecifiedIterator")
public class UnderspecifiedIterator {
  private int[] items;
  protected int index;

  public UnderspecifiedIterator(int[] items) {
    this.items = items;
    this.index = 0;
  }

  public boolean hasNext() {return this.index < this.items.length;}
  public @Nullable int next() {return this.items[this.index++];}
}
