package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXEventSerializer extends DefaultHandler implements LexicalHandler {
   private Writer _writer;
   private boolean _charactersAreCDATA;
   private StringBuffer _characters;
   private Stack _namespaceStack = new Stack();
   protected List _namespaceAttributes;

   public SAXEventSerializer(OutputStream s) throws IOException {
      this._writer = new OutputStreamWriter(s);
      this._charactersAreCDATA = false;
   }

   public void startDocument() throws SAXException {
      try {
         this._writer.write("<sax xmlns=\"http://www.sun.com/xml/sax-events\">\n");
         this._writer.write("<startDocument/>\n");
         this._writer.flush();
      } catch (IOException var2) {
         throw new SAXException(var2);
      }
   }

   public void endDocument() throws SAXException {
      try {
         this._writer.write("<endDocument/>\n");
         this._writer.write("</sax>");
         this._writer.flush();
         this._writer.close();
      } catch (IOException var2) {
         throw new SAXException(var2);
      }
   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
      if (this._namespaceAttributes == null) {
         this._namespaceAttributes = new ArrayList();
      }

      String qName = prefix.length() == 0 ? "xmlns" : "xmlns" + prefix;
      SAXEventSerializer.AttributeValueHolder attribute = new SAXEventSerializer.AttributeValueHolder(qName, prefix, uri, (String)null, (String)null);
      this._namespaceAttributes.add(attribute);
   }

   public void endPrefixMapping(String prefix) throws SAXException {
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      try {
         this.outputCharacters();
         SAXEventSerializer.AttributeValueHolder[] attrsHolder;
         int attributeCount;
         if (this._namespaceAttributes != null) {
            attrsHolder = new SAXEventSerializer.AttributeValueHolder[0];
            attrsHolder = (SAXEventSerializer.AttributeValueHolder[])((SAXEventSerializer.AttributeValueHolder[])this._namespaceAttributes.toArray(attrsHolder));
            this.quicksort(attrsHolder, 0, attrsHolder.length - 1);

            for(attributeCount = 0; attributeCount < attrsHolder.length; ++attributeCount) {
               this._writer.write("<startPrefixMapping prefix=\"" + attrsHolder[attributeCount].localName + "\" uri=\"" + attrsHolder[attributeCount].uri + "\"/>\n");
               this._writer.flush();
            }

            this._namespaceStack.push(attrsHolder);
            this._namespaceAttributes = null;
         } else {
            this._namespaceStack.push((Object)null);
         }

         attrsHolder = new SAXEventSerializer.AttributeValueHolder[attributes.getLength()];

         for(attributeCount = 0; attributeCount < attributes.getLength(); ++attributeCount) {
            attrsHolder[attributeCount] = new SAXEventSerializer.AttributeValueHolder(attributes.getQName(attributeCount), attributes.getLocalName(attributeCount), attributes.getURI(attributeCount), attributes.getType(attributeCount), attributes.getValue(attributeCount));
         }

         this.quicksort(attrsHolder, 0, attrsHolder.length - 1);
         attributeCount = 0;

         int i;
         for(i = 0; i < attrsHolder.length; ++i) {
            if (!attrsHolder[i].uri.equals("http://www.w3.org/2000/xmlns/")) {
               ++attributeCount;
            }
         }

         if (attributeCount == 0) {
            this._writer.write("<startElement uri=\"" + uri + "\" localName=\"" + localName + "\" qName=\"" + qName + "\"/>\n");
         } else {
            this._writer.write("<startElement uri=\"" + uri + "\" localName=\"" + localName + "\" qName=\"" + qName + "\">\n");

            for(i = 0; i < attrsHolder.length; ++i) {
               if (!attrsHolder[i].uri.equals("http://www.w3.org/2000/xmlns/")) {
                  this._writer.write("  <attribute qName=\"" + attrsHolder[i].qName + "\" localName=\"" + attrsHolder[i].localName + "\" uri=\"" + attrsHolder[i].uri + "\" value=\"" + attrsHolder[i].value + "\"/>\n");
               }
            }

            this._writer.write("</startElement>\n");
            this._writer.flush();
         }
      } catch (IOException var8) {
         throw new SAXException(var8);
      }
   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      try {
         this.outputCharacters();
         this._writer.write("<endElement uri=\"" + uri + "\" localName=\"" + localName + "\" qName=\"" + qName + "\"/>\n");
         this._writer.flush();
         SAXEventSerializer.AttributeValueHolder[] attrsHolder = (SAXEventSerializer.AttributeValueHolder[])((SAXEventSerializer.AttributeValueHolder[])this._namespaceStack.pop());
         if (attrsHolder != null) {
            for(int i = 0; i < attrsHolder.length; ++i) {
               this._writer.write("<endPrefixMapping prefix=\"" + attrsHolder[i].localName + "\"/>\n");
               this._writer.flush();
            }
         }

      } catch (IOException var6) {
         throw new SAXException(var6);
      }
   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      if (length != 0) {
         if (this._characters == null) {
            this._characters = new StringBuffer();
         }

         this._characters.append(ch, start, length);
      }
   }

   private void outputCharacters() throws SAXException {
      if (this._characters != null) {
         try {
            this._writer.write("<characters>" + (this._charactersAreCDATA ? "<![CDATA[" : "") + this._characters + (this._charactersAreCDATA ? "]]>" : "") + "</characters>\n");
            this._writer.flush();
            this._characters = null;
         } catch (IOException var2) {
            throw new SAXException(var2);
         }
      }
   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      this.characters(ch, start, length);
   }

   public void processingInstruction(String target, String data) throws SAXException {
      try {
         this.outputCharacters();
         this._writer.write("<processingInstruction target=\"" + target + "\" data=\"" + data + "\"/>\n");
         this._writer.flush();
      } catch (IOException var4) {
         throw new SAXException(var4);
      }
   }

   public void startDTD(String name, String publicId, String systemId) throws SAXException {
   }

   public void endDTD() throws SAXException {
   }

   public void startEntity(String name) throws SAXException {
   }

   public void endEntity(String name) throws SAXException {
   }

   public void startCDATA() throws SAXException {
      this._charactersAreCDATA = true;
   }

   public void endCDATA() throws SAXException {
      this._charactersAreCDATA = false;
   }

   public void comment(char[] ch, int start, int length) throws SAXException {
      try {
         this.outputCharacters();
         this._writer.write("<comment>" + new String(ch, start, length) + "</comment>\n");
         this._writer.flush();
      } catch (IOException var5) {
         throw new SAXException(var5);
      }
   }

   private void quicksort(SAXEventSerializer.AttributeValueHolder[] attrs, int p, int r) {
      while(p < r) {
         int q = this.partition(attrs, p, r);
         this.quicksort(attrs, p, q);
         p = q + 1;
      }

   }

   private int partition(SAXEventSerializer.AttributeValueHolder[] attrs, int p, int r) {
      SAXEventSerializer.AttributeValueHolder x = attrs[p + r >>> 1];
      int i = p - 1;
      int j = r + 1;

      while(true) {
         while(true) {
            --j;
            if (x.compareTo(attrs[j]) >= 0) {
               do {
                  ++i;
               } while(x.compareTo(attrs[i]) > 0);

               if (i >= j) {
                  return j;
               }

               SAXEventSerializer.AttributeValueHolder t = attrs[i];
               attrs[i] = attrs[j];
               attrs[j] = t;
            }
         }
      }
   }

   public static class AttributeValueHolder implements Comparable {
      public final String qName;
      public final String localName;
      public final String uri;
      public final String type;
      public final String value;

      public AttributeValueHolder(String qName, String localName, String uri, String type, String value) {
         this.qName = qName;
         this.localName = localName;
         this.uri = uri;
         this.type = type;
         this.value = value;
      }

      public int compareTo(Object o) {
         try {
            return this.qName.compareTo(((SAXEventSerializer.AttributeValueHolder)o).qName);
         } catch (Exception var3) {
            throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.AttributeValueHolderExpected"));
         }
      }

      public boolean equals(Object o) {
         try {
            return o instanceof SAXEventSerializer.AttributeValueHolder && this.qName.equals(((SAXEventSerializer.AttributeValueHolder)o).qName);
         } catch (Exception var3) {
            throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.AttributeValueHolderExpected"));
         }
      }

      public int hashCode() {
         int hash = 7;
         int hash = 97 * hash + (this.qName != null ? this.qName.hashCode() : 0);
         return hash;
      }
   }
}
