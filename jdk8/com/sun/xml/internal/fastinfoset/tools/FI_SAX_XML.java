package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

public class FI_SAX_XML extends TransformInputOutput {
   public void parse(InputStream finf, OutputStream xml) throws Exception {
      Transformer tx = TransformerFactory.newInstance().newTransformer();
      tx.transform(new FastInfosetSource(finf), new StreamResult(xml));
   }

   public static void main(String[] args) throws Exception {
      FI_SAX_XML p = new FI_SAX_XML();
      p.parse(args);
   }
}
