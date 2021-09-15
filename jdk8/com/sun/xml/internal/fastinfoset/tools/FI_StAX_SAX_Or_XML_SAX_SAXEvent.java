package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.DefaultHandler;

public class FI_StAX_SAX_Or_XML_SAX_SAXEvent extends TransformInputOutput {
   public void parse(InputStream document, OutputStream events) throws Exception {
      if (!((InputStream)document).markSupported()) {
         document = new BufferedInputStream((InputStream)document);
      }

      ((InputStream)document).mark(4);
      boolean isFastInfosetDocument = Decoder.isFastInfosetDocument((InputStream)document);
      ((InputStream)document).reset();
      if (isFastInfosetDocument) {
         StAXDocumentParser parser = new StAXDocumentParser();
         parser.setInputStream((InputStream)document);
         SAXEventSerializer ses = new SAXEventSerializer(events);
         StAX2SAXReader reader = new StAX2SAXReader(parser, ses);
         reader.setLexicalHandler(ses);
         reader.adapt();
      } else {
         SAXParserFactory parserFactory = SAXParserFactory.newInstance();
         parserFactory.setNamespaceAware(true);
         SAXParser parser = parserFactory.newSAXParser();
         SAXEventSerializer ses = new SAXEventSerializer(events);
         parser.setProperty("http://xml.org/sax/properties/lexical-handler", ses);
         parser.parse((InputStream)document, (DefaultHandler)ses);
      }

   }

   public static void main(String[] args) throws Exception {
      FI_StAX_SAX_Or_XML_SAX_SAXEvent p = new FI_StAX_SAX_Or_XML_SAX_SAXEvent();
      p.parse(args);
   }
}
