package sun.security.krb5;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.NetClient;

public final class KdcComm {
   private static int defaultKdcRetryLimit;
   private static int defaultKdcTimeout;
   private static int defaultUdpPrefLimit;
   private static final boolean DEBUG;
   private static final String BAD_POLICY_KEY = "krb5.kdc.bad.policy";
   private static int tryLessMaxRetries;
   private static int tryLessTimeout;
   private static KdcComm.BpType badPolicy;
   private String realm;

   public static void initStatic() {
      String var0 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return Security.getProperty("krb5.kdc.bad.policy");
         }
      });
      int var3;
      if (var0 != null) {
         var0 = var0.toLowerCase(Locale.ENGLISH);
         String[] var1 = var0.split(":");
         if ("tryless".equals(var1[0])) {
            if (var1.length > 1) {
               String[] var2 = var1[1].split(",");

               try {
                  var3 = Integer.parseInt(var2[0]);
                  if (var2.length > 1) {
                     tryLessTimeout = Integer.parseInt(var2[1]);
                  }

                  tryLessMaxRetries = var3;
               } catch (NumberFormatException var7) {
                  if (DEBUG) {
                     System.out.println("Invalid krb5.kdc.bad.policy parameter for tryLess: " + var0 + ", use default");
                  }
               }
            }

            badPolicy = KdcComm.BpType.TRY_LESS;
         } else if ("trylast".equals(var1[0])) {
            badPolicy = KdcComm.BpType.TRY_LAST;
         } else {
            badPolicy = KdcComm.BpType.NONE;
         }
      } else {
         badPolicy = KdcComm.BpType.NONE;
      }

      int var8 = -1;
      int var9 = -1;
      var3 = -1;

      try {
         Config var4 = Config.getInstance();
         String var5 = var4.get("libdefaults", "kdc_timeout");
         var8 = parseTimeString(var5);
         var5 = var4.get("libdefaults", "max_retries");
         var9 = parsePositiveIntString(var5);
         var5 = var4.get("libdefaults", "udp_preference_limit");
         var3 = parsePositiveIntString(var5);
      } catch (Exception var6) {
         if (DEBUG) {
            System.out.println("Exception in getting KDC communication settings, using default value " + var6.getMessage());
         }
      }

      defaultKdcTimeout = var8 > 0 ? var8 : 30000;
      defaultKdcRetryLimit = var9 > 0 ? var9 : 3;
      if (var3 < 0) {
         defaultUdpPrefLimit = 1465;
      } else if (var3 > 32700) {
         defaultUdpPrefLimit = 32700;
      } else {
         defaultUdpPrefLimit = var3;
      }

      KdcComm.KdcAccessibility.reset();
   }

   public KdcComm(String var1) throws KrbException {
      if (var1 == null) {
         var1 = Config.getInstance().getDefaultRealm();
         if (var1 == null) {
            throw new KrbException(60, "Cannot find default realm");
         }
      }

      this.realm = var1;
   }

   public byte[] send(byte[] var1) throws IOException, KrbException {
      int var2 = this.getRealmSpecificValue(this.realm, "udp_preference_limit", defaultUdpPrefLimit);
      boolean var3 = var2 > 0 && var1 != null && var1.length > var2;
      return this.send(var1, var3);
   }

   private byte[] send(byte[] var1, boolean var2) throws IOException, KrbException {
      if (var1 == null) {
         return null;
      } else {
         Config var3 = Config.getInstance();
         if (this.realm == null) {
            this.realm = var3.getDefaultRealm();
            if (this.realm == null) {
               throw new KrbException(60, "Cannot find default realm");
            }
         }

         String var4 = var3.getKDCList(this.realm);
         if (var4 == null) {
            throw new KrbException("Cannot get kdc for realm " + this.realm);
         } else {
            Iterator var5 = KdcComm.KdcAccessibility.list(var4).iterator();
            if (!var5.hasNext()) {
               throw new KrbException("Cannot get kdc for realm " + this.realm);
            } else {
               byte[] var6 = null;

               try {
                  var6 = this.sendIfPossible(var1, (String)var5.next(), var2);
               } catch (Exception var11) {
                  boolean var8 = false;

                  while(var5.hasNext()) {
                     try {
                        var6 = this.sendIfPossible(var1, (String)var5.next(), var2);
                        var8 = true;
                        break;
                     } catch (Exception var10) {
                     }
                  }

                  if (!var8) {
                     throw var11;
                  }
               }

               if (var6 == null) {
                  throw new IOException("Cannot get a KDC reply");
               } else {
                  return var6;
               }
            }
         }
      }
   }

   private byte[] sendIfPossible(byte[] var1, String var2, boolean var3) throws IOException, KrbException {
      try {
         byte[] var4 = this.send(var1, var2, var3);
         KRBError var5 = null;

         try {
            var5 = new KRBError(var4);
         } catch (Exception var7) {
         }

         if (var5 != null && var5.getErrorCode() == 52) {
            var4 = this.send(var1, var2, true);
         }

         KdcComm.KdcAccessibility.removeBad(var2);
         return var4;
      } catch (Exception var8) {
         if (DEBUG) {
            System.out.println(">>> KrbKdcReq send: error trying " + var2);
            var8.printStackTrace(System.out);
         }

         KdcComm.KdcAccessibility.addBad(var2);
         throw var8;
      }
   }

   private byte[] send(byte[] var1, String var2, boolean var3) throws IOException, KrbException {
      if (var1 == null) {
         return null;
      } else {
         int var4 = 88;
         int var5 = this.getRealmSpecificValue(this.realm, "max_retries", defaultKdcRetryLimit);
         int var6 = this.getRealmSpecificValue(this.realm, "kdc_timeout", defaultKdcTimeout);
         if (badPolicy == KdcComm.BpType.TRY_LESS && KdcComm.KdcAccessibility.isBad(var2)) {
            if (var5 > tryLessMaxRetries) {
               var5 = tryLessMaxRetries;
            }

            if (var6 > tryLessTimeout) {
               var6 = tryLessTimeout;
            }
         }

         String var7 = null;
         String var8 = null;
         int var9;
         if (var2.charAt(0) == '[') {
            var9 = var2.indexOf(93, 1);
            if (var9 == -1) {
               throw new IOException("Illegal KDC: " + var2);
            }

            var7 = var2.substring(1, var9);
            if (var9 != var2.length() - 1) {
               if (var2.charAt(var9 + 1) != ':') {
                  throw new IOException("Illegal KDC: " + var2);
               }

               var8 = var2.substring(var9 + 2);
            }
         } else {
            var9 = var2.indexOf(58);
            if (var9 == -1) {
               var7 = var2;
            } else {
               int var10 = var2.indexOf(58, var9 + 1);
               if (var10 > 0) {
                  var7 = var2;
               } else {
                  var7 = var2.substring(0, var9);
                  var8 = var2.substring(var9 + 1);
               }
            }
         }

         if (var8 != null) {
            var9 = parsePositiveIntString(var8);
            if (var9 > 0) {
               var4 = var9;
            }
         }

         if (DEBUG) {
            System.out.println(">>> KrbKdcReq send: kdc=" + var7 + (var3 ? " TCP:" : " UDP:") + var4 + ", timeout=" + var6 + ", number of retries =" + var5 + ", #bytes=" + var1.length);
         }

         KdcComm.KdcCommunication var13 = new KdcComm.KdcCommunication(var7, var4, var3, var6, var5, var1);

         try {
            byte[] var14 = (byte[])AccessController.doPrivileged((PrivilegedExceptionAction)var13);
            if (DEBUG) {
               System.out.println(">>> KrbKdcReq send: #bytes read=" + (var14 != null ? var14.length : 0));
            }

            return var14;
         } catch (PrivilegedActionException var12) {
            Exception var11 = var12.getException();
            if (var11 instanceof IOException) {
               throw (IOException)var11;
            } else {
               throw (KrbException)var11;
            }
         }
      }
   }

   private static int parseTimeString(String var0) {
      if (var0 == null) {
         return -1;
      } else if (var0.endsWith("s")) {
         int var1 = parsePositiveIntString(var0.substring(0, var0.length() - 1));
         return var1 < 0 ? -1 : var1 * 1000;
      } else {
         return parsePositiveIntString(var0);
      }
   }

   private int getRealmSpecificValue(String var1, String var2, int var3) {
      int var4 = var3;
      if (var1 == null) {
         return var3;
      } else {
         int var5 = -1;

         try {
            String var6 = Config.getInstance().get("realms", var1, var2);
            if (var2.equals("kdc_timeout")) {
               var5 = parseTimeString(var6);
            } else {
               var5 = parsePositiveIntString(var6);
            }
         } catch (Exception var7) {
         }

         if (var5 > 0) {
            var4 = var5;
         }

         return var4;
      }
   }

   private static int parsePositiveIntString(String var0) {
      if (var0 == null) {
         return -1;
      } else {
         boolean var1 = true;

         int var4;
         try {
            var4 = Integer.parseInt(var0);
         } catch (Exception var3) {
            return -1;
         }

         return var4 >= 0 ? var4 : -1;
      }
   }

   static {
      DEBUG = Krb5.DEBUG;
      tryLessMaxRetries = 1;
      tryLessTimeout = 5000;
      initStatic();
   }

   static class KdcAccessibility {
      private static Set<String> bads = new HashSet();

      private static synchronized void addBad(String var0) {
         if (KdcComm.DEBUG) {
            System.out.println(">>> KdcAccessibility: add " + var0);
         }

         bads.add(var0);
      }

      private static synchronized void removeBad(String var0) {
         if (KdcComm.DEBUG) {
            System.out.println(">>> KdcAccessibility: remove " + var0);
         }

         bads.remove(var0);
      }

      private static synchronized boolean isBad(String var0) {
         return bads.contains(var0);
      }

      private static synchronized void reset() {
         if (KdcComm.DEBUG) {
            System.out.println(">>> KdcAccessibility: reset");
         }

         bads.clear();
      }

      private static synchronized List<String> list(String var0) {
         StringTokenizer var1 = new StringTokenizer(var0);
         ArrayList var2 = new ArrayList();
         if (KdcComm.badPolicy == KdcComm.BpType.TRY_LAST) {
            ArrayList var3 = new ArrayList();

            while(var1.hasMoreTokens()) {
               String var4 = var1.nextToken();
               if (bads.contains(var4)) {
                  var3.add(var4);
               } else {
                  var2.add(var4);
               }
            }

            var2.addAll(var3);
         } else {
            while(var1.hasMoreTokens()) {
               var2.add(var1.nextToken());
            }
         }

         return var2;
      }
   }

   private static class KdcCommunication implements PrivilegedExceptionAction<byte[]> {
      private String kdc;
      private int port;
      private boolean useTCP;
      private int timeout;
      private int retries;
      private byte[] obuf;

      public KdcCommunication(String var1, int var2, boolean var3, int var4, int var5, byte[] var6) {
         this.kdc = var1;
         this.port = var2;
         this.useTCP = var3;
         this.timeout = var4;
         this.retries = var5;
         this.obuf = var6;
      }

      public byte[] run() throws IOException, KrbException {
         byte[] var1 = null;

         for(int var2 = 1; var2 <= this.retries; ++var2) {
            String var3 = this.useTCP ? "TCP" : "UDP";
            NetClient var4 = NetClient.getInstance(var3, this.kdc, this.port, this.timeout);
            Throwable var5 = null;

            try {
               if (KdcComm.DEBUG) {
                  System.out.println(">>> KDCCommunication: kdc=" + this.kdc + " " + var3 + ":" + this.port + ", timeout=" + this.timeout + ",Attempt =" + var2 + ", #bytes=" + this.obuf.length);
               }

               try {
                  var4.send(this.obuf);
                  var1 = var4.receive();
                  break;
               } catch (SocketTimeoutException var16) {
                  if (KdcComm.DEBUG) {
                     System.out.println("SocketTimeOutException with attempt: " + var2);
                  }

                  if (var2 == this.retries) {
                     Object var19 = null;
                     throw var16;
                  }
               }
            } catch (Throwable var17) {
               var5 = var17;
               throw var17;
            } finally {
               if (var4 != null) {
                  if (var5 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var15) {
                        var5.addSuppressed(var15);
                     }
                  } else {
                     var4.close();
                  }
               }

            }
         }

         return var1;
      }
   }

   private static enum BpType {
      NONE,
      TRY_LAST,
      TRY_LESS;
   }
}
