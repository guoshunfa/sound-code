package com.sun.xml.internal.txw2.output;

import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class IndentingXMLFilter extends XMLFilterImpl implements LexicalHandler {
   private LexicalHandler lexical;
   private static final char[] NEWLINE = new char[]{'\n'};
   private static final Object SEEN_NOTHING = new Object();
   private static final Object SEEN_ELEMENT = new Object();
   private static final Object SEEN_DATA = new Object();
   private Object state;
   private Stack<Object> stateStack;
   private String indentStep;
   private int depth;

   public IndentingXMLFilter() {
      this.state = SEEN_NOTHING;
      this.stateStack = new Stack();
      this.indentStep = "";
      this.depth = 0;
   }

   public IndentingXMLFilter(ContentHandler handler) {
      this.state = SEEN_NOTHING;
      this.stateStack = new Stack();
      this.indentStep = "";
      this.depth = 0;
      this.setContentHandler(handler);
   }

   public IndentingXMLFilter(ContentHandler handler, LexicalHandler lexical) {
      this.state = SEEN_NOTHING;
      this.stateStack = new Stack();
      this.indentStep = "";
      this.depth = 0;
      this.setContentHandler(handler);
      this.setLexicalHandler(lexical);
   }

   public LexicalHandler getLexicalHandler() {
      return this.lexical;
   }

   public void setLexicalHandler(LexicalHandler lexical) {
      this.lexical = lexical;
   }

   /** @deprecated */
   public int getIndentStep() {
      return this.indentStep.length();
   }

   /** @deprecated */
   public void setIndentStep(int indentStep) {
      StringBuilder s;
      for(s = new StringBuilder(); indentStep > 0; --indentStep) {
         s.append(' ');
      }

      this.setIndentStep(s.toString());
   }

   public void setIndentStep(String s) {
      this.indentStep = s;
   }

   public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
      this.stateStack.push(SEEN_ELEMENT);
      this.state = SEEN_NOTHING;
      if (this.depth > 0) {
         this.writeNewLine();
      }

      this.doIndent();
      super.startElement(uri, localName, qName, atts);
      ++this.depth;
   }

   private void writeNewLine() throws SAXException {
      super.characters(NEWLINE, 0, NEWLINE.length);
   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      --this.depth;
      if (this.state == SEEN_ELEMENT) {
         this.writeNewLine();
         this.doIndent();
      }

      super.endElement(uri, localName, qName);
      this.state = this.stateStack.pop();
   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      this.state = SEEN_DATA;
      super.characters(ch, start, length);
   }

   public void comment(char[] ch, int start, int length) throws SAXException {
      if (this.depth > 0) {
         this.writeNewLine();
      }

      this.doIndent();
      if (this.lexical != null) {
         this.lexical.comment(ch, start, length);
      }

   }

   public void startDTD(String name, String publicId, String systemId) throws SAXException {
      if (this.lexical != null) {
         this.lexical.startDTD(name, publicId, systemId);
      }

   }

   public void endDTD() throws SAXException {
      if (this.lexical != null) {
         this.lexical.endDTD();
      }

   }

   public void startEntity(String name) throws SAXException {
      if (this.lexical != null) {
         this.lexical.startEntity(name);
      }

   }

   public void endEntity(String name) throws SAXException {
      if (this.lexical != null) {
         this.lexical.endEntity(name);
      }

   }

   public void startCDATA() throws SAXException {
      if (this.lexical != null) {
         this.lexical.startCDATA();
      }

   }

   public void endCDATA() throws SAXException {
      if (this.lexical != null) {
         this.lexical.endCDATA();
      }

   }

   private void doIndent() throws SAXException {
      if (this.depth > 0) {
         char[] ch = this.indentStep.toCharArray();

         for(int i = 0; i < this.depth; ++i) {
            this.characters(ch, 0, ch.length);
         }
      }

   }
}
