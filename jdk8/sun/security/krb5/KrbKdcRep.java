package sun.security.krb5;

import sun.security.krb5.internal.KDCRep;
import sun.security.krb5.internal.KDCReq;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbApErrException;

abstract class KrbKdcRep {
   static void check(boolean var0, KDCReq var1, KDCRep var2) throws KrbApErrException {
      if (var0 && !var1.reqBody.cname.equals(var2.cname)) {
         var2.encKDCRepPart.key.destroy();
         throw new KrbApErrException(41);
      } else if (!var1.reqBody.sname.equals(var2.encKDCRepPart.sname)) {
         var2.encKDCRepPart.key.destroy();
         throw new KrbApErrException(41);
      } else if (var1.reqBody.getNonce() != var2.encKDCRepPart.nonce) {
         var2.encKDCRepPart.key.destroy();
         throw new KrbApErrException(41);
      } else if (var1.reqBody.addresses != null && var2.encKDCRepPart.caddr != null && !var1.reqBody.addresses.equals(var2.encKDCRepPart.caddr)) {
         var2.encKDCRepPart.key.destroy();
         throw new KrbApErrException(41);
      } else {
         for(int var3 = 2; var3 < 6; ++var3) {
            if (var1.reqBody.kdcOptions.get(var3) != var2.encKDCRepPart.flags.get(var3)) {
               if (Krb5.DEBUG) {
                  System.out.println("> KrbKdcRep.check: at #" + var3 + ". request for " + var1.reqBody.kdcOptions.get(var3) + ", received " + var2.encKDCRepPart.flags.get(var3));
               }

               throw new KrbApErrException(41);
            }
         }

         if (var1.reqBody.kdcOptions.get(8) != var2.encKDCRepPart.flags.get(8)) {
            throw new KrbApErrException(41);
         } else if ((var1.reqBody.from == null || var1.reqBody.from.isZero()) && var2.encKDCRepPart.starttime != null && !var2.encKDCRepPart.starttime.inClockSkew()) {
            var2.encKDCRepPart.key.destroy();
            throw new KrbApErrException(37);
         } else if (var1.reqBody.from != null && !var1.reqBody.from.isZero() && var2.encKDCRepPart.starttime != null && !var1.reqBody.from.equals(var2.encKDCRepPart.starttime)) {
            var2.encKDCRepPart.key.destroy();
            throw new KrbApErrException(41);
         } else if (!var1.reqBody.till.isZero() && var2.encKDCRepPart.endtime.greaterThan(var1.reqBody.till)) {
            var2.encKDCRepPart.key.destroy();
            throw new KrbApErrException(41);
         } else if (var1.reqBody.kdcOptions.get(8) && var1.reqBody.rtime != null && !var1.reqBody.rtime.isZero() && (var2.encKDCRepPart.renewTill == null || var2.encKDCRepPart.renewTill.greaterThan(var1.reqBody.rtime))) {
            var2.encKDCRepPart.key.destroy();
            throw new KrbApErrException(41);
         } else if (var1.reqBody.kdcOptions.get(27) && var2.encKDCRepPart.flags.get(8) && !var1.reqBody.till.isZero() && (var2.encKDCRepPart.renewTill == null || var2.encKDCRepPart.renewTill.greaterThan(var1.reqBody.till))) {
            var2.encKDCRepPart.key.destroy();
            throw new KrbApErrException(41);
         }
      }
   }
}
