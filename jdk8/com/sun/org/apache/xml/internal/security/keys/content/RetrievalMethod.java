package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RetrievalMethod extends SignatureElementProxy implements KeyInfoContent {
   public static final String TYPE_DSA = "http://www.w3.org/2000/09/xmldsig#DSAKeyValue";
   public static final String TYPE_RSA = "http://www.w3.org/2000/09/xmldsig#RSAKeyValue";
   public static final String TYPE_PGP = "http://www.w3.org/2000/09/xmldsig#PGPData";
   public static final String TYPE_SPKI = "http://www.w3.org/2000/09/xmldsig#SPKIData";
   public static final String TYPE_MGMT = "http://www.w3.org/2000/09/xmldsig#MgmtData";
   public static final String TYPE_X509 = "http://www.w3.org/2000/09/xmldsig#X509Data";
   public static final String TYPE_RAWX509 = "http://www.w3.org/2000/09/xmldsig#rawX509Certificate";

   public RetrievalMethod(Element var1, String var2) throws XMLSecurityException {
      super(var1, var2);
   }

   public RetrievalMethod(Document var1, String var2, Transforms var3, String var4) {
      super(var1);
      this.constructionElement.setAttributeNS((String)null, "URI", var2);
      if (var4 != null) {
         this.constructionElement.setAttributeNS((String)null, "Type", var4);
      }

      if (var3 != null) {
         this.constructionElement.appendChild(var3.getElement());
         XMLUtils.addReturnToElement(this.constructionElement);
      }

   }

   public Attr getURIAttr() {
      return this.constructionElement.getAttributeNodeNS((String)null, "URI");
   }

   public String getURI() {
      return this.getURIAttr().getNodeValue();
   }

   public String getType() {
      return this.constructionElement.getAttributeNS((String)null, "Type");
   }

   public Transforms getTransforms() throws XMLSecurityException {
      try {
         Element var1 = XMLUtils.selectDsNode(this.constructionElement.getFirstChild(), "Transforms", 0);
         return var1 != null ? new Transforms(var1, this.baseURI) : null;
      } catch (XMLSignatureException var2) {
         throw new XMLSecurityException("empty", var2);
      }
   }

   public String getBaseLocalName() {
      return "RetrievalMethod";
   }
}
