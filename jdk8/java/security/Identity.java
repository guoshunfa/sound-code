package java.security;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

/** @deprecated */
@Deprecated
public abstract class Identity implements Principal, Serializable {
   private static final long serialVersionUID = 3609922007826600659L;
   private String name;
   private PublicKey publicKey;
   String info;
   IdentityScope scope;
   Vector<Certificate> certificates;

   protected Identity() {
      this("restoring...");
   }

   public Identity(String var1, IdentityScope var2) throws KeyManagementException {
      this(var1);
      if (var2 != null) {
         var2.addIdentity(this);
      }

      this.scope = var2;
   }

   public Identity(String var1) {
      this.info = "No further information available.";
      this.name = var1;
   }

   public final String getName() {
      return this.name;
   }

   public final IdentityScope getScope() {
      return this.scope;
   }

   public PublicKey getPublicKey() {
      return this.publicKey;
   }

   public void setPublicKey(PublicKey var1) throws KeyManagementException {
      check("setIdentityPublicKey");
      this.publicKey = var1;
      this.certificates = new Vector();
   }

   public void setInfo(String var1) {
      check("setIdentityInfo");
      this.info = var1;
   }

   public String getInfo() {
      return this.info;
   }

   public void addCertificate(Certificate var1) throws KeyManagementException {
      check("addIdentityCertificate");
      if (this.certificates == null) {
         this.certificates = new Vector();
      }

      if (this.publicKey != null) {
         if (!this.keyEquals(this.publicKey, var1.getPublicKey())) {
            throw new KeyManagementException("public key different from cert public key");
         }
      } else {
         this.publicKey = var1.getPublicKey();
      }

      this.certificates.addElement(var1);
   }

   private boolean keyEquals(PublicKey var1, PublicKey var2) {
      String var3 = var1.getFormat();
      String var4 = var2.getFormat();
      if (var3 == null ^ var4 == null) {
         return false;
      } else {
         return var3 != null && var4 != null && !var3.equalsIgnoreCase(var4) ? false : Arrays.equals(var1.getEncoded(), var2.getEncoded());
      }
   }

   public void removeCertificate(Certificate var1) throws KeyManagementException {
      check("removeIdentityCertificate");
      if (this.certificates != null) {
         this.certificates.removeElement(var1);
      }

   }

   public Certificate[] certificates() {
      if (this.certificates == null) {
         return new Certificate[0];
      } else {
         int var1 = this.certificates.size();
         Certificate[] var2 = new Certificate[var1];
         this.certificates.copyInto(var2);
         return var2;
      }
   }

   public final boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof Identity) {
         Identity var2 = (Identity)var1;
         return this.fullName().equals(var2.fullName()) ? true : this.identityEquals(var2);
      } else {
         return false;
      }
   }

   protected boolean identityEquals(Identity var1) {
      if (!this.name.equalsIgnoreCase(var1.name)) {
         return false;
      } else if (this.publicKey == null ^ var1.publicKey == null) {
         return false;
      } else {
         return this.publicKey == null || var1.publicKey == null || this.publicKey.equals(var1.publicKey);
      }
   }

   String fullName() {
      String var1 = this.name;
      if (this.scope != null) {
         var1 = var1 + "." + this.scope.getName();
      }

      return var1;
   }

   public String toString() {
      check("printIdentity");
      String var1 = this.name;
      if (this.scope != null) {
         var1 = var1 + "[" + this.scope.getName() + "]";
      }

      return var1;
   }

   public String toString(boolean var1) {
      String var2 = this.toString();
      if (var1) {
         var2 = var2 + "\n";
         var2 = var2 + this.printKeys();
         var2 = var2 + "\n" + this.printCertificates();
         if (this.info != null) {
            var2 = var2 + "\n\t" + this.info;
         } else {
            var2 = var2 + "\n\tno additional information available.";
         }
      }

      return var2;
   }

   String printKeys() {
      String var1 = "";
      if (this.publicKey != null) {
         var1 = "\tpublic key initialized";
      } else {
         var1 = "\tno public key";
      }

      return var1;
   }

   String printCertificates() {
      String var1 = "";
      if (this.certificates == null) {
         return "\tno certificates";
      } else {
         var1 = var1 + "\tcertificates: \n";
         int var2 = 1;

         Certificate var4;
         for(Iterator var3 = this.certificates.iterator(); var3.hasNext(); var1 = var1 + "\t\t\tfrom : " + var4.getGuarantor() + "\n") {
            var4 = (Certificate)var3.next();
            var1 = var1 + "\tcertificate " + var2++ + "\tfor  : " + var4.getPrincipal() + "\n";
         }

         return var1;
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   private static void check(String var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkSecurityAccess(var0);
      }

   }
}
