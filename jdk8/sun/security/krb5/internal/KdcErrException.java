package sun.security.krb5.internal;

import sun.security.krb5.KrbException;

public class KdcErrException extends KrbException {
   private static final long serialVersionUID = -8788186031117310306L;

   public KdcErrException(int var1) {
      super(var1);
   }

   public KdcErrException(int var1, String var2) {
      super(var1, var2);
   }
}
