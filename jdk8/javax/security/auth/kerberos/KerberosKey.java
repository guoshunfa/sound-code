package javax.security.auth.kerberos;

import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

public class KerberosKey implements SecretKey, Destroyable {
   private static final long serialVersionUID = -4625402278148246993L;
   private KerberosPrincipal principal;
   private int versionNum;
   private KeyImpl key;
   private transient boolean destroyed = false;

   public KerberosKey(KerberosPrincipal var1, byte[] var2, int var3, int var4) {
      this.principal = var1;
      this.versionNum = var4;
      this.key = new KeyImpl(var2, var3);
   }

   public KerberosKey(KerberosPrincipal var1, char[] var2, String var3) {
      this.principal = var1;
      this.key = new KeyImpl(var1, var2, var3);
   }

   public final KerberosPrincipal getPrincipal() {
      if (this.destroyed) {
         throw new IllegalStateException("This key is no longer valid");
      } else {
         return this.principal;
      }
   }

   public final int getVersionNumber() {
      if (this.destroyed) {
         throw new IllegalStateException("This key is no longer valid");
      } else {
         return this.versionNum;
      }
   }

   public final int getKeyType() {
      if (this.destroyed) {
         throw new IllegalStateException("This key is no longer valid");
      } else {
         return this.key.getKeyType();
      }
   }

   public final String getAlgorithm() {
      if (this.destroyed) {
         throw new IllegalStateException("This key is no longer valid");
      } else {
         return this.key.getAlgorithm();
      }
   }

   public final String getFormat() {
      if (this.destroyed) {
         throw new IllegalStateException("This key is no longer valid");
      } else {
         return this.key.getFormat();
      }
   }

   public final byte[] getEncoded() {
      if (this.destroyed) {
         throw new IllegalStateException("This key is no longer valid");
      } else {
         return this.key.getEncoded();
      }
   }

   public void destroy() throws DestroyFailedException {
      if (!this.destroyed) {
         this.key.destroy();
         this.principal = null;
         this.destroyed = true;
      }

   }

   public boolean isDestroyed() {
      return this.destroyed;
   }

   public String toString() {
      return this.destroyed ? "Destroyed Principal" : "Kerberos Principal " + this.principal.toString() + "Key Version " + this.versionNum + "key " + this.key.toString();
   }

   public int hashCode() {
      byte var1 = 17;
      if (this.isDestroyed()) {
         return var1;
      } else {
         int var2 = 37 * var1 + Arrays.hashCode(this.getEncoded());
         var2 = 37 * var2 + this.getKeyType();
         if (this.principal != null) {
            var2 = 37 * var2 + this.principal.hashCode();
         }

         return var2 * 37 + this.versionNum;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof KerberosKey)) {
         return false;
      } else {
         KerberosKey var2 = (KerberosKey)var1;
         if (!this.isDestroyed() && !var2.isDestroyed()) {
            if (this.versionNum == var2.getVersionNumber() && this.getKeyType() == var2.getKeyType() && Arrays.equals(this.getEncoded(), var2.getEncoded())) {
               if (this.principal == null) {
                  if (var2.getPrincipal() != null) {
                     return false;
                  }
               } else if (!this.principal.equals(var2.getPrincipal())) {
                  return false;
               }

               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }
}
