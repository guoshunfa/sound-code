package sun.security.krb5;

import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.SeqNumber;

abstract class KrbAppMessage {
   private static boolean DEBUG;

   void check(KerberosTime var1, Integer var2, Integer var3, HostAddress var4, HostAddress var5, SeqNumber var6, HostAddress var7, HostAddress var8, boolean var9, boolean var10, PrincipalName var11) throws KrbApErrException {
      if (var7 != null && (var4 == null || var7 == null || !var4.equals(var7))) {
         if (DEBUG && var4 == null) {
            System.out.println("packetSAddress is null");
         }

         if (DEBUG && var7 == null) {
            System.out.println("sAddress is null");
         }

         throw new KrbApErrException(38);
      } else if (var8 == null || var5 != null && var8 != null && var5.equals(var8)) {
         if (var1 != null) {
            if (var2 != null) {
               var1 = var1.withMicroSeconds(var2);
            }

            if (!var1.inClockSkew()) {
               throw new KrbApErrException(37);
            }
         } else if (var9) {
            throw new KrbApErrException(37);
         }

         if (var6 == null && var10) {
            throw new KrbApErrException(400);
         } else {
            if (var3 != null && var6 != null) {
               if (var3 != var6.current()) {
                  throw new KrbApErrException(42);
               }

               var6.step();
            } else if (var10) {
               throw new KrbApErrException(42);
            }

            if (var1 == null && var3 == null) {
               throw new KrbApErrException(41);
            }
         }
      } else {
         throw new KrbApErrException(38);
      }
   }

   static {
      DEBUG = Krb5.DEBUG;
   }
}
