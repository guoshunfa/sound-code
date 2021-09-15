package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import java.io.IOException;
import javax.xml.crypto.OctetStreamData;

public class ApacheOctetStreamData extends OctetStreamData implements ApacheData {
   private XMLSignatureInput xi;

   public ApacheOctetStreamData(XMLSignatureInput var1) throws CanonicalizationException, IOException {
      super(var1.getOctetStream(), var1.getSourceURI(), var1.getMIMEType());
      this.xi = var1;
   }

   public XMLSignatureInput getXMLSignatureInput() {
      return this.xi;
   }
}
