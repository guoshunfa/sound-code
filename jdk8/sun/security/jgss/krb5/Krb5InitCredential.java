package sun.security.jgss.krb5;

import java.io.IOException;
import java.net.InetAddress;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Provider;
import java.util.Date;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;

public class Krb5InitCredential extends KerberosTicket implements Krb5CredElement {
   private static final long serialVersionUID = 7723415700837898232L;
   private Krb5NameElement name;
   private Credentials krb5Credentials;

   private Krb5InitCredential(Krb5NameElement var1, byte[] var2, KerberosPrincipal var3, KerberosPrincipal var4, byte[] var5, int var6, boolean[] var7, Date var8, Date var9, Date var10, Date var11, InetAddress[] var12) throws GSSException {
      super(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
      this.name = var1;

      try {
         this.krb5Credentials = new Credentials(var2, var3.getName(), var4.getName(), var5, var6, var7, var8, var9, var10, var11, var12);
      } catch (KrbException var14) {
         throw new GSSException(13, -1, var14.getMessage());
      } catch (IOException var15) {
         throw new GSSException(13, -1, var15.getMessage());
      }
   }

   private Krb5InitCredential(Krb5NameElement var1, Credentials var2, byte[] var3, KerberosPrincipal var4, KerberosPrincipal var5, byte[] var6, int var7, boolean[] var8, Date var9, Date var10, Date var11, Date var12, InetAddress[] var13) throws GSSException {
      super(var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
      this.name = var1;
      this.krb5Credentials = var2;
   }

   static Krb5InitCredential getInstance(GSSCaller var0, Krb5NameElement var1, int var2) throws GSSException {
      KerberosTicket var3 = getTgt(var0, var1, var2);
      if (var3 == null) {
         throw new GSSException(13, -1, "Failed to find any Kerberos tgt");
      } else {
         if (var1 == null) {
            String var4 = var3.getClient().getName();
            var1 = Krb5NameElement.getInstance(var4, Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL);
         }

         return new Krb5InitCredential(var1, var3.getEncoded(), var3.getClient(), var3.getServer(), var3.getSessionKey().getEncoded(), var3.getSessionKeyType(), var3.getFlags(), var3.getAuthTime(), var3.getStartTime(), var3.getEndTime(), var3.getRenewTill(), var3.getClientAddresses());
      }
   }

   static Krb5InitCredential getInstance(Krb5NameElement var0, Credentials var1) throws GSSException {
      EncryptionKey var2 = var1.getSessionKey();
      PrincipalName var3 = var1.getClient();
      PrincipalName var4 = var1.getServer();
      KerberosPrincipal var5 = null;
      KerberosPrincipal var6 = null;
      Krb5NameElement var7 = null;
      if (var3 != null) {
         String var8 = var3.getName();
         var7 = Krb5NameElement.getInstance(var8, Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL);
         var5 = new KerberosPrincipal(var8);
      }

      if (var4 != null) {
         var6 = new KerberosPrincipal(var4.getName(), 2);
      }

      return new Krb5InitCredential(var7, var1, var1.getEncoded(), var5, var6, var2.getBytes(), var2.getEType(), var1.getFlags(), var1.getAuthTime(), var1.getStartTime(), var1.getEndTime(), var1.getRenewTill(), var1.getClientAddresses());
   }

   public final GSSNameSpi getName() throws GSSException {
      return this.name;
   }

   public int getInitLifetime() throws GSSException {
      boolean var1 = false;
      Date var2 = this.getEndTime();
      if (var2 == null) {
         return 0;
      } else {
         int var3 = (int)(var2.getTime() - (new Date()).getTime());
         return var3 / 1000;
      }
   }

   public int getAcceptLifetime() throws GSSException {
      return 0;
   }

   public boolean isInitiatorCredential() throws GSSException {
      return true;
   }

   public boolean isAcceptorCredential() throws GSSException {
      return false;
   }

   public final Oid getMechanism() {
      return Krb5MechFactory.GSS_KRB5_MECH_OID;
   }

   public final Provider getProvider() {
      return Krb5MechFactory.PROVIDER;
   }

   Credentials getKrb5Credentials() {
      return this.krb5Credentials;
   }

   public void dispose() throws GSSException {
      try {
         this.destroy();
      } catch (DestroyFailedException var3) {
         GSSException var2 = new GSSException(11, -1, "Could not destroy credentials - " + var3.getMessage());
         var2.initCause(var3);
      }

   }

   private static KerberosTicket getTgt(GSSCaller var0, Krb5NameElement var1, int var2) throws GSSException {
      final String var3;
      if (var1 != null) {
         var3 = var1.getKrb5PrincipalName().getName();
      } else {
         var3 = null;
      }

      final AccessControlContext var4 = AccessController.getContext();

      try {
         final GSSCaller var5 = var0 == GSSCaller.CALLER_UNKNOWN ? GSSCaller.CALLER_INITIATE : var0;
         return (KerberosTicket)AccessController.doPrivileged(new PrivilegedExceptionAction<KerberosTicket>() {
            public KerberosTicket run() throws Exception {
               return Krb5Util.getTicket(var5, var3, (String)null, var4);
            }
         });
      } catch (PrivilegedActionException var7) {
         GSSException var6 = new GSSException(13, -1, "Attempt to obtain new INITIATE credentials failed! (" + var7.getMessage() + ")");
         var6.initCause(var7.getException());
         throw var6;
      }
   }

   public GSSCredentialSpi impersonate(GSSNameSpi var1) throws GSSException {
      try {
         Krb5NameElement var2 = (Krb5NameElement)var1;
         Credentials var5 = Credentials.acquireS4U2selfCreds(var2.getKrb5PrincipalName(), this.krb5Credentials);
         return new Krb5ProxyCredential(this, var2, var5.getTicket());
      } catch (KrbException | IOException var4) {
         GSSException var3 = new GSSException(11, -1, "Attempt to obtain S4U2self credentials failed!");
         var3.initCause(var4);
         throw var3;
      }
   }
}
