package sun.security.jgss.krb5;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Provider;
import java.util.Locale;
import javax.security.auth.kerberos.ServicePermission;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;

public class Krb5NameElement implements GSSNameSpi {
   private PrincipalName krb5PrincipalName;
   private String gssNameStr = null;
   private Oid gssNameType = null;
   private static String CHAR_ENCODING = "UTF-8";

   private Krb5NameElement(PrincipalName var1, String var2, Oid var3) {
      this.krb5PrincipalName = var1;
      this.gssNameStr = var2;
      this.gssNameType = var3;
   }

   static Krb5NameElement getInstance(String var0, Oid var1) throws GSSException {
      if (var1 == null) {
         var1 = Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL;
      } else if (!var1.equals(GSSName.NT_USER_NAME) && !var1.equals(GSSName.NT_HOSTBASED_SERVICE) && !var1.equals(Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL) && !var1.equals(GSSName.NT_EXPORT_NAME)) {
         throw new GSSException(4, -1, var1.toString() + " is an unsupported nametype");
      }

      PrincipalName var2;
      try {
         if (!var1.equals(GSSName.NT_EXPORT_NAME) && !var1.equals(Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL)) {
            String[] var3 = getComponents(var0);
            if (var1.equals(GSSName.NT_USER_NAME)) {
               var2 = new PrincipalName(var0, 1);
            } else {
               String var4 = null;
               String var5 = var3[0];
               if (var3.length >= 2) {
                  var4 = var3[1];
               }

               String var6 = getHostBasedInstance(var5, var4);
               var2 = new PrincipalName(var6, 3);
            }
         } else {
            var2 = new PrincipalName(var0, 1);
         }
      } catch (KrbException var8) {
         throw new GSSException(3, -1, var8.getMessage());
      }

      if (var2.isRealmDeduced() && !Realm.AUTODEDUCEREALM) {
         SecurityManager var9 = System.getSecurityManager();
         if (var9 != null) {
            try {
               var9.checkPermission(new ServicePermission("@" + var2.getRealmAsString(), "-"));
            } catch (SecurityException var7) {
               throw new GSSException(11);
            }
         }
      }

      return new Krb5NameElement(var2, var0, var1);
   }

   static Krb5NameElement getInstance(PrincipalName var0) {
      return new Krb5NameElement(var0, var0.getName(), Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL);
   }

   private static String[] getComponents(String var0) throws GSSException {
      int var2 = var0.lastIndexOf(64, var0.length());
      if (var2 > 0 && var0.charAt(var2 - 1) == '\\' && (var2 - 2 < 0 || var0.charAt(var2 - 2) != '\\')) {
         var2 = -1;
      }

      String[] var1;
      if (var2 > 0) {
         String var3 = var0.substring(0, var2);
         String var4 = var0.substring(var2 + 1);
         var1 = new String[]{var3, var4};
      } else {
         var1 = new String[]{var0};
      }

      return var1;
   }

   private static String getHostBasedInstance(String var0, String var1) throws GSSException {
      StringBuffer var2 = new StringBuffer(var0);

      try {
         if (var1 == null) {
            var1 = InetAddress.getLocalHost().getHostName();
         }
      } catch (UnknownHostException var4) {
      }

      var1 = var1.toLowerCase(Locale.ENGLISH);
      var2 = var2.append('/').append(var1);
      return var2.toString();
   }

   public final PrincipalName getKrb5PrincipalName() {
      return this.krb5PrincipalName;
   }

   public boolean equals(GSSNameSpi var1) throws GSSException {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof Krb5NameElement) {
         Krb5NameElement var2 = (Krb5NameElement)var1;
         return this.krb5PrincipalName.getName().equals(var2.krb5PrincipalName.getName());
      } else {
         return false;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         try {
            if (var1 instanceof Krb5NameElement) {
               return this.equals((GSSNameSpi)((Krb5NameElement)var1));
            }
         } catch (GSSException var3) {
         }

         return false;
      }
   }

   public int hashCode() {
      return 629 + this.krb5PrincipalName.getName().hashCode();
   }

   public byte[] export() throws GSSException {
      byte[] var1 = null;

      try {
         var1 = this.krb5PrincipalName.getName().getBytes(CHAR_ENCODING);
      } catch (UnsupportedEncodingException var3) {
      }

      return var1;
   }

   public Oid getMechanism() {
      return Krb5MechFactory.GSS_KRB5_MECH_OID;
   }

   public String toString() {
      return this.gssNameStr;
   }

   public Oid getGSSNameType() {
      return this.gssNameType;
   }

   public Oid getStringNameType() {
      return this.gssNameType;
   }

   public boolean isAnonymousName() {
      return this.gssNameType.equals(GSSName.NT_ANONYMOUS);
   }

   public Provider getProvider() {
      return Krb5MechFactory.PROVIDER;
   }
}
