package sun.security.x509;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.security.auth.x500.X500Principal;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CertificateIssuerName implements CertAttrSet<String> {
   public static final String IDENT = "x509.info.issuer";
   public static final String NAME = "issuer";
   public static final String DN_NAME = "dname";
   public static final String DN_PRINCIPAL = "x500principal";
   private X500Name dnName;
   private X500Principal dnPrincipal;

   public CertificateIssuerName(X500Name var1) {
      this.dnName = var1;
   }

   public CertificateIssuerName(DerInputStream var1) throws IOException {
      this.dnName = new X500Name(var1);
   }

   public CertificateIssuerName(InputStream var1) throws IOException {
      DerValue var2 = new DerValue(var1);
      this.dnName = new X500Name(var2);
   }

   public String toString() {
      return this.dnName == null ? "" : this.dnName.toString();
   }

   public void encode(OutputStream var1) throws IOException {
      DerOutputStream var2 = new DerOutputStream();
      this.dnName.encode(var2);
      var1.write(var2.toByteArray());
   }

   public void set(String var1, Object var2) throws IOException {
      if (!(var2 instanceof X500Name)) {
         throw new IOException("Attribute must be of type X500Name.");
      } else if (var1.equalsIgnoreCase("dname")) {
         this.dnName = (X500Name)var2;
         this.dnPrincipal = null;
      } else {
         throw new IOException("Attribute name not recognized by CertAttrSet:CertificateIssuerName.");
      }
   }

   public Object get(String var1) throws IOException {
      if (var1.equalsIgnoreCase("dname")) {
         return this.dnName;
      } else if (var1.equalsIgnoreCase("x500principal")) {
         if (this.dnPrincipal == null && this.dnName != null) {
            this.dnPrincipal = this.dnName.asX500Principal();
         }

         return this.dnPrincipal;
      } else {
         throw new IOException("Attribute name not recognized by CertAttrSet:CertificateIssuerName.");
      }
   }

   public void delete(String var1) throws IOException {
      if (var1.equalsIgnoreCase("dname")) {
         this.dnName = null;
         this.dnPrincipal = null;
      } else {
         throw new IOException("Attribute name not recognized by CertAttrSet:CertificateIssuerName.");
      }
   }

   public Enumeration<String> getElements() {
      AttributeNameEnumeration var1 = new AttributeNameEnumeration();
      var1.addElement("dname");
      return var1.elements();
   }

   public String getName() {
      return "issuer";
   }
}
