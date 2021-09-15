package javax.imageio;

import java.io.IOException;

public class IIOException extends IOException {
   public IIOException(String var1) {
      super(var1);
   }

   public IIOException(String var1, Throwable var2) {
      super(var1);
      this.initCause(var2);
   }
}
