package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import java.io.OutputStream;

public class TransformXPointer extends TransformSpi {
   public static final String implementedTransformURI = "http://www.w3.org/TR/2001/WD-xptr-20010108";

   protected String engineGetURI() {
      return "http://www.w3.org/TR/2001/WD-xptr-20010108";
   }

   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput var1, OutputStream var2, Transform var3) throws TransformationException {
      Object[] var4 = new Object[]{"http://www.w3.org/TR/2001/WD-xptr-20010108"};
      throw new TransformationException("signature.Transform.NotYetImplemented", var4);
   }
}
