package org.jcp.xml.dsig.internal.dom;

import java.math.BigInteger;
import javax.security.auth.x500.X500Principal;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMX509IssuerSerial extends DOMStructure implements X509IssuerSerial {
   private final String issuerName;
   private final BigInteger serialNumber;

   public DOMX509IssuerSerial(String var1, BigInteger var2) {
      if (var1 == null) {
         throw new NullPointerException("issuerName cannot be null");
      } else if (var2 == null) {
         throw new NullPointerException("serialNumber cannot be null");
      } else {
         new X500Principal(var1);
         this.issuerName = var1;
         this.serialNumber = var2;
      }
   }

   public DOMX509IssuerSerial(Element var1) throws MarshalException {
      Element var2 = DOMUtils.getFirstChildElement(var1, "X509IssuerName");
      Element var3 = DOMUtils.getNextSiblingElement(var2, "X509SerialNumber");
      this.issuerName = var2.getFirstChild().getNodeValue();
      this.serialNumber = new BigInteger(var3.getFirstChild().getNodeValue());
   }

   public String getIssuerName() {
      return this.issuerName;
   }

   public BigInteger getSerialNumber() {
      return this.serialNumber;
   }

   public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
      Document var4 = DOMUtils.getOwnerDocument(var1);
      Element var5 = DOMUtils.createElement(var4, "X509IssuerSerial", "http://www.w3.org/2000/09/xmldsig#", var2);
      Element var6 = DOMUtils.createElement(var4, "X509IssuerName", "http://www.w3.org/2000/09/xmldsig#", var2);
      Element var7 = DOMUtils.createElement(var4, "X509SerialNumber", "http://www.w3.org/2000/09/xmldsig#", var2);
      var6.appendChild(var4.createTextNode(this.issuerName));
      var7.appendChild(var4.createTextNode(this.serialNumber.toString()));
      var5.appendChild(var6);
      var5.appendChild(var7);
      var1.appendChild(var5);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof X509IssuerSerial)) {
         return false;
      } else {
         X509IssuerSerial var2 = (X509IssuerSerial)var1;
         return this.issuerName.equals(var2.getIssuerName()) && this.serialNumber.equals(var2.getSerialNumber());
      }
   }

   public int hashCode() {
      byte var1 = 17;
      int var2 = 31 * var1 + this.issuerName.hashCode();
      var2 = 31 * var2 + this.serialNumber.hashCode();
      return var2;
   }
}
