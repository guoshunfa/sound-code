package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class StAX2SAXReader {
   ContentHandler _handler;
   LexicalHandler _lexicalHandler;
   XMLStreamReader _reader;

   public StAX2SAXReader(XMLStreamReader reader, ContentHandler handler) {
      this._handler = handler;
      this._reader = reader;
   }

   public StAX2SAXReader(XMLStreamReader reader) {
      this._reader = reader;
   }

   public void setContentHandler(ContentHandler handler) {
      this._handler = handler;
   }

   public void setLexicalHandler(LexicalHandler lexicalHandler) {
      this._lexicalHandler = lexicalHandler;
   }

   public void adapt() throws XMLStreamException, SAXException {
      AttributesImpl attrs = new AttributesImpl();
      this._handler.startDocument();

      try {
         label69:
         while(this._reader.hasNext()) {
            int event = this._reader.next();
            QName qname;
            String prefix;
            String localPart;
            int nsc;
            int i;
            switch(event) {
            case 1:
               nsc = this._reader.getNamespaceCount();

               for(i = 0; i < nsc; ++i) {
                  this._handler.startPrefixMapping(this._reader.getNamespacePrefix(i), this._reader.getNamespaceURI(i));
               }

               attrs.clear();
               int nat = this._reader.getAttributeCount();

               for(i = 0; i < nat; ++i) {
                  QName q = this._reader.getAttributeName(i);
                  String qName = this._reader.getAttributePrefix(i);
                  if (qName != null && qName != "") {
                     qName = qName + ":" + q.getLocalPart();
                  } else {
                     qName = q.getLocalPart();
                  }

                  attrs.addAttribute(this._reader.getAttributeNamespace(i), q.getLocalPart(), qName, this._reader.getAttributeType(i), this._reader.getAttributeValue(i));
               }

               qname = this._reader.getName();
               prefix = qname.getPrefix();
               localPart = qname.getLocalPart();
               this._handler.startElement(this._reader.getNamespaceURI(), localPart, prefix.length() > 0 ? prefix + ":" + localPart : localPart, attrs);
               break;
            case 2:
               qname = this._reader.getName();
               prefix = qname.getPrefix();
               localPart = qname.getLocalPart();
               this._handler.endElement(this._reader.getNamespaceURI(), localPart, prefix.length() > 0 ? prefix + ":" + localPart : localPart);
               nsc = this._reader.getNamespaceCount();
               i = 0;

               while(true) {
                  if (i >= nsc) {
                     continue label69;
                  }

                  this._handler.endPrefixMapping(this._reader.getNamespacePrefix(i));
                  ++i;
               }
            case 3:
               this._handler.processingInstruction(this._reader.getPITarget(), this._reader.getPIData());
               break;
            case 4:
               this._handler.characters(this._reader.getTextCharacters(), this._reader.getTextStart(), this._reader.getTextLength());
               break;
            case 5:
               this._lexicalHandler.comment(this._reader.getTextCharacters(), this._reader.getTextStart(), this._reader.getTextLength());
               break;
            case 6:
            case 7:
            default:
               throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.StAX2SAXReader", new Object[]{event}));
            case 8:
            }
         }
      } catch (XMLStreamException var12) {
         this._handler.endDocument();
         throw var12;
      }

      this._handler.endDocument();
   }
}
