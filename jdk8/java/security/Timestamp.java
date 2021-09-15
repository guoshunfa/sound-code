package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.cert.CertPath;
import java.util.Date;
import java.util.List;

public final class Timestamp implements Serializable {
   private static final long serialVersionUID = -5502683707821851294L;
   private Date timestamp;
   private CertPath signerCertPath;
   private transient int myhash = -1;

   public Timestamp(Date var1, CertPath var2) {
      if (var1 != null && var2 != null) {
         this.timestamp = new Date(var1.getTime());
         this.signerCertPath = var2;
      } else {
         throw new NullPointerException();
      }
   }

   public Date getTimestamp() {
      return new Date(this.timestamp.getTime());
   }

   public CertPath getSignerCertPath() {
      return this.signerCertPath;
   }

   public int hashCode() {
      if (this.myhash == -1) {
         this.myhash = this.timestamp.hashCode() + this.signerCertPath.hashCode();
      }

      return this.myhash;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof Timestamp) {
         Timestamp var2 = (Timestamp)var1;
         if (this == var2) {
            return true;
         } else {
            return this.timestamp.equals(var2.getTimestamp()) && this.signerCertPath.equals(var2.getSignerCertPath());
         }
      } else {
         return false;
      }
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("(");
      var1.append("timestamp: " + this.timestamp);
      List var2 = this.signerCertPath.getCertificates();
      if (!var2.isEmpty()) {
         var1.append("TSA: " + var2.get(0));
      } else {
         var1.append("TSA: <empty>");
      }

      var1.append(")");
      return var1.toString();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.myhash = -1;
      this.timestamp = new Date(this.timestamp.getTime());
   }
}
