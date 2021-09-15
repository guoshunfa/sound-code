package java.security.cert;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public abstract class CertPath implements Serializable {
   private static final long serialVersionUID = 6068470306649138683L;
   private String type;

   protected CertPath(String var1) {
      this.type = var1;
   }

   public String getType() {
      return this.type;
   }

   public abstract Iterator<String> getEncodings();

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CertPath)) {
         return false;
      } else {
         CertPath var2 = (CertPath)var1;
         if (!var2.getType().equals(this.type)) {
            return false;
         } else {
            List var3 = this.getCertificates();
            List var4 = var2.getCertificates();
            return var3.equals(var4);
         }
      }
   }

   public int hashCode() {
      int var1 = this.type.hashCode();
      var1 = 31 * var1 + this.getCertificates().hashCode();
      return var1;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      Iterator var2 = this.getCertificates().iterator();
      var1.append("\n" + this.type + " Cert Path: length = " + this.getCertificates().size() + ".\n");
      var1.append("[\n");

      for(int var3 = 1; var2.hasNext(); ++var3) {
         var1.append("=========================================================Certificate " + var3 + " start.\n");
         Certificate var4 = (Certificate)var2.next();
         var1.append(var4.toString());
         var1.append("\n=========================================================Certificate " + var3 + " end.\n\n\n");
      }

      var1.append("\n]");
      return var1.toString();
   }

   public abstract byte[] getEncoded() throws CertificateEncodingException;

   public abstract byte[] getEncoded(String var1) throws CertificateEncodingException;

   public abstract List<? extends Certificate> getCertificates();

   protected Object writeReplace() throws ObjectStreamException {
      try {
         return new CertPath.CertPathRep(this.type, this.getEncoded());
      } catch (CertificateException var3) {
         NotSerializableException var2 = new NotSerializableException("java.security.cert.CertPath: " + this.type);
         var2.initCause(var3);
         throw var2;
      }
   }

   protected static class CertPathRep implements Serializable {
      private static final long serialVersionUID = 3015633072427920915L;
      private String type;
      private byte[] data;

      protected CertPathRep(String var1, byte[] var2) {
         this.type = var1;
         this.data = var2;
      }

      protected Object readResolve() throws ObjectStreamException {
         try {
            CertificateFactory var1 = CertificateFactory.getInstance(this.type);
            return var1.generateCertPath((InputStream)(new ByteArrayInputStream(this.data)));
         } catch (CertificateException var3) {
            NotSerializableException var2 = new NotSerializableException("java.security.cert.CertPath: " + this.type);
            var2.initCause(var3);
            throw var2;
         }
      }
   }
}
