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
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RSAKeyValue extends SignatureElementProxy implements KeyValueContent {
   public RSAKeyValue(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public RSAKeyValue(Document var1, BigInteger var2, BigInteger var3) {
      super(var1);
      XMLUtils.addReturnToElement(this.constructionElement);
      this.addBigIntegerElement(var2, "Modulus");
      this.addBigIntegerElement(var3, "Exponent");
   }

   public RSAKeyValue(Document var1, Key var2) throws IllegalArgumentException {
      super(var1);
      XMLUtils.addReturnToElement(this.constructionElement);
      if (var2 instanceof RSAPublicKey) {
         this.addBigIntegerElement(((RSAPublicKey)var2).getModulus(), "Modulus");
         this.addBigIntegerElement(((RSAPublicKey)var2).getPublicExponent(), "Exponent");
      } else {
         Object[] var3 = new Object[]{"RSAKeyValue", var2.getClass().getName()};
         throw new IllegalArgumentException(I18n.translate("KeyValue.IllegalArgument", var3));
      }
   }

   public PublicKey getPublicKey() throws XMLSecurityException {
      try {
         KeyFactory var1 = KeyFactory.getInstance("RSA");
         RSAPublicKeySpec var2 = new RSAPublicKeySpec(this.getBigIntegerFromChildElement("Modulus", "http://www.w3.org/2000/09/xmldsig#"), this.getBigIntegerFromChildElement("Exponent", "http://www.w3.org/2000/09/xmldsig#"));
         PublicKey var3 = var1.generatePublic(var2);
         return var3;
      } catch (NoSuchAlgorithmException var4) {
         throw new XMLSecurityException("empty", var4);
      } catch (InvalidKeySpecException var5) {
         throw new XMLSecurityException("empty", var5);
      }
   }

   public String getBaseLocalName() {
      return "RSAKeyValue";
   }
}
