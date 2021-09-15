package java.security.cert;

import java.security.GeneralSecurityException;

public class CRLException extends GeneralSecurityException {
   private static final long serialVersionUID = -6694728944094197147L;

   public CRLException() {
   }

   public CRLException(String var1) {
      super(var1);
   }

   public CRLException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public CRLException(Throwable var1) {
      super(var1);
   }
}
