import jatyc.lib.Typestate;
import jatyc.lib.Nullable;

@Typestate("Iterator")
public interface Iterator {
  public boolean hasNext();
  public @Nullable Object next();
}
