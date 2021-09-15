package com.sun.org.apache.xml.internal.security.keys.content.x509;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.RFC2253Parser;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import java.security.cert.X509Certificate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLX509SubjectName extends SignatureElementProxy implements XMLX509DataContent {
   public XMLX509SubjectName(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public XMLX509SubjectName(Document var1, String var2) {
      super(var1);
      this.addText(var2);
   }

   public XMLX509SubjectName(Document var1, X509Certificate var2) {
      this(var1, var2.getSubjectX500Principal().getName());
   }

   public String getSubjectName() {
      return RFC2253Parser.normalize(this.getTextFromTextChild());
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof XMLX509SubjectName)) {
         return false;
      } else {
         XMLX509SubjectName var2 = (XMLX509SubjectName)var1;
         String var3 = var2.getSubjectName();
         String var4 = this.getSubjectName();
         return var4.equals(var3);
      }
   }

   public int hashCode() {
      byte var1 = 17;
      int var2 = 31 * var1 + this.getSubjectName().hashCode();
      return var2;
   }

   public String getBaseLocalName() {
      return "X509SubjectName";
   }
}
