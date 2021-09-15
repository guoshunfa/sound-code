package javax.management.remote;

import java.io.IOException;

public class JMXServerErrorException extends IOException {
   private static final long serialVersionUID = 3996732239558744666L;
   private final Error cause;

   public JMXServerErrorException(String var1, Error var2) {
      super(var1);
      this.cause = var2;
   }

   public Throwable getCause() {
      return this.cause;
   }
}
