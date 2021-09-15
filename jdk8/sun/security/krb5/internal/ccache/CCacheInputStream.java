package sun.security.krb5.internal.ccache;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import sun.misc.IOUtils;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.AuthorizationDataEntry;
import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.TicketFlags;
import sun.security.krb5.internal.util.KrbDataInputStream;

public class CCacheInputStream extends KrbDataInputStream implements FileCCacheConstants {
   private static boolean DEBUG;

   public CCacheInputStream(InputStream var1) {
      super(var1);
   }

   public Tag readTag() throws IOException {
      char[] var1 = new char[1024];
      int var3 = -1;
      Integer var5 = null;
      Integer var6 = null;
      int var2 = this.read(2);
      if (var2 < 0) {
         throw new IOException("stop.");
      } else if (var2 > var1.length) {
         throw new IOException("Invalid tag length.");
      } else {
         while(var2 > 0) {
            var3 = this.read(2);
            int var4 = this.read(2);
            switch(var3) {
            case 1:
               var5 = new Integer(this.read(4));
               var6 = new Integer(this.read(4));
            default:
               var2 -= 4 + var4;
            }
         }

         return new Tag(var2, var3, var5, var6);
      }
   }

   public PrincipalName readPrincipal(int var1) throws IOException, RealmException {
      Object var6 = null;
      int var2;
      if (var1 == 1281) {
         var2 = 0;
      } else {
         var2 = this.read(4);
      }

      int var3 = this.readLength4();
      ArrayList var8 = new ArrayList();
      if (var1 == 1281) {
         --var3;
      }

      for(int var9 = 0; var9 <= var3; ++var9) {
         int var4 = this.readLength4();
         byte[] var10 = IOUtils.readFully(this, var4, true);
         var8.add(new String(var10));
      }

      if (var8.isEmpty()) {
         throw new IOException("No realm or principal");
      } else if (this.isRealm((String)var8.get(0))) {
         String var7 = (String)var8.remove(0);
         if (var8.isEmpty()) {
            throw new IOException("No principal name components");
         } else {
            return new PrincipalName(var2, (String[])var8.toArray(new String[var8.size()]), new Realm(var7));
         }
      } else {
         try {
            return new PrincipalName(var2, (String[])var8.toArray(new String[var8.size()]), Realm.getDefault());
         } catch (RealmException var11) {
            return null;
         }
      }
   }

   boolean isRealm(String var1) {
      try {
         new Realm(var1);
      } catch (Exception var5) {
         return false;
      }

      StringTokenizer var2 = new StringTokenizer(var1, ".");

      while(var2.hasMoreTokens()) {
         String var3 = var2.nextToken();

         for(int var4 = 0; var4 < var3.length(); ++var4) {
            if (var3.charAt(var4) >= 141) {
               return false;
            }
         }
      }

      return true;
   }

   EncryptionKey readKey(int var1) throws IOException {
      int var2 = this.read(2);
      if (var1 == 1283) {
         this.read(2);
      }

      int var3 = this.readLength4();
      byte[] var4 = IOUtils.readFully(this, var3, true);
      return new EncryptionKey(var4, var2, new Integer(var1));
   }

   long[] readTimes() throws IOException {
      long[] var1 = new long[]{(long)this.read(4) * 1000L, (long)this.read(4) * 1000L, (long)this.read(4) * 1000L, (long)this.read(4) * 1000L};
      return var1;
   }

   boolean readskey() throws IOException {
      return this.read() != 0;
   }

   HostAddress[] readAddr() throws IOException, KrbApErrException {
      int var1 = this.readLength4();
      if (var1 <= 0) {
         return null;
      } else {
         ArrayList var4 = new ArrayList();

         for(int var5 = 0; var5 < var1; ++var5) {
            int var2 = this.read(2);
            int var3 = this.readLength4();
            if (var3 != 4 && var3 != 16) {
               if (DEBUG) {
                  System.out.println("Incorrect address format.");
               }

               return null;
            }

            byte[] var6 = new byte[var3];

            for(int var7 = 0; var7 < var3; ++var7) {
               var6[var7] = (byte)this.read(1);
            }

            var4.add(new HostAddress(var2, var6));
         }

         return (HostAddress[])var4.toArray(new HostAddress[var4.size()]);
      }
   }

   AuthorizationDataEntry[] readAuth() throws IOException {
      int var1 = this.readLength4();
      if (var1 <= 0) {
         return null;
      } else {
         ArrayList var4 = new ArrayList();
         Object var5 = null;

         for(int var6 = 0; var6 < var1; ++var6) {
            int var2 = this.read(2);
            int var3 = this.readLength4();
            byte[] var7 = IOUtils.readFully(this, var3, true);
            var4.add(new AuthorizationDataEntry(var2, var7));
         }

         return (AuthorizationDataEntry[])var4.toArray(new AuthorizationDataEntry[var4.size()]);
      }
   }

