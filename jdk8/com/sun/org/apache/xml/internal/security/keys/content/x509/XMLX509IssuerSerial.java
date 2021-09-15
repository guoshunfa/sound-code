package com.sun.org.apache.xml.internal.security.keys.content.x509;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.RFC2253Parser;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLX509IssuerSerial extends SignatureElementProxy implements XMLX509DataContent {
   private static Logger log = Logger.getLogger(XMLX509IssuerSerial.class.getName());

   public XMLX509IssuerSerial(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public XMLX509IssuerSerial(Document var1, String var2, BigInteger var3) {
      super(var1);
      XMLUtils.addReturnToElement(this.constructionElement);
      this.addTextElement(var2, "X509IssuerName");
      this.addTextElement(var3.toString(), "X509SerialNumber");
   }

   public XMLX509IssuerSerial(Document var1, String var2, String var3) {
      this(var1, var2, new BigInteger(var3));
   }

   public XMLX509IssuerSerial(Document var1, String var2, int var3) {
      this(var1, var2, new BigInteger(Integer.toString(var3)));
   }

   public XMLX509IssuerSerial(Document var1, X509Certificate var2) {
      this(var1, var2.getIssuerX500Principal().getName(), var2.getSerialNumber());
   }

   public BigInteger getSerialNumber() {
      String var1 = this.getTextFromChildElement("X509SerialNumber", "http://www.w3.org/2000/09/xmldsig#");
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "X509SerialNumber text: " + var1);
      }

      return new BigInteger(var1);
   }

   public int getSerialNumberInteger() {
      return this.getSerialNumber().intValue();
   }

   public String getIssuerName() {
      return RFC2253Parser.normalize(this.getTextFromChildElement("X509IssuerName", "http://www.w3.org/2000/09/xmldsig#"));
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof XMLX509IssuerSerial)) {
         return false;
      } else {
         XMLX509IssuerSerial var2 = (XMLX509IssuerSerial)var1;
         return this.getSerialNumber().equals(var2.getSerialNumber()) && this.getIssuerName().equals(var2.getIssuerName());
      }
   }

   public int hashCode() {
      byte var1 = 17;
      int var2 = 31 * var1 + this.getSerialNumber().hashCode();
      var2 = 31 * var2 + this.getIssuerName().hashCode();
      return var2;
   }

   public String getBaseLocalName() {
      return "X509IssuerSerial";
   }
}
