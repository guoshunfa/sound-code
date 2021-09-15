package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315WithComments;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import java.io.OutputStream;

public class TransformC14NWithComments extends TransformSpi {
   public static final String implementedTransformURI = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";

   protected String engineGetURI() {
      return "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
   }

   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput var1, OutputStream var2, Transform var3) throws CanonicalizationException {
      Canonicalizer20010315WithComments var4 = new Canonicalizer20010315WithComments();
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
