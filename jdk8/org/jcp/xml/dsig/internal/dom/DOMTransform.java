package org.jcp.xml.dsig.internal.dom;

import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DOMTransform extends DOMStructure implements Transform {
   protected TransformService spi;

   public DOMTransform(TransformService var1) {
      this.spi = var1;
   }

   public DOMTransform(Element var1, XMLCryptoContext var2, Provider var3) throws MarshalException {
      String var4 = DOMUtils.getAttributeValue(var1, "Algorithm");
      if (var3 == null) {
         try {
            this.spi = TransformService.getInstance(var4, "DOM");
         } catch (NoSuchAlgorithmException var10) {
            throw new MarshalException(var10);
         }
      } else {
         try {
            this.spi = TransformService.getInstance(var4, "DOM", var3);
         } catch (NoSuchAlgorithmException var9) {
            try {
               this.spi = TransformService.getInstance(var4, "DOM");
            } catch (NoSuchAlgorithmException var8) {
               throw new MarshalException(var8);
            }
         }
      }

      try {
         this.spi.init(new javax.xml.crypto.dom.DOMStructure(var1), var2);
      } catch (InvalidAlgorithmParameterException var7) {
         throw new MarshalException(var7);
      }
   }

   public final AlgorithmParameterSpec getParameterSpec() {
      return this.spi.getParameterSpec();
   }

   public final String getAlgorithm() {
      return this.spi.getAlgorithm();
   }

   public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
      Document var4 = DOMUtils.getOwnerDocument(var1);
      Element var5 = null;
      if (var1.getLocalName().equals("Transforms")) {
         var5 = DOMUtils.createElement(var4, "Transform", "http://www.w3.org/2000/09/xmldsig#", var2);
      } else {
         var5 = DOMUtils.createElement(var4, "CanonicalizationMethod", "http://www.w3.org/2000/09/xmldsig#", var2);
      }

      DOMUtils.setAttribute(var5, "Algorithm", this.getAlgorithm());
      this.spi.marshalParams(new javax.xml.crypto.dom.DOMStructure(var5), var3);
      var1.appendChild(var5);
   }

   public Data transform(Data var1, XMLCryptoContext var2) throws TransformException {
      return this.spi.transform(var1, var2);
   }

   public Data transform(Data var1, XMLCryptoContext var2, OutputStream var3) throws TransformException {
      return this.spi.transform(var1, var2, var3);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Transform)) {
         return false;
      } else {
         Transform var2 = (Transform)var1;
         return this.getAlgorithm().equals(var2.getAlgorithm()) && DOMUtils.paramsEqual(this.getParameterSpec(), var2.getParameterSpec());
      }
   }

   public int hashCode() {
      byte var1 = 17;
      int var3 = 31 * var1 + this.getAlgorithm().hashCode();
      AlgorithmParameterSpec var2 = this.getParameterSpec();
      if (var2 != null) {
         var3 = 31 * var3 + var2.hashCode();
      }

      return var3;
   }

   Data transform(Data var1, XMLCryptoContext var2, DOMSignContext var3) throws MarshalException, TransformException {
      this.marshal(var3.getParent(), DOMUtils.getSignaturePrefix(var3), var3);
      return this.transform(var1, var2);
   }
}
