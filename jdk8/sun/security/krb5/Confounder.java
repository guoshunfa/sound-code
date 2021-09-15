package sun.security.krb5;

import java.security.SecureRandom;

public final class Confounder {
   private static SecureRandom srand = new SecureRandom();

   private Confounder() {
   }

   public static byte[] bytes(int var0) {
      byte[] var1 = new byte[var0];
      srand.nextBytes(var1);
      return var1;
   }

   public static int intValue() {
      return srand.nextInt();
   }

   public static long longValue() {
      return srand.nextLong();
   }
}
