package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.OutputStream;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TransformEnvelopedSignature extends TransformSpi {
   public static final String implementedTransformURI = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";

   protected String engineGetURI() {
      return "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
   }

   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput var1, OutputStream var2, Transform var3) throws TransformationException {
      Element var4 = var3.getElement();
      Node var5 = searchSignatureElement(var4);
      var1.setExcludeNode(var5);
      var1.addNodeFilter(new TransformEnvelopedSignature.EnvelopedNodeFilter(var5));
      return var1;
   }

   private static Node searchSignatureElement(Node var0) throws TransformationException {
      boolean var1;
      for(var1 = false; var0 != null && var0.getNodeType() != 9; var0 = var0.getParentNode()) {
         Element var2 = (Element)var0;
         if (var2.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#") && var2.getLocalName().equals("Signature")) {
            var1 = true;
            break;
         }
      }

      if (!var1) {
         throw new TransformationException("transform.envelopedSignatureTransformNotInSignatureElement");
      } else {
         return var0;
      }
   }

   static class EnvelopedNodeFilter implements NodeFilter {
      Node exclude;

      EnvelopedNodeFilter(Node var1) {
         this.exclude = var1;
      }

      public int isNodeIncludeDO(Node var1, int var2) {
         return var1 == this.exclude ? -1 : 1;
      }

      public int isNodeInclude(Node var1) {
         return var1 != this.exclude && !XMLUtils.isDescendantOrSelf(this.exclude, var1) ? 1 : -1;
      }
   }
}
