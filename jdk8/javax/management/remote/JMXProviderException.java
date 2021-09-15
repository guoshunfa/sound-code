package javax.management.remote;

import java.io.IOException;

public class JMXProviderException extends IOException {
   private static final long serialVersionUID = -3166703627550447198L;
   private Throwable cause = null;

   public JMXProviderException() {
   }

   public JMXProviderException(String var1) {
      super(var1);
   }

   public JMXProviderException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   public Throwable getCause() {
      return this.cause;
   }
}
