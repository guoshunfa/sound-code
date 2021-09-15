package sun.security.krb5.internal;

import sun.security.krb5.KrbException;

public class KrbErrException extends KrbException {
   private static final long serialVersionUID = 2186533836785448317L;

   public KrbErrException(int var1) {
      super(var1);
   }

   public KrbErrException(int var1, String var2) {
      super(var1, var2);
   }
}
