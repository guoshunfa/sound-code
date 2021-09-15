package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_OmitComments;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import java.io.OutputStream;

public class TransformC14N11 extends TransformSpi {
   protected String engineGetURI() {
      return "http://www.w3.org/2006/12/xml-c14n11";
   }

   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput var1, OutputStream var2, Transform var3) throws CanonicalizationException {
      Canonicalizer11_OmitComments var4 = new Canonicalizer11_OmitComments();
      if (var2 != null) {
         var4.setWriter(var2);
      }

      Object var5 = null;
      byte[] var7 = var4.engineCanonicalize(var1);
      XMLSignatureInput var6 = new XMLSignatureInput(var7);
      if (var2 != null) {
         var6.setOutputStream(var2);
      }

      return var6;
   }
}
