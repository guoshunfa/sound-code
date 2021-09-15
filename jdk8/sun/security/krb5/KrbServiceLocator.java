package sun.security.krb5;

import java.net.SocketPermission;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.spi.NamingManager;
import sun.security.krb5.internal.Krb5;

class KrbServiceLocator {
   private static final String SRV_RR = "SRV";
   private static final String[] SRV_RR_ATTR = new String[]{"SRV"};
   private static final String SRV_TXT = "TXT";
   private static final String[] SRV_TXT_ATTR = new String[]{"TXT"};
   private static final Random random = new Random();
   private static final boolean DEBUG;

   private KrbServiceLocator() {
   }

   static String[] getKerberosService(String var0) {
      String var1 = "dns:///_kerberos." + var0;
      String[] var2 = null;

      try {
         Context var3 = NamingManager.getURLContext("dns", new Hashtable(0));
         if (!(var3 instanceof DirContext)) {
            return null;
         }

         Attributes var4 = null;

         try {
            var4 = (Attributes)AccessController.doPrivileged((PrivilegedExceptionAction)(() -> {
               return ((DirContext)var3).getAttributes(var1, SRV_TXT_ATTR);
            }), (AccessControlContext)null, new SocketPermission("*", "connect,accept"));
         } catch (PrivilegedActionException var13) {
            throw (NamingException)var13.getCause();
         }

         Attribute var5;
         if (var4 != null && (var5 = var4.get("TXT")) != null) {
            int var6 = var5.size();
            boolean var7 = false;
            String[] var8 = new String[var6];
            int var9 = 0;

            int var10;
            for(var10 = 0; var9 < var6; ++var9) {
               try {
                  var8[var10] = (String)var5.get(var9);
                  ++var10;
               } catch (Exception var12) {
               }
            }

            if (var10 < var6) {
               String[] var11 = new String[var10];
               System.arraycopy(var8, 0, var11, 0, var10);
               var2 = var11;
            } else {
               var2 = var8;
            }
         }
      } catch (NamingException var14) {
      }

      return var2;
   }

   static String[] getKerberosService(String var0, String var1) {
      String var2 = "dns:///_kerberos." + var1 + "." + var0;
      String[] var3 = null;

      try {
         Context var4 = NamingManager.getURLContext("dns", new Hashtable(0));
         if (!(var4 instanceof DirContext)) {
            return null;
         }

         Attributes var5 = null;

         try {
            var5 = (Attributes)AccessController.doPrivileged((PrivilegedExceptionAction)(() -> {
               return ((DirContext)var4).getAttributes(var2, SRV_RR_ATTR);
            }), (AccessControlContext)null, new SocketPermission("*", "connect,accept"));
         } catch (PrivilegedActionException var14) {
            throw (NamingException)var14.getCause();
         }

         Attribute var6;
         if (var5 != null && (var6 = var5.get("SRV")) != null) {
            int var7 = var6.size();
            boolean var8 = false;
            KrbServiceLocator.SrvRecord[] var9 = new KrbServiceLocator.SrvRecord[var7];
            int var10 = 0;

            int var11;
            for(var11 = 0; var10 < var7; ++var10) {
               try {
                  var9[var11] = new KrbServiceLocator.SrvRecord((String)var6.get(var10));
                  ++var11;
               } catch (Exception var13) {
               }
            }

            if (var11 < var7) {
               KrbServiceLocator.SrvRecord[] var12 = new KrbServiceLocator.SrvRecord[var11];
               System.arraycopy(var9, 0, var12, 0, var11);
               var9 = var12;
            }

            if (var11 > 1) {
               Arrays.sort((Object[])var9);
            }

            var3 = extractHostports(var9);
         }
      } catch (NamingException var15) {
      }

      return var3;
   }

   private static String[] extractHostports(KrbServiceLocator.SrvRecord[] var0) {
      String[] var1 = null;
      boolean var2 = false;
      boolean var3 = false;
      boolean var4 = false;
      int var5 = 0;

      for(int var6 = 0; var6 < var0.length; ++var6) {
         if (var1 == null) {
            var1 = new String[var0.length];
         }

         int var8;
         for(var8 = var6; var6 < var0.length - 1 && var0[var6].priority == var0[var6 + 1].priority; ++var6) {
         }

         int var9 = var6;
         int var10 = var6 - var8 + 1;

         for(int var7 = 0; var7 < var10; ++var7) {
            var1[var5++] = selectHostport(var0, var8, var9);
         }
      }

      return var1;
   }

   private static String selectHostport(KrbServiceLocator.SrvRecord[] var0, int var1, int var2) {
      if (var1 == var2) {
         return var0[var1].hostport;
      } else {
         int var3 = 0;

         for(int var4 = var1; var4 <= var2; ++var4) {
            if (var0[var4] != null) {
               var3 += var0[var4].weight;
               var0[var4].sum = var3;
            }
         }

         String var7 = null;
         int var5 = var3 == 0 ? 0 : random.nextInt(var3 + 1);

         for(int var6 = var1; var6 <= var2; ++var6) {
            if (var0[var6] != null && var0[var6].sum >= var5) {
               var7 = var0[var6].hostport;
               var0[var6] = null;
               break;
            }
         }

         return var7;
      }
   }

   static {
      DEBUG = Krb5.DEBUG;
   }

   static class SrvRecord implements Comparable<KrbServiceLocator.SrvRecord> {
      int priority;
      int weight;
      int sum;
      String hostport;

      SrvRecord(String var1) throws Exception {
         StringTokenizer var2 = new StringTokenizer(var1, " ");
         if (var2.countTokens() == 4) {
            this.priority = Integer.parseInt(var2.nextToken());
            this.weight = Integer.parseInt(var2.nextToken());
            String var3 = var2.nextToken();
            this.hostport = var2.nextToken() + ":" + var3;
         } else {
            throw new IllegalArgumentException();
         }
      }

      public int compareTo(KrbServiceLocator.SrvRecord var1) {
         if (this.priority > var1.priority) {
            return 1;
         } else if (this.priority < var1.priority) {
            return -1;
         } else if (this.weight == 0 && var1.weight != 0) {
            return -1;
         } else {
            return this.weight != 0 && var1.weight == 0 ? 1 : 0;
         }
      }
   }
}
