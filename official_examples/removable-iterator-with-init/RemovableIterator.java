import java.util.*;
import jatyc.lib.Typestate;
import jatyc.lib.Nullable;

@Typestate("RemovableIterator")
public class RemovableIterator extends BaseIterator {
  protected @Nullable List<Object> items;

  public RemovableIterator() {
    super();
    this.items = null;
  }

  public void init(String[] items) {
    this.items = Util.toList(items);
  }

  public boolean hasNext() {
    if (this.items != null) {
      return this.index < this.items.size();
    }
    throw new RuntimeException();
  }

  public @Nullable Object next() {
    if (this.items != null) {
      return this.items.get(this.index++);
    }
    throw new RuntimeException();
  }

  public void remove() {
    if (this.items != null) {
      this.items.remove(--this.index);
    }
    throw new RuntimeException();
  }
}
