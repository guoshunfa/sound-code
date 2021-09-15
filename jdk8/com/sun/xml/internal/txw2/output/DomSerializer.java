package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TxwException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ext.LexicalHandler;

public class DomSerializer implements XmlSerializer {
   private final SaxSerializer serializer;

   public DomSerializer(Node node) {
      Dom2SaxAdapter adapter = new Dom2SaxAdapter(node);
      this.serializer = new SaxSerializer(adapter, adapter, false);
   }

   public DomSerializer(DOMResult domResult) {
      Node node = domResult.getNode();
      if (node == null) {
         try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            domResult.setNode(doc);
            this.serializer = new SaxSerializer(new Dom2SaxAdapter(doc), (LexicalHandler)null, false);
         } catch (ParserConfigurationException var6) {
            throw new TxwException(var6);
         }
      } else {
         this.serializer = new SaxSerializer(new Dom2SaxAdapter(node), (LexicalHandler)null, false);
      }

   }

   public void startDocument() {
      this.serializer.startDocument();
   }

   public void beginStartTag(String uri, String localName, String prefix) {
      this.serializer.beginStartTag(uri, localName, prefix);
   }

   public void writeAttribute(String uri, String localName, String prefix, StringBuilder value) {
      this.serializer.writeAttribute(uri, localName, prefix, value);
   }

   public void writeXmlns(String prefix, String uri) {
      this.serializer.writeXmlns(prefix, uri);
   }

   public void endStartTag(String uri, String localName, String prefix) {
      this.serializer.endStartTag(uri, localName, prefix);
   }

   public void endTag() {
      this.serializer.endTag();
   }

   public void text(StringBuilder text) {
      this.serializer.text(text);
   }

   public void cdata(StringBuilder text) {
      this.serializer.cdata(text);
   }

   public void comment(StringBuilder comment) {
      this.serializer.comment(comment);
   }

   public void endDocument() {
      this.serializer.endDocument();
   }

   public void flush() {
   }
}
