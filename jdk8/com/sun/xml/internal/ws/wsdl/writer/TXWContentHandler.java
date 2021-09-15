package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class TXWContentHandler implements ContentHandler {
   Stack<TypedXmlWriter> stack = new Stack();

   public TXWContentHandler(TypedXmlWriter txw) {
      this.stack.push(txw);
   }

   public void setDocumentLocator(Locator locator) {
   }

   public void startDocument() throws SAXException {
   }

   public void endDocument() throws SAXException {
   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
   }

   public void endPrefixMapping(String prefix) throws SAXException {
   }

   public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
      TypedXmlWriter txw = ((TypedXmlWriter)this.stack.peek())._element(uri, localName, TypedXmlWriter.class);
      this.stack.push(txw);
      if (atts != null) {
         for(int i = 0; i < atts.getLength(); ++i) {
            String auri = atts.getURI(i);
            if ("http://www.w3.org/2000/xmlns/".equals(auri)) {
               if ("xmlns".equals(atts.getLocalName(i))) {
                  txw._namespace(atts.getValue(i), "");
               } else {
                  txw._namespace(atts.getValue(i), atts.getLocalName(i));
               }
            } else if (!"schemaLocation".equals(atts.getLocalName(i)) || !"".equals(atts.getValue(i))) {
               txw._attribute(auri, atts.getLocalName(i), atts.getValue(i));
            }
         }
      }

   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      this.stack.pop();
   }

   public void characters(char[] ch, int start, int length) throws SAXException {
   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
   }

   public void processingInstruction(String target, String data) throws SAXException {
   }

   public void skippedEntity(String name) throws SAXException {
   }
}
