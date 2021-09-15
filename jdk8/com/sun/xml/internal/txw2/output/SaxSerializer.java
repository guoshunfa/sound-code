package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TxwException;
import java.util.Stack;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class SaxSerializer implements XmlSerializer {
   private final ContentHandler writer;
   private final LexicalHandler lexical;
   private final Stack<String> prefixBindings;
   private final Stack<String> elementBindings;
   private final AttributesImpl attrs;

   public SaxSerializer(ContentHandler handler) {
      this(handler, (LexicalHandler)null, true);
   }

   public SaxSerializer(ContentHandler handler, LexicalHandler lex) {
      this(handler, lex, true);
   }

   public SaxSerializer(ContentHandler handler, LexicalHandler lex, boolean indenting) {
      this.prefixBindings = new Stack();
      this.elementBindings = new Stack();
      this.attrs = new AttributesImpl();
      if (!indenting) {
         this.writer = handler;
         this.lexical = lex;
      } else {
         IndentingXMLFilter indenter = new IndentingXMLFilter(handler, lex);
         this.writer = indenter;
         this.lexical = indenter;
      }

   }

   public SaxSerializer(SAXResult result) {
      this(result.getHandler(), result.getLexicalHandler());
   }

   public void startDocument() {
      try {
         this.writer.startDocument();
      } catch (SAXException var2) {
         throw new TxwException(var2);
      }
   }

   public void writeXmlns(String prefix, String uri) {
      if (prefix == null) {
         prefix = "";
      }

      if (!prefix.equals("xml")) {
         this.prefixBindings.add(uri);
         this.prefixBindings.add(prefix);
      }
   }

   public void beginStartTag(String uri, String localName, String prefix) {
      this.elementBindings.add(getQName(prefix, localName));
      this.elementBindings.add(localName);
      this.elementBindings.add(uri);
   }

   public void writeAttribute(String uri, String localName, String prefix, StringBuilder value) {
      this.attrs.addAttribute(uri, localName, getQName(prefix, localName), "CDATA", value.toString());
   }

   public void endStartTag(String uri, String localName, String prefix) {
      try {
         while(this.prefixBindings.size() != 0) {
            this.writer.startPrefixMapping((String)this.prefixBindings.pop(), (String)this.prefixBindings.pop());
         }

         this.writer.startElement(uri, localName, getQName(prefix, localName), this.attrs);
         this.attrs.clear();
      } catch (SAXException var5) {
         throw new TxwException(var5);
      }
   }

   public void endTag() {
      try {
         this.writer.endElement((String)this.elementBindings.pop(), (String)this.elementBindings.pop(), (String)this.elementBindings.pop());
      } catch (SAXException var2) {
         throw new TxwException(var2);
      }
   }

   public void text(StringBuilder text) {
      try {
         this.writer.characters(text.toString().toCharArray(), 0, text.length());
      } catch (SAXException var3) {
         throw new TxwException(var3);
      }
   }

   public void cdata(StringBuilder text) {
      if (this.lexical == null) {
         throw new UnsupportedOperationException("LexicalHandler is needed to write PCDATA");
      } else {
         try {
            this.lexical.startCDATA();
            this.text(text);
            this.lexical.endCDATA();
         } catch (SAXException var3) {
            throw new TxwException(var3);
         }
      }
   }

   public void comment(StringBuilder comment) {
      try {
         if (this.lexical == null) {
            throw new UnsupportedOperationException("LexicalHandler is needed to write comments");
         } else {
            this.lexical.comment(comment.toString().toCharArray(), 0, comment.length());
         }
      } catch (SAXException var3) {
         throw new TxwException(var3);
      }
   }

   public void endDocument() {
      try {
         this.writer.endDocument();
      } catch (SAXException var2) {
         throw new TxwException(var2);
      }
   }

   public void flush() {
   }

   private static String getQName(String prefix, String localName) {
      String qName;
      if (prefix != null && prefix.length() != 0) {
         qName = prefix + ':' + localName;
      } else {
         qName = localName;
      }

      return qName;
   }
}
