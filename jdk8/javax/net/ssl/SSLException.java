package javax.net.ssl;

import java.io.IOException;

public class SSLException extends IOException {
   private static final long serialVersionUID = 4511006460650708967L;

   public SSLException(String var1) {
      super(var1);
   }

   public SSLException(String var1, Throwable var2) {
      super(var1);
      this.initCause(var2);
   }

   public SSLException(Throwable var1) {
      super(var1 == null ? null : var1.toString());
      this.initCause(var1);
   }
}
