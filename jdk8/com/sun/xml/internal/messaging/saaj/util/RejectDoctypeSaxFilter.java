package com.sun.xml.internal.messaging.saaj.util;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.soap.SOAPException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

public class RejectDoctypeSaxFilter extends XMLFilterImpl implements XMLReader, LexicalHandler {
   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.util", "com.sun.xml.internal.messaging.saaj.util.LocalStrings");
   static final String LEXICAL_HANDLER_PROP = "http://xml.org/sax/properties/lexical-handler";
   static final String WSU_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd".intern();
   static final String SIGNATURE_LNAME = "Signature".intern();
   static final String ENCRYPTED_DATA_LNAME = "EncryptedData".intern();
   static final String DSIG_NS = "http://www.w3.org/2000/09/xmldsig#".intern();
   static final String XENC_NS = "http://www.w3.org/2001/04/xmlenc#".intern();
   static final String ID_NAME = "ID".intern();
   private LexicalHandler lexicalHandler;

   public RejectDoctypeSaxFilter(SAXParser saxParser) throws SOAPException {
      XMLReader xmlReader;
      try {
         xmlReader = saxParser.getXMLReader();
      } catch (Exception var5) {
         log.severe("SAAJ0602.util.getXMLReader.exception");
         throw new SOAPExceptionImpl("Couldn't get an XMLReader while constructing a RejectDoctypeSaxFilter", var5);
      }

      try {
         xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
      } catch (Exception var4) {
         log.severe("SAAJ0603.util.setProperty.exception");
         throw new SOAPExceptionImpl("Couldn't set the lexical handler property while constructing a RejectDoctypeSaxFilter", var4);
      }

      this.setParent(xmlReader);
   }

   public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
      if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
         this.lexicalHandler = (LexicalHandler)value;
      } else {
         super.setProperty(name, value);
      }

   }

   public void startDTD(String name, String publicId, String systemId) throws SAXException {
      throw new SAXException("Document Type Declaration is not allowed");
   }

   public void endDTD() throws SAXException {
   }

   public void startEntity(String name) throws SAXException {
      if (this.lexicalHandler != null) {
         this.lexicalHandler.startEntity(name);
      }

   }

   public void endEntity(String name) throws SAXException {
      if (this.lexicalHandler != null) {
         this.lexicalHandler.endEntity(name);
      }

   }

   public void startCDATA() throws SAXException {
      if (this.lexicalHandler != null) {
         this.lexicalHandler.startCDATA();
      }

   }

   public void endCDATA() throws SAXException {
      if (this.lexicalHandler != null) {
         this.lexicalHandler.endCDATA();
      }

   }

   public void comment(char[] ch, int start, int length) throws SAXException {
      if (this.lexicalHandler != null) {
         this.lexicalHandler.comment(ch, start, length);
      }

   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
      if (atts != null) {
         boolean eos = false;
         if (namespaceURI == DSIG_NS || XENC_NS == namespaceURI) {
            eos = true;
         }

         int length = atts.getLength();
         AttributesImpl attrImpl = new AttributesImpl();

         for(int i = 0; i < length; ++i) {
            String name = atts.getLocalName(i);
            if (name != null && name.equals("Id")) {
               if (!eos && atts.getURI(i) != WSU_NS) {
                  attrImpl.addAttribute(atts.getURI(i), atts.getLocalName(i), atts.getQName(i), atts.getType(i), atts.getValue(i));
               } else {
                  attrImpl.addAttribute(atts.getURI(i), atts.getLocalName(i), atts.getQName(i), ID_NAME, atts.getValue(i));
               }
            } else {
               attrImpl.addAttribute(atts.getURI(i), atts.getLocalName(i), atts.getQName(i), atts.getType(i), atts.getValue(i));
            }
         }

         super.startElement(namespaceURI, localName, qName, attrImpl);
      } else {
         super.startElement(namespaceURI, localName, qName, (Attributes)null);
      }

   }
}
