package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetResult;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;

public class XML_DOM_SAX_FI extends TransformInputOutput {
   public void parse(InputStream document, OutputStream finf, String workingDirectory) throws Exception {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      DocumentBuilder db = dbf.newDocumentBuilder();
      if (workingDirectory != null) {
         db.setEntityResolver(createRelativePathResolver(workingDirectory));
      }

      Document d = db.parse(document);
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer t = tf.newTransformer();
      t.transform(new DOMSource(d), new FastInfosetResult(finf));
   }

   public void parse(InputStream document, OutputStream finf) throws Exception {
      this.parse(document, finf, (String)null);
   }

   public static void main(String[] args) throws Exception {
      XML_DOM_SAX_FI p = new XML_DOM_SAX_FI();
      p.parse(args);
   }
}
