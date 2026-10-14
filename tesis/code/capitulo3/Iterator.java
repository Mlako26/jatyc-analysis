import jatyc.lib.Typestate;

@Typestate("Iterator")
public interface Iterator {
  public boolean hasNext();
  public int next();
}
