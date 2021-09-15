package java.security.cert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.security.auth.x500.X500Principal;
import sun.misc.IOUtils;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.InvalidityDateExtension;

public class CertificateRevokedException extends CertificateException {
   private static final long serialVersionUID = 7839996631571608627L;
   private Date revocationDate;
   private final CRLReason reason;
   private final X500Principal authority;
   private transient Map<String, Extension> extensions;

   public CertificateRevokedException(Date var1, CRLReason var2, X500Principal var3, Map<String, Extension> var4) {
      if (var1 != null && var2 != null && var3 != null && var4 != null) {
         this.revocationDate = new Date(var1.getTime());
         this.reason = var2;
         this.authority = var3;
         this.extensions = Collections.checkedMap(new HashMap(), String.class, Extension.class);
         this.extensions.putAll(var4);
      } else {
         throw new NullPointerException();
      }
   }

   public Date getRevocationDate() {
      return (Date)this.revocationDate.clone();
   }

   public CRLReason getRevocationReason() {
      return this.reason;
   }

   public X500Principal getAuthorityName() {
      return this.authority;
   }

   public Date getInvalidityDate() {
      Extension var1 = (Extension)this.getExtensions().get("2.5.29.24");
      if (var1 == null) {
         return null;
      } else {
         try {
            Date var2 = InvalidityDateExtension.toImpl(var1).get("DATE");
            return new Date(var2.getTime());
         } catch (IOException var3) {
            return null;
         }
      }
   }

   public Map<String, Extension> getExtensions() {
      return Collections.unmodifiableMap(this.extensions);
   }

   public String getMessage() {
      return "Certificate has been revoked, reason: " + this.reason + ", revocation date: " + this.revocationDate + ", authority: " + this.authority + ", extension OIDs: " + this.extensions.keySet();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.extensions.size());
      Iterator var2 = this.extensions.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         Extension var4 = (Extension)var3.getValue();
         var1.writeObject(var4.getId());
         var1.writeBoolean(var4.isCritical());
         byte[] var5 = var4.getValue();
         var1.writeInt(var5.length);
         var1.write(var5);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.revocationDate = new Date(this.revocationDate.getTime());
      int var2 = var1.readInt();
      if (var2 == 0) {
         this.extensions = Collections.emptyMap();
      } else {
         if (var2 < 0) {
            throw new IOException("size cannot be negative");
         }

         this.extensions = new HashMap(var2 > 20 ? 20 : var2);
      }

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = (String)var1.readObject();
         boolean var5 = var1.readBoolean();
         byte[] var6 = IOUtils.readNBytes(var1, var1.readInt());
         sun.security.x509.Extension var7 = sun.security.x509.Extension.newExtension(new ObjectIdentifier(var4), var5, var6);
         this.extensions.put(var4, var7);
      }

   }
}
