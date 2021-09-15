package com.sun.org.apache.xalan.internal.xsltc.trax;

import java.util.Vector;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public abstract class SAX2StAXBaseWriter extends DefaultHandler implements LexicalHandler {
   protected boolean isCDATA;
   protected StringBuffer CDATABuffer;
   protected Vector namespaces;
   protected Locator docLocator;
   protected XMLReporter reporter;

   public SAX2StAXBaseWriter() {
   }

   public SAX2StAXBaseWriter(XMLReporter reporter) {
      this.reporter = reporter;
   }

   public void setXMLReporter(XMLReporter reporter) {
      this.reporter = reporter;
   }

   public void setDocumentLocator(Locator locator) {
      this.docLocator = locator;
   }

   public Location getCurrentLocation() {
      return this.docLocator != null ? new SAX2StAXBaseWriter.SAXLocation(this.docLocator) : null;
   }

   public void error(SAXParseException e) throws SAXException {
      this.reportException("ERROR", e);
   }

   public void fatalError(SAXParseException e) throws SAXException {
      this.reportException("FATAL", e);
   }

   public void warning(SAXParseException e) throws SAXException {
      this.reportException("WARNING", e);
   }

   public void startDocument() throws SAXException {
      this.namespaces = new Vector(2);
   }

   public void endDocument() throws SAXException {
      this.namespaces = null;
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      this.namespaces = null;
   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      this.namespaces = null;
   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
      if (prefix == null) {
         prefix = "";
      } else if (prefix.equals("xml")) {
         return;
      }

      if (this.namespaces == null) {
         this.namespaces = new Vector(2);
      }

      this.namespaces.addElement(prefix);
      this.namespaces.addElement(uri);
   }

   public void endPrefixMapping(String prefix) throws SAXException {
   }

   public void startCDATA() throws SAXException {
      this.isCDATA = true;
      if (this.CDATABuffer == null) {
         this.CDATABuffer = new StringBuffer();
      } else {
         this.CDATABuffer.setLength(0);
      }

   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      if (this.isCDATA) {
         this.CDATABuffer.append(ch, start, length);
      }

   }

   public void endCDATA() throws SAXException {
      this.isCDATA = false;
      this.CDATABuffer.setLength(0);
   }

   public void comment(char[] ch, int start, int length) throws SAXException {
   }

   public void endDTD() throws SAXException {
   }

   public void endEntity(String name) throws SAXException {
   }

   public void startDTD(String name, String publicId, String systemId) throws SAXException {
   }

   public void startEntity(String name) throws SAXException {
   }

   protected void reportException(String type, SAXException e) throws SAXException {
      if (this.reporter != null) {
         try {
            this.reporter.report(e.getMessage(), type, e, this.getCurrentLocation());
         } catch (XMLStreamException var4) {
            throw new SAXException(var4);
         }
      }

   }

   public static final void parseQName(String qName, String[] results) {
      int idx = qName.indexOf(58);
      String prefix;
      String local;
      if (idx >= 0) {
         prefix = qName.substring(0, idx);
         local = qName.substring(idx + 1);
      } else {
         prefix = "";
         local = qName;
      }

      results[0] = prefix;
      results[1] = local;
   }

   private static final class SAXLocation implements Location {
      private int lineNumber;
      private int columnNumber;
      private String publicId;
      private String systemId;

      private SAXLocation(Locator locator) {
         this.lineNumber = locator.getLineNumber();
         this.columnNumber = locator.getColumnNumber();
         this.publicId = locator.getPublicId();
         this.systemId = locator.getSystemId();
      }

      public int getLineNumber() {
         return this.lineNumber;
      }

      public int getColumnNumber() {
         return this.columnNumber;
      }

      public int getCharacterOffset() {
         return -1;
      }

      public String getPublicId() {
         return this.publicId;
      }

      public String getSystemId() {
         return this.systemId;
      }

      // $FF: synthetic method
      SAXLocation(Locator x0, Object x1) {
         this(x0);
      }
   }
}
