package com.sun.org.apache.xml.internal.security.transforms;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public abstract class TransformSpi {
   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput var1, OutputStream var2, Transform var3) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException, ParserConfigurationException, SAXException {
      throw new UnsupportedOperationException();
   }

   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput var1, Transform var2) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException, ParserConfigurationException, SAXException {
      return this.enginePerformTransform(var1, (OutputStream)null, var2);
   }

   protected XMLSignatureInput enginePerformTransform(XMLSignatureInput var1) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException, ParserConfigurationException, SAXException {
      return this.enginePerformTransform(var1, (Transform)null);
   }

   protected abstract String engineGetURI();
}
