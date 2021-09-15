package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.cert.CertPath;

public final class CodeSigner implements Serializable {
   private static final long serialVersionUID = 6819288105193937581L;
   private CertPath signerCertPath;
   private Timestamp timestamp;
   private transient int myhash = -1;

   public CodeSigner(CertPath var1, Timestamp var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.signerCertPath = var1;
         this.timestamp = var2;
      }
   }

   public CertPath getSignerCertPath() {
      return this.signerCertPath;
   }

   public Timestamp getTimestamp() {
      return this.timestamp;
   }

   public int hashCode() {
      if (this.myhash == -1) {
         if (this.timestamp == null) {
            this.myhash = this.signerCertPath.hashCode();
         } else {
            this.myhash = this.signerCertPath.hashCode() + this.timestamp.hashCode();
         }
      }

      return this.myhash;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof CodeSigner) {
         CodeSigner var2 = (CodeSigner)var1;
         if (this == var2) {
            return true;
         } else {
            Timestamp var3 = var2.getTimestamp();
            if (this.timestamp == null) {
               if (var3 != null) {
                  return false;
               }
            } else if (var3 == null || !this.timestamp.equals(var3)) {
               return false;
            }

            return this.signerCertPath.equals(var2.getSignerCertPath());
         }
      } else {
         return false;
      }
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("(");
      var1.append("Signer: " + this.signerCertPath.getCertificates().get(0));
      if (this.timestamp != null) {
         var1.append("timestamp: " + this.timestamp);
      }

      var1.append(")");
      return var1.toString();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.myhash = -1;
   }
}
