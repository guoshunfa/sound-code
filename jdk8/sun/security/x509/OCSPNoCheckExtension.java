package sun.security.x509;

import java.io.IOException;
import java.util.Enumeration;

public class OCSPNoCheckExtension extends Extension implements CertAttrSet<String> {
   public static final String IDENT = "x509.info.extensions.OCSPNoCheck";
   public static final String NAME = "OCSPNoCheck";

   public OCSPNoCheckExtension() throws IOException {
      this.extensionId = PKIXExtensions.OCSPNoCheck_Id;
      this.critical = false;
      this.extensionValue = new byte[0];
   }

   public OCSPNoCheckExtension(Boolean var1, Object var2) throws IOException {
      this.extensionId = PKIXExtensions.OCSPNoCheck_Id;
      this.critical = var1;
      this.extensionValue = new byte[0];
   }

   public void set(String var1, Object var2) throws IOException {
      throw new IOException("No attribute is allowed by CertAttrSet:OCSPNoCheckExtension.");
   }

   public Object get(String var1) throws IOException {
      throw new IOException("No attribute is allowed by CertAttrSet:OCSPNoCheckExtension.");
   }

   public void delete(String var1) throws IOException {
      throw new IOException("No attribute is allowed by CertAttrSet:OCSPNoCheckExtension.");
   }

   public Enumeration<String> getElements() {
      return (new AttributeNameEnumeration()).elements();
   }

   public String getName() {
      return "OCSPNoCheck";
   }
}
