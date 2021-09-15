package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Principal;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.util.DerValue;

public final class KerberosPrincipal implements Principal, Serializable {
   private static final long serialVersionUID = -7374788026156829911L;
   public static final int KRB_NT_UNKNOWN = 0;
   public static final int KRB_NT_PRINCIPAL = 1;
   public static final int KRB_NT_SRV_INST = 2;
   public static final int KRB_NT_SRV_HST = 3;
   public static final int KRB_NT_SRV_XHST = 4;
   public static final int KRB_NT_UID = 5;
   private transient String fullName;
   private transient String realm;
   private transient int nameType;

   public KerberosPrincipal(String var1) {
      this(var1, 1);
   }

   public KerberosPrincipal(String var1, int var2) {
      PrincipalName var3 = null;

      try {
         var3 = new PrincipalName(var1, var2);
      } catch (KrbException var7) {
         throw new IllegalArgumentException(var7.getMessage());
      }

      if (var3.isRealmDeduced() && !Realm.AUTODEDUCEREALM) {
         SecurityManager var4 = System.getSecurityManager();
         if (var4 != null) {
            try {
               var4.checkPermission(new ServicePermission("@" + var3.getRealmAsString(), "-"));
            } catch (SecurityException var6) {
               throw new SecurityException("Cannot read realm info");
            }
         }
      }

      this.nameType = var2;
      this.fullName = var3.toString();
      this.realm = var3.getRealmString();
   }

   public String getRealm() {
      return this.realm;
   }

   public int hashCode() {
      return this.getName().hashCode();
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof KerberosPrincipal)) {
         return false;
      } else {
         String var2 = this.getName();
         String var3 = ((KerberosPrincipal)var1).getName();
         return var2.equals(var3);
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      try {
         PrincipalName var2 = new PrincipalName(this.fullName, this.nameType);
         var1.writeObject(var2.asn1Encode());
         var1.writeObject(var2.getRealm().asn1Encode());
      } catch (Exception var4) {
         throw new IOException(var4);
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      byte[] var2 = (byte[])((byte[])var1.readObject());
      byte[] var3 = (byte[])((byte[])var1.readObject());

      try {
         Realm var4 = new Realm(new DerValue(var3));
         PrincipalName var5 = new PrincipalName(new DerValue(var2), var4);
         this.realm = var4.toString();
         this.fullName = var5.toString();
         this.nameType = var5.getNameType();
      } catch (Exception var6) {
         throw new IOException(var6);
      }
   }

   public String getName() {
      return this.fullName;
   }

   public int getNameType() {
      return this.nameType;
   }

   public String toString() {
      return this.getName();
   }
}
