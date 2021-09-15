package com.sun.org.apache.xml.internal.serialize;

import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class TextSerializer extends BaseMarkupSerializer {
   public TextSerializer() {
      super(new OutputFormat("text", (String)null, false));
   }

   public void setOutputFormat(OutputFormat format) {
      super.setOutputFormat(format != null ? format : new OutputFormat("text", (String)null, false));
   }

   public void startElement(String namespaceURI, String localName, String rawName, Attributes attrs) throws SAXException {
      this.startElement(rawName == null ? localName : rawName, (AttributeList)null);
   }

   public void endElement(String namespaceURI, String localName, String rawName) throws SAXException {
      this.endElement(rawName == null ? localName : rawName);
   }

   public void startElement(String tagName, AttributeList attrs) throws SAXException {
      try {
         ElementState state = this.getElementState();
         if (this.isDocumentState() && !this._started) {
            this.startDocument(tagName);
         }

         boolean preserveSpace = state.preserveSpace;
         this.enterElementState((String)null, (String)null, tagName, preserveSpace);
      } catch (IOException var6) {
         throw new SAXException(var6);
      }
   }

   public void endElement(String tagName) throws SAXException {
      try {
         this.endElementIO(tagName);
      } catch (IOException var3) {
         throw new SAXException(var3);
      }
   }

   public void endElementIO(String tagName) throws IOException {
      ElementState state = this.getElementState();
      state = this.leaveElementState();
      state.afterElement = true;
      state.empty = false;
      if (this.isDocumentState()) {
         this._printer.flush();
      }

   }

   public void processingInstructionIO(String target, String code) throws IOException {
   }

   public void comment(String text) {
   }

   public void comment(char[] chars, int start, int length) {
   }

   public void characters(char[] chars, int start, int length) throws SAXException {
      try {
         ElementState state = this.content();
         state.doCData = state.inCData = false;
         this.printText(chars, start, length, true, true);
      } catch (IOException var6) {
         throw new SAXException(var6);
      }
   }

   protected void characters(String text, boolean unescaped) throws IOException {
      ElementState state = this.content();
      state.doCData = state.inCData = false;
      this.printText(text, true, true);
   }

   protected void startDocument(String rootTagName) throws IOException {
      this._printer.leaveDTD();
      this._started = true;
      this.serializePreRoot();
   }

   protected void serializeElement(Element elem) throws IOException {
      String tagName = elem.getTagName();
      ElementState state = this.getElementState();
      if (this.isDocumentState() && !this._started) {
         this.startDocument(tagName);
      }

      boolean preserveSpace = state.preserveSpace;
      if (elem.hasChildNodes()) {
         this.enterElementState((String)null, (String)null, tagName, preserveSpace);

         for(Node child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
            this.serializeNode(child);
         }

         this.endElementIO(tagName);
      } else if (!this.isDocumentState()) {
         state.afterElement = true;
         state.empty = false;
      }

   }

   protected void serializeNode(Node node) throws IOException {
      String text;
      switch(node.getNodeType()) {
      case 1:
         this.serializeElement((Element)node);
      case 2:
      case 5:
      case 6:
      case 7:
      case 8:
      case 10:
      default:
         break;
      case 3:
         text = node.getNodeValue();
         if (text != null) {
            this.characters(node.getNodeValue(), true);
         }
         break;
      case 4:
         text = node.getNodeValue();
         if (text != null) {
            this.characters(node.getNodeValue(), true);
         }
         break;
      case 9:
      case 11:
         for(Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            this.serializeNode(child);
         }
      }

   }

   protected ElementState content() {
      ElementState state = this.getElementState();
      if (!this.isDocumentState()) {
         if (state.empty) {
            state.empty = false;
         }

         state.afterElement = false;
      }

      return state;
   }

   protected String getEntityRef(int ch) {
      return null;
   }
}
