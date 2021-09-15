package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent extends TransformInputOutput {
   public void parse(InputStream document, OutputStream events, String workingDirectory) throws Exception {
      if (!((InputStream)document).markSupported()) {
         document = new BufferedInputStream((InputStream)document);
      }

      ((InputStream)document).mark(4);
      boolean isFastInfosetDocument = Decoder.isFastInfosetDocument((InputStream)document);
      ((InputStream)document).reset();
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer t = tf.newTransformer();
      DOMResult dr = new DOMResult();
      if (isFastInfosetDocument) {
         t.transform(new FastInfosetSource((InputStream)document), dr);
      } else if (workingDirectory != null) {
         SAXParser parser = this.getParser();
         XMLReader reader = parser.getXMLReader();
         reader.setEntityResolver(createRelativePathResolver(workingDirectory));
         SAXSource source = new SAXSource(reader, new InputSource((InputStream)document));
         t.transform(source, dr);
      } else {
         t.transform(new StreamSource((InputStream)document), dr);
      }

      SAXEventSerializer ses = new SAXEventSerializer(events);
      t.transform(new DOMSource(dr.getNode()), new SAXResult(ses));
   }

   public void parse(InputStream document, OutputStream events) throws Exception {
      this.parse(document, events, (String)null);
   }

   private SAXParser getParser() {
      SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
      saxParserFactory.setNamespaceAware(true);

      try {
         return saxParserFactory.newSAXParser();
      } catch (Exception var3) {
         return null;
      }
   }

   public static void main(String[] args) throws Exception {
      FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent p = new FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent();
      p.parse(args);
   }
}
