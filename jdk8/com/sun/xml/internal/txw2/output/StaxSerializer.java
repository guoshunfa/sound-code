package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TxwException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StaxSerializer implements XmlSerializer {
   private final XMLStreamWriter out;

   public StaxSerializer(XMLStreamWriter writer) {
      this(writer, true);
   }

   public StaxSerializer(XMLStreamWriter writer, boolean indenting) {
      if (indenting) {
         writer = new IndentingXMLStreamWriter((XMLStreamWriter)writer);
      }

      this.out = (XMLStreamWriter)writer;
   }

   public void startDocument() {
      try {
         this.out.writeStartDocument();
      } catch (XMLStreamException var2) {
         throw new TxwException(var2);
      }
   }

   public void beginStartTag(String uri, String localName, String prefix) {
      try {
         this.out.writeStartElement(prefix, localName, uri);
      } catch (XMLStreamException var5) {
         throw new TxwException(var5);
      }
   }

   public void writeAttribute(String uri, String localName, String prefix, StringBuilder value) {
      try {
         this.out.writeAttribute(prefix, uri, localName, value.toString());
      } catch (XMLStreamException var6) {
         throw new TxwException(var6);
      }
   }

   public void writeXmlns(String prefix, String uri) {
      try {
         if (prefix.length() == 0) {
            this.out.setDefaultNamespace(uri);
         } else {
            this.out.setPrefix(prefix, uri);
         }

         this.out.writeNamespace(prefix, uri);
      } catch (XMLStreamException var4) {
         throw new TxwException(var4);
      }
   }

   public void endStartTag(String uri, String localName, String prefix) {
   }

   public void endTag() {
      try {
         this.out.writeEndElement();
      } catch (XMLStreamException var2) {
         throw new TxwException(var2);
      }
   }

   public void text(StringBuilder text) {
      try {
         this.out.writeCharacters(text.toString());
      } catch (XMLStreamException var3) {
         throw new TxwException(var3);
      }
   }

   public void cdata(StringBuilder text) {
      try {
         this.out.writeCData(text.toString());
      } catch (XMLStreamException var3) {
         throw new TxwException(var3);
      }
   }

   public void comment(StringBuilder comment) {
      try {
         this.out.writeComment(comment.toString());
      } catch (XMLStreamException var3) {
         throw new TxwException(var3);
      }
   }

   public void endDocument() {
      try {
         this.out.writeEndDocument();
         this.out.flush();
      } catch (XMLStreamException var2) {
         throw new TxwException(var2);
      }
   }

   public void flush() {
      try {
         this.out.flush();
      } catch (XMLStreamException var2) {
         throw new TxwException(var2);
      }
   }
}
