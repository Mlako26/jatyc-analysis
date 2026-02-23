import java.io.FileReader;
import java.io.IOException;
import jatyc.lib.Typestate;
import jatyc.lib.Nullable;

@Typestate("LineReader")
public class LineReader {
  private @Nullable FileReader file;
  private int curr;

  public LineReader() {
    this.file = null;
    this.curr = 0;
  }

  public Status open(String filename) {
    try {
      file = new FileReader(filename);
      curr = file.read();
      return Status.OK;
    } catch (IOException exp) {
      return Status.ERROR;
    }
  }

  public String read() throws IOException {
    StringBuilder str = new StringBuilder();

    while (curr != 10 && curr != -1) {
      str.append((char) curr);
      curr = file.read();
    }

    if (curr == 10) {
      curr = file.read();
    }

    return str.toString();
  }

  public boolean eof() {
    return curr == -1;
  }

  public void close() throws IOException {
    file.close();
  }

  // public int countLines(String filename) throws IOException { // Anytime method follows protocol?
  //   switch (this.open(filename)) {
  //     case OK:
  //       int counter = 0;
  //       while (!this.eof()) {
  //         this.read();
  //         counter++;
  //       }
  //       this.close();
  //       return counter;
  //     default:
  //       System.err.println("Could not open file");
  //       return -1;
  //   }
  // }

  /**
   * This returns:
   * LineReader.java:53: error: Cannot call own public method [eof]
            while (!this.eof()) {
                              ^
      LineReader.java:54: error: Cannot call own public method [read]
                this.read();
                        ^
      LineReader.java:50: error: Cannot call own public method [open]
          switch (this.open(filename)) {
                          ^
      LineReader.java:57: error: Cannot call own public method [close]
              this.close();
                        ^
      4 errors

   */

  // Seems like it does not like doing so in anytime methods, I need to try within a method from my protocol
}
