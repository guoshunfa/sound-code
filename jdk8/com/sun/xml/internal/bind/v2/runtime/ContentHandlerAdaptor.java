package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.FinalArrayList;
import com.sun.istack.internal.SAXException2;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

final class ContentHandlerAdaptor extends DefaultHandler {
   private final FinalArrayList<String> prefixMap = new FinalArrayList();
   private final XMLSerializer serializer;
   private final StringBuffer text = new StringBuffer();

   ContentHandlerAdaptor(XMLSerializer _serializer) {
      this.serializer = _serializer;
   }

   public void startDocument() {
      this.prefixMap.clear();
   }

   public void startPrefixMapping(String prefix, String uri) {
      this.prefixMap.add(prefix);
      this.prefixMap.add(uri);
   }

   private boolean containsPrefixMapping(String prefix, String uri) {
      for(int i = 0; i < this.prefixMap.size(); i += 2) {
         if (((String)this.prefixMap.get(i)).equals(prefix) && ((String)this.prefixMap.get(i + 1)).equals(uri)) {
            return true;
         }
      }

      return false;
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
      try {
         this.flushText();
         int len = atts.getLength();
         String p = this.getPrefix(qName);
         if (this.containsPrefixMapping(p, namespaceURI)) {
            this.serializer.startElementForce(namespaceURI, localName, p, (Object)null);
         } else {
            this.serializer.startElement(namespaceURI, localName, p, (Object)null);
         }

         int i;
         for(i = 0; i < this.prefixMap.size(); i += 2) {
            this.serializer.getNamespaceContext().force((String)this.prefixMap.get(i + 1), (String)this.prefixMap.get(i));
         }

         for(i = 0; i < len; ++i) {
            String qname = atts.getQName(i);
            if (!qname.startsWith("xmlns") && atts.getURI(i).length() != 0) {
               String prefix = this.getPrefix(qname);
               this.serializer.getNamespaceContext().declareNamespace(atts.getURI(i), prefix, true);
            }
         }

         this.serializer.endNamespaceDecls((Object)null);

         for(i = 0; i < len; ++i) {
            if (!atts.getQName(i).startsWith("xmlns")) {
               this.serializer.attribute(atts.getURI(i), atts.getLocalName(i), atts.getValue(i));
            }
         }

         this.prefixMap.clear();
         this.serializer.endAttributes();
      } catch (IOException var10) {
         throw new SAXException2(var10);
      } catch (XMLStreamException var11) {
         throw new SAXException2(var11);
      }
   }

   private String getPrefix(String qname) {
      int idx = qname.indexOf(58);
      String prefix = idx == -1 ? "" : qname.substring(0, idx);
      return prefix;
   }

   public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
      try {
         this.flushText();
         this.serializer.endElement();
      } catch (IOException var5) {
         throw new SAXException2(var5);
      } catch (XMLStreamException var6) {
         throw new SAXException2(var6);
      }
   }

   private void flushText() throws SAXException, IOException, XMLStreamException {
      if (this.text.length() != 0) {
         this.serializer.text((String)this.text.toString(), (String)null);
         this.text.setLength(0);
      }

   }

   public void characters(char[] ch, int start, int length) {
      this.text.append(ch, start, length);
   }
}
