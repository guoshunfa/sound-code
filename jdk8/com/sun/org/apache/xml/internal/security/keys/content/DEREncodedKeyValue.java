package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Signature11ElementProxy;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DEREncodedKeyValue extends Signature11ElementProxy implements KeyInfoContent {
   private static final String[] supportedKeyTypes = new String[]{"RSA", "DSA", "EC"};

   public DEREncodedKeyValue(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public DEREncodedKeyValue(Document var1, PublicKey var2) throws XMLSecurityException {
      super(var1);
      this.addBase64Text(this.getEncodedDER(var2));
   }

   public DEREncodedKeyValue(Document var1, byte[] var2) {
      super(var1);
      this.addBase64Text(var2);
   }

   public void setId(String var1) {
      if (var1 != null) {
         this.constructionElement.setAttributeNS((String)null, "Id", var1);
         this.constructionElement.setIdAttributeNS((String)null, "Id", true);
      } else {
         this.constructionElement.removeAttributeNS((String)null, "Id");
      }

   }

   public String getId() {
      return this.constructionElement.getAttributeNS((String)null, "Id");
   }

   public String getBaseLocalName() {
      return "DEREncodedKeyValue";
   }

   public PublicKey getPublicKey() throws XMLSecurityException {
      byte[] var1 = this.getBytesFromTextChild();
      String[] var2 = supportedKeyTypes;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];

         try {
            KeyFactory var6 = KeyFactory.getInstance(var5);
            X509EncodedKeySpec var7 = new X509EncodedKeySpec(var1);
            PublicKey var8 = var6.generatePublic(var7);
            if (var8 != null) {
               return var8;
            }
         } catch (NoSuchAlgorithmException var9) {
         } catch (InvalidKeySpecException var10) {
         }
      }

      throw new XMLSecurityException("DEREncodedKeyValue.UnsupportedEncodedKey");
   }

   protected byte[] getEncodedDER(PublicKey var1) throws XMLSecurityException {
      Object[] var3;
      try {
         KeyFactory var2 = KeyFactory.getInstance(var1.getAlgorithm());
         X509EncodedKeySpec var6 = (X509EncodedKeySpec)var2.getKeySpec(var1, X509EncodedKeySpec.class);
         return var6.getEncoded();
      } catch (NoSuchAlgorithmException var4) {
         var3 = new Object[]{var1.getAlgorithm(), var1.getFormat(), var1.getClass().getName()};
         throw new XMLSecurityException("DEREncodedKeyValue.UnsupportedPublicKey", var3, var4);
      } catch (InvalidKeySpecException var5) {
         var3 = new Object[]{var1.getAlgorithm(), var1.getFormat(), var1.getClass().getName()};
         throw new XMLSecurityException("DEREncodedKeyValue.UnsupportedPublicKey", var3, var5);
      }
   }
}
