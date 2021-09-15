package com.sun.xml.internal.txw2.output;

import java.io.Writer;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DataWriter extends XMLWriter {
   private static final Object SEEN_NOTHING = new Object();
   private static final Object SEEN_ELEMENT = new Object();
   private static final Object SEEN_DATA = new Object();
   private Object state;
   private Stack stateStack;
   private String indentStep;
   private int depth;

   public DataWriter(Writer writer, String encoding, CharacterEscapeHandler _escapeHandler) {
      super(writer, encoding, _escapeHandler);
      this.state = SEEN_NOTHING;
      this.stateStack = new Stack();
      this.indentStep = "";
      this.depth = 0;
   }

   public DataWriter(Writer writer, String encoding) {
      this(writer, encoding, DumbEscapeHandler.theInstance);
   }

   public DataWriter(Writer writer) {
      this(writer, (String)null, DumbEscapeHandler.theInstance);
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

   public void reset() {
      this.depth = 0;
      this.state = SEEN_NOTHING;
      this.stateStack = new Stack();
      super.reset();
   }

   public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
      this.stateStack.push(SEEN_ELEMENT);
      this.state = SEEN_NOTHING;
      if (this.depth > 0) {
         super.characters("\n");
      }

      this.doIndent();
      super.startElement(uri, localName, qName, atts);
      ++this.depth;
   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      --this.depth;
      if (this.state == SEEN_ELEMENT) {
         super.characters("\n");
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
         super.characters("\n");
      }

      this.doIndent();
      super.comment(ch, start, length);
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
