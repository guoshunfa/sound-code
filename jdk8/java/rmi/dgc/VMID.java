package java.rmi.dgc;

import java.io.Serializable;
import java.rmi.server.UID;
import java.security.SecureRandom;

public final class VMID implements Serializable {
   private static final byte[] randomBytes;
   private byte[] addr;
   private UID uid;
   private static final long serialVersionUID = -538642295484486218L;

   public VMID() {
      this.addr = randomBytes;
      this.uid = new UID();
   }

   /** @deprecated */
   @Deprecated
   public static boolean isUnique() {
      return true;
   }

   public int hashCode() {
      return this.uid.hashCode();
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof VMID)) {
         return false;
      } else {
         VMID var2 = (VMID)var1;
         if (!this.uid.equals(var2.uid)) {
            return false;
         } else if (this.addr == null ^ var2.addr == null) {
            return false;
         } else {
            if (this.addr != null) {
               if (this.addr.length != var2.addr.length) {
                  return false;
               }

               for(int var3 = 0; var3 < this.addr.length; ++var3) {
                  if (this.addr[var3] != var2.addr[var3]) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      if (this.addr != null) {
         for(int var2 = 0; var2 < this.addr.length; ++var2) {
            int var3 = this.addr[var2] & 255;
            var1.append((var3 < 16 ? "0" : "") + Integer.toString(var3, 16));
         }
      }

      var1.append(':');
      var1.append(this.uid.toString());
      return var1.toString();
   }

   static {
      SecureRandom var0 = new SecureRandom();
      byte[] var1 = new byte[8];
      var0.nextBytes(var1);
      randomBytes = var1;
   }
}
