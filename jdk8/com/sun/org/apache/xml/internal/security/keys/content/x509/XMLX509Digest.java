package com.sun.org.apache.xml.internal.security.keys.content.x509;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Signature11ElementProxy;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLX509Digest extends Signature11ElementProxy implements XMLX509DataContent {
   public XMLX509Digest(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public XMLX509Digest(Document var1, byte[] var2, String var3) {
      super(var1);
      this.addBase64Text(var2);
      this.constructionElement.setAttributeNS((String)null, "Algorithm", var3);
   }

   public XMLX509Digest(Document var1, X509Certificate var2, String var3) throws XMLSecurityException {
      super(var1);
      this.addBase64Text(getDigestBytesFromCert(var2, var3));
      this.constructionElement.setAttributeNS((String)null, "Algorithm", var3);
   }

   public Attr getAlgorithmAttr() {
      return this.constructionElement.getAttributeNodeNS((String)null, "Algorithm");
   }

   public String getAlgorithm() {
      return this.getAlgorithmAttr().getNodeValue();
   }

   public byte[] getDigestBytes() throws XMLSecurityException {
      return this.getBytesFromTextChild();
   }

   public static byte[] getDigestBytesFromCert(X509Certificate var0, String var1) throws XMLSecurityException {
      String var2 = JCEMapper.translateURItoJCEID(var1);
      if (var2 == null) {
         Object[] var6 = new Object[]{var1};
         throw new XMLSecurityException("XMLX509Digest.UnknownDigestAlgorithm", var6);
      } else {
         try {
            MessageDigest var3 = MessageDigest.getInstance(var2);
            return var3.digest(var0.getEncoded());
         } catch (Exception var5) {
            Object[] var4 = new Object[]{var2};
            throw new XMLSecurityException("XMLX509Digest.FailedDigest", var4);
         }
      }
   }

   public String getBaseLocalName() {
      return "X509Digest";
   }
}
