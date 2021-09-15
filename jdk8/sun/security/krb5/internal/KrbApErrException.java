package sun.security.krb5.internal;

import sun.security.krb5.KrbException;

public class KrbApErrException extends KrbException {
   private static final long serialVersionUID = 7545264413323118315L;

   public KrbApErrException(int var1) {
      super(var1);
   }

   public KrbApErrException(int var1, String var2) {
      super(var1, var2);
   }
}
