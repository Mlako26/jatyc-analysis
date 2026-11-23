import jatyc.lib.*;

@Typestate("DroppedList")
public class DroppedList {
  private String s;

  public DroppedList(String s) { this.s = s; }

  @SuppressWarnings("all")
  public static DroppedList of(String s) {
    return new DroppedList(s);
  }

  public void add(String s) { return; }
  public String get() { return this.s; }
}

