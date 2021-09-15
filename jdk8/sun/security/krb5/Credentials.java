package sun.security.krb5;

import java.io.IOException;
import java.net.InetAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Date;
import java.util.Locale;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.CredentialsUtil;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCOptions;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.TicketFlags;
import sun.security.krb5.internal.ccache.CredentialsCache;
import sun.security.krb5.internal.crypto.EType;

public class Credentials {
   Ticket ticket;
   PrincipalName client;
   PrincipalName server;
   EncryptionKey key;
   TicketFlags flags;
   KerberosTime authTime;
   KerberosTime startTime;
   KerberosTime endTime;
   KerberosTime renewTill;
   HostAddresses cAddr;
   EncryptionKey serviceKey;
   AuthorizationData authzData;
   private static boolean DEBUG;
   private static CredentialsCache cache;
   static boolean alreadyLoaded;
   private static boolean alreadyTried;

   private static native Credentials acquireDefaultNativeCreds(int[] var0);

   public Credentials(Ticket var1, PrincipalName var2, PrincipalName var3, EncryptionKey var4, TicketFlags var5, KerberosTime var6, KerberosTime var7, KerberosTime var8, KerberosTime var9, HostAddresses var10, AuthorizationData var11) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      this.authzData = var11;
   }

   public Credentials(Ticket var1, PrincipalName var2, PrincipalName var3, EncryptionKey var4, TicketFlags var5, KerberosTime var6, KerberosTime var7, KerberosTime var8, KerberosTime var9, HostAddresses var10) {
      this.ticket = var1;
      this.client = var2;
      this.server = var3;
      this.key = var4;
      this.flags = var5;
      this.authTime = var6;
      this.startTime = var7;
      this.endTime = var8;
      this.renewTill = var9;
      this.cAddr = var10;
   }

   public Credentials(byte[] var1, String var2, String var3, byte[] var4, int var5, boolean[] var6, Date var7, Date var8, Date var9, Date var10, InetAddress[] var11) throws KrbException, IOException {
      this(new Ticket(var1), new PrincipalName(var2, 1), new PrincipalName(var3, 2), new EncryptionKey(var5, var4), var6 == null ? null : new TicketFlags(var6), var7 == null ? null : new KerberosTime(var7), var8 == null ? null : new KerberosTime(var8), var9 == null ? null : new KerberosTime(var9), var10 == null ? null : new KerberosTime(var10), (HostAddresses)null);
   }

   public final PrincipalName getClient() {
      return this.client;
   }

   public final PrincipalName getServer() {
      return this.server;
   }

   public final EncryptionKey getSessionKey() {
      return this.key;
   }

   public final Date getAuthTime() {
      return this.authTime != null ? this.authTime.toDate() : null;
   }

   public final Date getStartTime() {
      return this.startTime != null ? this.startTime.toDate() : null;
   }

   public final Date getEndTime() {
      return this.endTime != null ? this.endTime.toDate() : null;
   }

   public final Date getRenewTill() {
      return this.renewTill != null ? this.renewTill.toDate() : null;
   }

   public final boolean[] getFlags() {
      return this.flags == null ? null : this.flags.toBooleanArray();
   }

   public final InetAddress[] getClientAddresses() {
      return this.cAddr == null ? null : this.cAddr.getInetAddresses();
   }

   public final byte[] getEncoded() {
      byte[] var1 = null;

      try {
         var1 = this.ticket.asn1Encode();
      } catch (Asn1Exception var3) {
         if (DEBUG) {
            System.out.println((Object)var3);
         }
      } catch (IOException var4) {
         if (DEBUG) {
            System.out.println((Object)var4);
         }
      }

      return var1;
   }

   public boolean isForwardable() {
      return this.flags.get(1);
   }

   public boolean isRenewable() {
      return this.flags.get(8);
   }

   public Ticket getTicket() {
      return this.ticket;
   }

   public TicketFlags getTicketFlags() {
      return this.flags;
   }

   public AuthorizationData getAuthzData() {
      return this.authzData;
   }

   public boolean checkDelegate() {
      return this.flags.get(13);
   }

   public void resetDelegate() {
      this.flags.set(13, false);
   }

   public Credentials renew() throws KrbException, IOException {
      KDCOptions var1 = new KDCOptions();
      var1.set(30, true);
      var1.set(8, true);
      return (new KrbTgsReq(var1, this, this.server, (KerberosTime)null, (KerberosTime)null, (KerberosTime)null, (int[])null, this.cAddr, (AuthorizationData)null, (Ticket[])null, (EncryptionKey)null)).sendAndGetCreds();
   }

   public static Credentials acquireTGTFromCache(PrincipalName var0, String var1) throws KrbException, IOException {
      if (var1 == null) {
         String var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("os.name")));
         if (var2.toUpperCase(Locale.ENGLISH).startsWith("WINDOWS") || var2.toUpperCase(Locale.ENGLISH).contains("OS X")) {
            Credentials var5 = acquireDefaultCreds();
            if (var5 == null) {
               if (DEBUG) {
                  System.out.println(">>> Found no TGT's in LSA");
               }

               return null;
            }

            if (var0 != null) {
               if (var5.getClient().equals(var0)) {
                  if (DEBUG) {
                     System.out.println(">>> Obtained TGT from LSA: " + var5);
                  }

                  return var5;
               }

               if (DEBUG) {
                  System.out.println(">>> LSA contains TGT for " + var5.getClient() + " not " + var0);
               }

               return null;
            }

            if (DEBUG) {
               System.out.println(">>> Obtained TGT from LSA: " + var5);
            }

            return var5;
         }
      }

      CredentialsCache var4 = CredentialsCache.getInstance(var0, var1);
      if (var4 == null) {
         return null;
      } else {
         sun.security.krb5.internal.ccache.Credentials var3 = var4.getDefaultCreds();
         if (var3 == null) {
            return null;
         } else if (EType.isSupported(var3.getEType())) {
            return var3.setKrbCreds();
         } else {
            if (DEBUG) {
               System.out.println(">>> unsupported key type found the default TGT: " + var3.getEType());
            }

            return null;
         }
      }
   }

   public static synchronized Credentials acquireDefaultCreds() {
      Credentials var0 = null;
      if (cache == null) {
         cache = CredentialsCache.getInstance();
      }

      if (cache != null) {
         sun.security.krb5.internal.ccache.Credentials var1 = cache.getDefaultCreds();
         if (var1 != null) {
            if (DEBUG) {
               System.out.println(">>> KrbCreds found the default ticket granting ticket in credential cache.");
            }

            if (EType.isSupported(var1.getEType())) {
               var0 = var1.setKrbCreds();
            } else if (DEBUG) {
               System.out.println(">>> unsupported key type found the default TGT: " + var1.getEType());
            }
         }
      }

      if (var0 == null) {
         if (!alreadyTried) {
            try {
               ensureLoaded();
            } catch (Exception var3) {
               if (DEBUG) {
                  System.out.println("Can not load credentials cache");
                  var3.printStackTrace();
               }

               alreadyTried = true;
            }
         }

         if (alreadyLoaded) {
            if (DEBUG) {
               System.out.println(">> Acquire default native Credentials");
            }

            try {
               var0 = acquireDefaultNativeCreds(EType.getDefaults("default_tkt_enctypes"));
            } catch (KrbException var2) {
            }
         }
      }

      return var0;
   }

   public static Credentials acquireServiceCreds(String var0, Credentials var1) throws KrbException, IOException {
      return CredentialsUtil.acquireServiceCreds(var0, var1);
   }

   public static Credentials acquireS4U2selfCreds(PrincipalName var0, Credentials var1) throws KrbException, IOException {
      return CredentialsUtil.acquireS4U2selfCreds(var0, var1);
   }

   public static Credentials acquireS4U2proxyCreds(String var0, Ticket var1, PrincipalName var2, Credentials var3) throws KrbException, IOException {
      return CredentialsUtil.acquireS4U2proxyCreds(var0, var1, var2, var3);
   }

   public CredentialsCache getCache() {
      return cache;
   }

   public EncryptionKey getServiceKey() {
      return this.serviceKey;
   }

   public static void printDebug(Credentials var0) {
      System.out.println(">>> DEBUG: ----Credentials----");
      System.out.println("\tclient: " + var0.client.toString());
      System.out.println("\tserver: " + var0.server.toString());
      System.out.println("\tticket: sname: " + var0.ticket.sname.toString());
      if (var0.startTime != null) {
         System.out.println("\tstartTime: " + var0.startTime.getTime());
      }

      System.out.println("\tendTime: " + var0.endTime.getTime());
      System.out.println("        ----Credentials end----");
   }

   static void ensureLoaded() {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            if (System.getProperty("os.name").contains("OS X")) {
               System.loadLibrary("osxkrb5");
            } else {
               System.loadLibrary("w2k_lsa_auth");
            }

            return null;
         }
      });
      alreadyLoaded = true;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer("Credentials:");
      var1.append("\n      client=").append((Object)this.client);
      var1.append("\n      server=").append((Object)this.server);
      if (this.authTime != null) {
         var1.append("\n    authTime=").append((Object)this.authTime);
      }

      if (this.startTime != null) {
         var1.append("\n   startTime=").append((Object)this.startTime);
      }

      var1.append("\n     endTime=").append((Object)this.endTime);
      var1.append("\n   renewTill=").append((Object)this.renewTill);
      var1.append("\n       flags=").append((Object)this.flags);
      var1.append("\nEType (skey)=").append(this.key.getEType());
      var1.append("\n   (tkt key)=").append(this.ticket.encPart.eType);
      return var1.toString();
   }

   static {
      DEBUG = Krb5.DEBUG;
      alreadyLoaded = false;
      alreadyTried = false;
   }
}