   byte[] readData() throws IOException {
      int var1 = this.readLength4();
      return var1 == 0 ? null : IOUtils.readFully(this, var1, true);
   }

   boolean[] readFlags() throws IOException {
      boolean[] var1 = new boolean[32];
      int var2 = this.read(4);
      if ((var2 & 1073741824) == 1073741824) {
         var1[1] = true;
      }

      if ((var2 & 536870912) == 536870912) {
         var1[2] = true;
      }

      if ((var2 & 268435456) == 268435456) {
         var1[3] = true;
      }

      if ((var2 & 134217728) == 134217728) {
         var1[4] = true;
      }

      if ((var2 & 67108864) == 67108864) {
         var1[5] = true;
      }

      if ((var2 & 33554432) == 33554432) {
         var1[6] = true;
      }

      if ((var2 & 16777216) == 16777216) {
         var1[7] = true;
      }

      if ((var2 & 8388608) == 8388608) {
         var1[8] = true;
      }

      if ((var2 & 4194304) == 4194304) {
         var1[9] = true;
      }

      if ((var2 & 2097152) == 2097152) {
         var1[10] = true;
      }

      if ((var2 & 1048576) == 1048576) {
         var1[11] = true;
      }

      if (DEBUG) {
         String var3 = ">>> CCacheInputStream: readFlags() ";
         if (var1[1]) {
            var3 = var3 + " FORWARDABLE;";
         }

         if (var1[2]) {
            var3 = var3 + " FORWARDED;";
         }

         if (var1[3]) {
            var3 = var3 + " PROXIABLE;";
         }

         if (var1[4]) {
            var3 = var3 + " PROXY;";
         }

         if (var1[5]) {
            var3 = var3 + " MAY_POSTDATE;";
         }

         if (var1[6]) {
            var3 = var3 + " POSTDATED;";
         }

         if (var1[7]) {
            var3 = var3 + " INVALID;";
         }

         if (var1[8]) {
            var3 = var3 + " RENEWABLE;";
         }

         if (var1[9]) {
            var3 = var3 + " INITIAL;";
         }

         if (var1[10]) {
            var3 = var3 + " PRE_AUTH;";
         }

         if (var1[11]) {
            var3 = var3 + " HW_AUTH;";
         }

         System.out.println(var3);
      }

      return var1;
   }

   Credentials readCred(int var1) throws IOException, RealmException, KrbApErrException, Asn1Exception {
      PrincipalName var2 = null;

      try {
         var2 = this.readPrincipal(var1);
      } catch (Exception var22) {
      }

      if (DEBUG) {
         System.out.println(">>>DEBUG <CCacheInputStream>  client principal is " + var2);
      }

      PrincipalName var3 = null;

      try {
         var3 = this.readPrincipal(var1);
      } catch (Exception var21) {
      }

      if (DEBUG) {
         System.out.println(">>>DEBUG <CCacheInputStream> server principal is " + var3);
      }

      EncryptionKey var4 = this.readKey(var1);
      if (DEBUG) {
         System.out.println(">>>DEBUG <CCacheInputStream> key type: " + var4.getEType());
      }

      long[] var5 = this.readTimes();
      KerberosTime var6 = new KerberosTime(var5[0]);
      KerberosTime var7 = var5[1] == 0L ? null : new KerberosTime(var5[1]);
      KerberosTime var8 = new KerberosTime(var5[2]);
      KerberosTime var9 = var5[3] == 0L ? null : new KerberosTime(var5[3]);
      if (DEBUG) {
         System.out.println(">>>DEBUG <CCacheInputStream> auth time: " + var6.toDate().toString());
         System.out.println(">>>DEBUG <CCacheInputStream> start time: " + (var7 == null ? "null" : var7.toDate().toString()));
         System.out.println(">>>DEBUG <CCacheInputStream> end time: " + var8.toDate().toString());
         System.out.println(">>>DEBUG <CCacheInputStream> renew_till time: " + (var9 == null ? "null" : var9.toDate().toString()));
      }

      boolean var10 = this.readskey();
      boolean[] var11 = this.readFlags();
      TicketFlags var12 = new TicketFlags(var11);
      HostAddress[] var13 = this.readAddr();
      HostAddresses var14 = null;
      if (var13 != null) {
         var14 = new HostAddresses(var13);
      }

      AuthorizationDataEntry[] var15 = this.readAuth();
      AuthorizationData var16 = null;
      if (var15 != null) {
         var16 = new AuthorizationData(var15);
      }

      byte[] var17 = this.readData();
      byte[] var18 = this.readData();
      if (var2 != null && var3 != null) {
         try {
            return new Credentials(var2, var3, var4, var6, var7, var8, var9, var10, var12, var14, var16, var17 != null ? new Ticket(var17) : null, var18 != null ? new Ticket(var18) : null);
         } catch (Exception var20) {
            return null;
         }
      } else {
         return null;
      }
   }

   static {
      DEBUG = Krb5.DEBUG;
   }
}
