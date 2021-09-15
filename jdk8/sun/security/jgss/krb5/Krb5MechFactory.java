package sun.security.jgss.krb5;

import java.security.Provider;
import java.util.Vector;
import javax.security.auth.kerberos.ServicePermission;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.SunProvider;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.spi.MechanismFactory;

public final class Krb5MechFactory implements MechanismFactory {
   private static final boolean DEBUG;
   static final Provider PROVIDER;
   static final Oid GSS_KRB5_MECH_OID;
   static final Oid NT_GSS_KRB5_PRINCIPAL;
   private static Oid[] nameTypes;
   private final GSSCaller caller;

   private static Krb5CredElement getCredFromSubject(GSSNameSpi var0, boolean var1) throws GSSException {
      Vector var2 = GSSUtil.searchSubject(var0, GSS_KRB5_MECH_OID, var1, var1 ? Krb5InitCredential.class : Krb5AcceptCredential.class);
      Krb5CredElement var3 = var2 != null && !var2.isEmpty() ? (Krb5CredElement)var2.firstElement() : null;
      if (var3 != null) {
         if (var1) {
            checkInitCredPermission((Krb5NameElement)var3.getName());
         } else {
            checkAcceptCredPermission((Krb5NameElement)var3.getName(), var0);
         }
      }

      return var3;
   }

   public Krb5MechFactory(GSSCaller var1) {
      this.caller = var1;
   }

   public GSSNameSpi getNameElement(String var1, Oid var2) throws GSSException {
      return Krb5NameElement.getInstance(var1, var2);
   }

   public GSSNameSpi getNameElement(byte[] var1, Oid var2) throws GSSException {
      return Krb5NameElement.getInstance(new String(var1), var2);
   }

   public GSSCredentialSpi getCredentialElement(GSSNameSpi var1, int var2, int var3, int var4) throws GSSException {
      if (var1 != null && !(var1 instanceof Krb5NameElement)) {
         var1 = Krb5NameElement.getInstance(((GSSNameSpi)var1).toString(), ((GSSNameSpi)var1).getStringNameType());
      }

      Object var5 = getCredFromSubject((GSSNameSpi)var1, var4 != 2);
      if (var5 == null) {
         if (var4 != 1 && var4 != 0) {
            if (var4 != 2) {
               throw new GSSException(11, -1, "Unknown usage mode requested");
            }

            var5 = Krb5AcceptCredential.getInstance(this.caller, (Krb5NameElement)var1);
            checkAcceptCredPermission((Krb5NameElement)((Krb5CredElement)var5).getName(), (GSSNameSpi)var1);
         } else {
            var5 = Krb5InitCredential.getInstance(this.caller, (Krb5NameElement)var1, var2);
            checkInitCredPermission((Krb5NameElement)((Krb5CredElement)var5).getName());
         }
      }

      return (GSSCredentialSpi)var5;
   }

   public static void checkInitCredPermission(Krb5NameElement var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         String var2 = var0.getKrb5PrincipalName().getRealmAsString();
         String var3 = new String("krbtgt/" + var2 + '@' + var2);
         ServicePermission var4 = new ServicePermission(var3, "initiate");

         try {
            var1.checkPermission(var4);
         } catch (SecurityException var6) {
            if (DEBUG) {
               System.out.println("Permission to initiatekerberos init credential" + var6.getMessage());
            }

            throw var6;
         }
      }

   }

   public static void checkAcceptCredPermission(Krb5NameElement var0, GSSNameSpi var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null && var0 != null) {
         ServicePermission var3 = new ServicePermission(var0.getKrb5PrincipalName().getName(), "accept");

         try {
            var2.checkPermission(var3);
         } catch (SecurityException var5) {
            SecurityException var4 = var5;
            if (var1 == null) {
               var4 = new SecurityException("No permission to acquire Kerberos accept credential");
            }

            throw var4;
         }
      }

   }

   public GSSContextSpi getMechanismContext(GSSNameSpi var1, GSSCredentialSpi var2, int var3) throws GSSException {
      if (var1 != null && !(var1 instanceof Krb5NameElement)) {
         var1 = Krb5NameElement.getInstance(((GSSNameSpi)var1).toString(), ((GSSNameSpi)var1).getStringNameType());
      }

      if (var2 == null) {
         var2 = this.getCredentialElement((GSSNameSpi)null, var3, 0, 1);
      }

      return new Krb5Context(this.caller, (Krb5NameElement)var1, (Krb5CredElement)var2, var3);
   }

   public GSSContextSpi getMechanismContext(GSSCredentialSpi var1) throws GSSException {
      if (var1 == null) {
         var1 = this.getCredentialElement((GSSNameSpi)null, 0, Integer.MAX_VALUE, 2);
      }

      return new Krb5Context(this.caller, (Krb5CredElement)var1);
   }

   public GSSContextSpi getMechanismContext(byte[] var1) throws GSSException {
      return new Krb5Context(this.caller, var1);
   }

   public final Oid getMechanismOid() {
      return GSS_KRB5_MECH_OID;
   }

   public Provider getProvider() {
      return PROVIDER;
   }

   public Oid[] getNameTypes() {
      return nameTypes;
   }

   private static Oid createOid(String var0) {
      Oid var1 = null;

      try {
         var1 = new Oid(var0);
      } catch (GSSException var3) {
      }

      return var1;
   }

   static {
      DEBUG = Krb5Util.DEBUG;
      PROVIDER = new SunProvider();
      GSS_KRB5_MECH_OID = createOid("1.2.840.113554.1.2.2");
      NT_GSS_KRB5_PRINCIPAL = createOid("1.2.840.113554.1.2.2.1");
      nameTypes = new Oid[]{GSSName.NT_USER_NAME, GSSName.NT_HOSTBASED_SERVICE, GSSName.NT_EXPORT_NAME, NT_GSS_KRB5_PRINCIPAL};
   }
}
