import jatyc.lib.Typestate;

@Typestate("ListIterator")
public interface ListIterator extends Iterator {
  public void add(int element);
  public void set(int element);
}
