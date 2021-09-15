package java.security.cert;

import java.math.BigInteger;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import sun.security.x509.X509CRLEntryImpl;

public abstract class X509CRLEntry implements X509Extension {
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof X509CRLEntry)) {
         return false;
      } else {
         try {
            byte[] var2 = this.getEncoded();
            byte[] var3 = ((X509CRLEntry)var1).getEncoded();
            if (var2.length != var3.length) {
               return false;
            } else {
               for(int var4 = 0; var4 < var2.length; ++var4) {
                  if (var2[var4] != var3[var4]) {
                     return false;
                  }
               }

               return true;
            }
         } catch (CRLException var5) {
            return false;
         }
      }
   }

   public int hashCode() {
      int var1 = 0;

      try {
         byte[] var2 = this.getEncoded();

         for(int var3 = 1; var3 < var2.length; ++var3) {
            var1 += var2[var3] * var3;
         }

         return var1;
      } catch (CRLException var4) {
         return var1;
      }
   }

   public abstract byte[] getEncoded() throws CRLException;

   public abstract BigInteger getSerialNumber();

   public X500Principal getCertificateIssuer() {
      return null;
   }

   public abstract Date getRevocationDate();

   public abstract boolean hasExtensions();

   public abstract String toString();

   public CRLReason getRevocationReason() {
      return !this.hasExtensions() ? null : X509CRLEntryImpl.getRevocationReason(this);
   }
}
