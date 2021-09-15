package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315ExclWithComments;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.OutputStream;
import org.w3c.dom.Element;

public class TransformC14NExclusiveWithComments extends TransformSpi {
   public static final String implementedTransformURI = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";

   protected String engineGetURI() {
      return "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
   }

   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput var1, OutputStream var2, Transform var3) throws CanonicalizationException {
      try {
         String var4 = null;
         if (var3.length("http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces") == 1) {
            Element var5 = XMLUtils.selectNode(var3.getElement().getFirstChild(), "http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces", 0);
            var4 = (new InclusiveNamespaces(var5, var3.getBaseURI())).getInclusiveNamespaces();
         }

         Canonicalizer20010315ExclWithComments var9 = new Canonicalizer20010315ExclWithComments();
         if (var2 != null) {
            var9.setWriter(var2);
         }

         byte[] var6 = var9.engineCanonicalize(var1, var4);
         XMLSignatureInput var7 = new XMLSignatureInput(var6);
         return var7;
      } catch (XMLSecurityException var8) {
         throw new CanonicalizationException("empty", var8);
      }
   }
}
