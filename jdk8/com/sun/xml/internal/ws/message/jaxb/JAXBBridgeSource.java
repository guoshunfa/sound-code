package com.sun.xml.internal.ws.message.jaxb;

import com.sun.xml.internal.ws.spi.db.XMLBridge;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

final class JAXBBridgeSource extends SAXSource {
   private final XMLBridge bridge;
   private final Object contentObject;
   private final XMLReader pseudoParser = new XMLFilterImpl() {
      private LexicalHandler lexicalHandler;

      public boolean getFeature(String name) throws SAXNotRecognizedException {
         if (name.equals("http://xml.org/sax/features/namespaces")) {
            return true;
         } else if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
            return false;
         } else {
            throw new SAXNotRecognizedException(name);
         }
      }

      public void setFeature(String name, boolean value) throws SAXNotRecognizedException {
         if (!name.equals("http://xml.org/sax/features/namespaces") || !value) {
            if (!name.equals("http://xml.org/sax/features/namespace-prefixes") || value) {
               throw new SAXNotRecognizedException(name);
            }
         }
      }

      public Object getProperty(String name) throws SAXNotRecognizedException {
         if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
            return this.lexicalHandler;
         } else {
            throw new SAXNotRecognizedException(name);
         }
      }

      public void setProperty(String name, Object value) throws SAXNotRecognizedException {
         if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
            this.lexicalHandler = (LexicalHandler)value;
         } else {
            throw new SAXNotRecognizedException(name);
         }
      }

      public void parse(InputSource input) throws SAXException {
         this.parse();
      }

      public void parse(String systemId) throws SAXException {
         this.parse();
      }

      public void parse() throws SAXException {
         try {
            this.startDocument();
            JAXBBridgeSource.this.bridge.marshal(JAXBBridgeSource.this.contentObject, (ContentHandler)this, (AttachmentMarshaller)null);
            this.endDocument();
         } catch (JAXBException var3) {
            SAXParseException se = new SAXParseException(var3.getMessage(), (String)null, (String)null, -1, -1, var3);
            this.fatalError(se);
            throw se;
         }
      }
   };

   public JAXBBridgeSource(XMLBridge bridge, Object contentObject) {
      this.bridge = bridge;
      this.contentObject = contentObject;
      super.setXMLReader(this.pseudoParser);
      super.setInputSource(new InputSource());
   }
}
