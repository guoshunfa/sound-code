package sun.security.krb5.internal.crypto;

import sun.security.krb5.Confounder;

public class Nonce {
   public static synchronized int value() {
      return Confounder.intValue() & Integer.MAX_VALUE;
   }
}
