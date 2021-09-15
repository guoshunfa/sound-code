package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Credentials;
import sun.security.krb5.KrbException;
import sun.security.krb5.KrbTgsReq;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;

public class CredentialsUtil {
   private static boolean DEBUG;

   public static Credentials acquireS4U2selfCreds(PrincipalName var0, Credentials var1) throws KrbException, IOException {
      String var2 = var0.getRealmString();
      String var3 = var1.getClient().getRealmString();
      if (!var2.equals(var3)) {
         throw new KrbException("Cross realm impersonation not supported");
      } else if (!var1.isForwardable()) {
         throw new KrbException("S4U2self needs a FORWARDABLE ticket");
      } else {
         KrbTgsReq var4 = new KrbTgsReq(var1, var1.getClient(), new PAData(129, (new PAForUserEnc(var0, var1.getSessionKey())).asn1Encode()));
         Credentials var5 = var4.sendAndGetCreds();
         if (!var5.getClient().equals(var0)) {
            throw new KrbException("S4U2self request not honored by KDC");
         } else if (!var5.isForwardable()) {
            throw new KrbException("S4U2self ticket must be FORWARDABLE");
         } else {
            return var5;
         }
      }
   }

   public static Credentials acquireS4U2proxyCreds(String var0, Ticket var1, PrincipalName var2, Credentials var3) throws KrbException, IOException {
      KrbTgsReq var4 = new KrbTgsReq(var3, var1, new PrincipalName(var0));
      Credentials var5 = var4.sendAndGetCreds();
      if (!var5.getClient().equals(var2)) {
         throw new KrbException("S4U2proxy request not honored by KDC");
      } else {
         return var5;
      }
   }

   public static Credentials acquireServiceCreds(String var0, Credentials var1) throws KrbException, IOException {
      PrincipalName var2 = new PrincipalName(var0);
      String var3 = var2.getRealmString();
      String var4 = var1.getClient().getRealmString();
      if (var4.equals(var3)) {
         if (DEBUG) {
            System.out.println(">>> Credentials acquireServiceCreds: same realm");
         }

         return serviceCreds(var2, var1);
      } else {
         Credentials var5 = null;
         boolean[] var6 = new boolean[1];
         Credentials var7 = getTGTforRealm(var4, var3, var1, var6);
         if (var7 != null) {
            if (DEBUG) {
               System.out.println(">>> Credentials acquireServiceCreds: got right tgt");
               System.out.println(">>> Credentials acquireServiceCreds: obtaining service creds for " + var2);
            }

            try {
               var5 = serviceCreds(var2, var7);
            } catch (Exception var9) {
               if (DEBUG) {
                  System.out.println((Object)var9);
               }

               var5 = null;
            }
         }

         if (var5 != null) {
            if (DEBUG) {
               System.out.println(">>> Credentials acquireServiceCreds: returning creds:");
               Credentials.printDebug(var5);
            }

            if (!var6[0]) {
               var5.resetDelegate();
            }

            return var5;
         } else {
            throw new KrbApErrException(63, "No service creds");
         }
      }
   }

   private static Credentials getTGTforRealm(String var0, String var1, Credentials var2, boolean[] var3) throws KrbException {
      String[] var4 = Realm.getRealmsList(var0, var1);
      boolean var5 = false;
      boolean var6 = false;
      Credentials var7 = null;
      Credentials var8 = null;
      Credentials var9 = null;
      PrincipalName var10 = null;
      String var11 = null;
      var3[0] = true;
      var7 = var2;
      int var15 = 0;

      while(var15 < var4.length) {
         var10 = PrincipalName.tgsService(var1, var4[var15]);
         if (DEBUG) {
            System.out.println(">>> Credentials acquireServiceCreds: main loop: [" + var15 + "] tempService=" + var10);
         }

         try {
            var8 = serviceCreds(var10, var7);
         } catch (Exception var14) {
            var8 = null;
         }

         int var16;
         if (var8 == null) {
            if (DEBUG) {
               System.out.println(">>> Credentials acquireServiceCreds: no tgt; searching thru capath");
            }

            var8 = null;

            for(var16 = var15 + 1; var8 == null && var16 < var4.length; ++var16) {
               var10 = PrincipalName.tgsService(var4[var16], var4[var15]);
               if (DEBUG) {
                  System.out.println(">>> Credentials acquireServiceCreds: inner loop: [" + var16 + "] tempService=" + var10);
               }

               try {
                  var8 = serviceCreds(var10, var7);
               } catch (Exception var13) {
                  var8 = null;
               }
            }
         }

         if (var8 == null) {
            if (DEBUG) {
               System.out.println(">>> Credentials acquireServiceCreds: no tgt; cannot get creds");
            }
            break;
         }

         var11 = var8.getServer().getInstanceComponent();
         if (var3[0] && !var8.checkDelegate()) {
            if (DEBUG) {
               System.out.println(">>> Credentials acquireServiceCreds: global OK-AS-DELEGATE turned off at " + var8.getServer());
            }

            var3[0] = false;
         }

         if (DEBUG) {
            System.out.println(">>> Credentials acquireServiceCreds: got tgt");
         }

         if (var11.equals(var1)) {
            var9 = var8;
            break;
         }

         for(var16 = var15 + 1; var16 < var4.length && !var11.equals(var4[var16]); ++var16) {
         }

         if (var16 >= var4.length) {
            break;
         }

         var15 = var16;
         var7 = var8;
         if (DEBUG) {
            System.out.println(">>> Credentials acquireServiceCreds: continuing with main loop counter reset to " + var16);
         }
      }

      return var9;
   }

   private static Credentials serviceCreds(PrincipalName var0, Credentials var1) throws KrbException, IOException {
      return (new KrbTgsReq(var1, var0)).sendAndGetCreds();
   }

   static {
      DEBUG = Krb5.DEBUG;
   }
}
