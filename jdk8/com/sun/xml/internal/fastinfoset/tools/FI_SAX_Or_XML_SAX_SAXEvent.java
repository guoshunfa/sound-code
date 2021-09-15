package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.fastinfoset.sax.SAXDocumentParser;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class FI_SAX_Or_XML_SAX_SAXEvent extends TransformInputOutput {
   public void parse(InputStream document, OutputStream events, String workingDirectory) throws Exception {
      if (!((InputStream)document).markSupported()) {
         document = new BufferedInputStream((InputStream)document);
      }

      ((InputStream)document).mark(4);
      boolean isFastInfosetDocument = Decoder.isFastInfosetDocument((InputStream)document);
      ((InputStream)document).reset();
      if (isFastInfosetDocument) {
         SAXDocumentParser parser = new SAXDocumentParser();
         SAXEventSerializer ses = new SAXEventSerializer(events);
         parser.setContentHandler(ses);
         parser.setProperty("http://xml.org/sax/properties/lexical-handler", ses);
         parser.parse((InputStream)document);
      } else {
         SAXParserFactory parserFactory = SAXParserFactory.newInstance();
         parserFactory.setNamespaceAware(true);
         SAXParser parser = parserFactory.newSAXParser();
         SAXEventSerializer ses = new SAXEventSerializer(events);
         XMLReader reader = parser.getXMLReader();
         reader.setProperty("http://xml.org/sax/properties/lexical-handler", ses);
         reader.setContentHandler(ses);
         if (workingDirectory != null) {
            reader.setEntityResolver(createRelativePathResolver(workingDirectory));
         }

         reader.parse(new InputSource((InputStream)document));
      }

   }

   public void parse(InputStream document, OutputStream events) throws Exception {
      this.parse(document, events, (String)null);
   }

   public static void main(String[] args) throws Exception {
      FI_SAX_Or_XML_SAX_SAXEvent p = new FI_SAX_Or_XML_SAX_SAXEvent();
      p.parse(args);
   }
}
