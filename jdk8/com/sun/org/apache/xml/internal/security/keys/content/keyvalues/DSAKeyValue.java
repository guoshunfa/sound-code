package com.sun.org.apache.xml.internal.security.keys.content.keyvalues;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DSAKeyValue extends SignatureElementProxy implements KeyValueContent {
   public DSAKeyValue(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public DSAKeyValue(Document var1, BigInteger var2, BigInteger var3, BigInteger var4, BigInteger var5) {
      super(var1);
      XMLUtils.addReturnToElement(this.constructionElement);
      this.addBigIntegerElement(var2, "P");
      this.addBigIntegerElement(var3, "Q");
      this.addBigIntegerElement(var4, "G");
      this.addBigIntegerElement(var5, "Y");
   }

   public DSAKeyValue(Document var1, Key var2) throws IllegalArgumentException {
      super(var1);
      XMLUtils.addReturnToElement(this.constructionElement);
      if (var2 instanceof DSAPublicKey) {
         this.addBigIntegerElement(((DSAPublicKey)var2).getParams().getP(), "P");
         this.addBigIntegerElement(((DSAPublicKey)var2).getParams().getQ(), "Q");
         this.addBigIntegerElement(((DSAPublicKey)var2).getParams().getG(), "G");
         this.addBigIntegerElement(((DSAPublicKey)var2).getY(), "Y");
      } else {
         Object[] var3 = new Object[]{"DSAKeyValue", var2.getClass().getName()};
         throw new IllegalArgumentException(I18n.translate("KeyValue.IllegalArgument", var3));
      }
   }

   public PublicKey getPublicKey() throws XMLSecurityException {
      try {
         DSAPublicKeySpec var1 = new DSAPublicKeySpec(this.getBigIntegerFromChildElement("Y", "http://www.w3.org/2000/09/xmldsig#"), this.getBigIntegerFromChildElement("P", "http://www.w3.org/2000/09/xmldsig#"), this.getBigIntegerFromChildElement("Q", "http://www.w3.org/2000/09/xmldsig#"), this.getBigIntegerFromChildElement("G", "http://www.w3.org/2000/09/xmldsig#"));
         KeyFactory var2 = KeyFactory.getInstance("DSA");
         PublicKey var3 = var2.generatePublic(var1);
         return var3;
      } catch (NoSuchAlgorithmException var4) {
         throw new XMLSecurityException("empty", var4);
      } catch (InvalidKeySpecException var5) {
         throw new XMLSecurityException("empty", var5);
      }
   }

   public String getBaseLocalName() {
      return "DSAKeyValue";
   }
}
