package javax.security.auth.kerberos;

import java.io.File;
import java.security.AccessControlException;
import java.util.Objects;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KerberosSecrets;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;

public final class KeyTab {
   private final File file;
   private final KerberosPrincipal princ;
   private final boolean bound;

   private KeyTab(KerberosPrincipal var1, File var2, boolean var3) {
      this.princ = var1;
      this.file = var2;
      this.bound = var3;
   }

   public static KeyTab getInstance(File var0) {
      if (var0 == null) {
         throw new NullPointerException("file must be non null");
      } else {
         return new KeyTab((KerberosPrincipal)null, var0, true);
      }
   }

   public static KeyTab getUnboundInstance(File var0) {
      if (var0 == null) {
         throw new NullPointerException("file must be non null");
      } else {
         return new KeyTab((KerberosPrincipal)null, var0, false);
      }
   }

   public static KeyTab getInstance(KerberosPrincipal var0, File var1) {
      if (var0 == null) {
         throw new NullPointerException("princ must be non null");
      } else if (var1 == null) {
         throw new NullPointerException("file must be non null");
      } else {
         return new KeyTab(var0, var1, true);
      }
   }

   public static KeyTab getInstance() {
      return new KeyTab((KerberosPrincipal)null, (File)null, true);
   }

   public static KeyTab getUnboundInstance() {
      return new KeyTab((KerberosPrincipal)null, (File)null, false);
   }

   public static KeyTab getInstance(KerberosPrincipal var0) {
      if (var0 == null) {
         throw new NullPointerException("princ must be non null");
      } else {
         return new KeyTab(var0, (File)null, true);
      }
   }

   sun.security.krb5.internal.ktab.KeyTab takeSnapshot() {
      try {
         return sun.security.krb5.internal.ktab.KeyTab.getInstance(this.file);
      } catch (AccessControlException var3) {
         if (this.file != null) {
            throw var3;
         } else {
            AccessControlException var2 = new AccessControlException("Access to default keytab denied (modified exception)");
            var2.setStackTrace(var3.getStackTrace());
            throw var2;
         }
      }
   }

   public KerberosKey[] getKeys(KerberosPrincipal var1) {
      try {
         if (this.princ != null && !var1.equals(this.princ)) {
            return new KerberosKey[0];
         } else {
            PrincipalName var2 = new PrincipalName(var1.getName());
            EncryptionKey[] var3 = this.takeSnapshot().readServiceKeys(var2);
            KerberosKey[] var4 = new KerberosKey[var3.length];

            for(int var5 = 0; var5 < var4.length; ++var5) {
               Integer var6 = var3[var5].getKeyVersionNumber();
               var4[var5] = new KerberosKey(var1, var3[var5].getBytes(), var3[var5].getEType(), var6 == null ? 0 : var6);
               var3[var5].destroy();
            }

            return var4;
         }
      } catch (RealmException var7) {
         return new KerberosKey[0];
      }
   }

   EncryptionKey[] getEncryptionKeys(PrincipalName var1) {
      return this.takeSnapshot().readServiceKeys(var1);
   }

   public boolean exists() {
      return !this.takeSnapshot().isMissing();
   }

   public String toString() {
      String var1 = this.file == null ? "Default keytab" : this.file.toString();
      if (!this.bound) {
         return var1;
      } else {
         return this.princ == null ? var1 + " for someone" : var1 + " for " + this.princ;
      }
   }

   public int hashCode() {
      return Objects.hash(this.file, this.princ, this.bound);
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof KeyTab)) {
         return false;
      } else {
         KeyTab var2 = (KeyTab)var1;
         return Objects.equals(var2.princ, this.princ) && Objects.equals(var2.file, this.file) && this.bound == var2.bound;
      }
   }

   public KerberosPrincipal getPrincipal() {
      return this.princ;
   }

   public boolean isBound() {
      return this.bound;
   }

   static {
      KerberosSecrets.setJavaxSecurityAuthKerberosAccess(new JavaxSecurityAuthKerberosAccessImpl());
   }
}
