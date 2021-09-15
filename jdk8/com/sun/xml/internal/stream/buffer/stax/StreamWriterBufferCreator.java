package com.sun.xml.internal.stream.buffer.stax;

import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;

public class StreamWriterBufferCreator extends StreamBufferCreator implements XMLStreamWriterEx {
   private final NamespaceContexHelper namespaceContext = new NamespaceContexHelper();
   private int depth = 0;

   public StreamWriterBufferCreator() {
      this.setXMLStreamBuffer(new MutableXMLStreamBuffer());
   }

   public StreamWriterBufferCreator(MutableXMLStreamBuffer buffer) {
      this.setXMLStreamBuffer(buffer);
   }

   public Object getProperty(String str) throws IllegalArgumentException {
      return null;
   }

   public void close() throws XMLStreamException {
   }

   public void flush() throws XMLStreamException {
   }

   public NamespaceContextEx getNamespaceContext() {
      return this.namespaceContext;
   }

   public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
      throw new UnsupportedOperationException();
   }

   public void setDefaultNamespace(String namespaceURI) throws XMLStreamException {
      this.setPrefix("", namespaceURI);
   }

   public void setPrefix(String prefix, String namespaceURI) throws XMLStreamException {
      this.namespaceContext.declareNamespace(prefix, namespaceURI);
   }

   public String getPrefix(String namespaceURI) throws XMLStreamException {
      return this.namespaceContext.getPrefix(namespaceURI);
   }

   public void writeStartDocument() throws XMLStreamException {
      this.writeStartDocument("", "");
   }

   public void writeStartDocument(String version) throws XMLStreamException {
      this.writeStartDocument("", "");
   }

   public void writeStartDocument(String encoding, String version) throws XMLStreamException {
      this.namespaceContext.resetContexts();
      this.storeStructure(16);
   }

   public void writeEndDocument() throws XMLStreamException {
      this.storeStructure(144);
   }

   public void writeStartElement(String localName) throws XMLStreamException {
      this.namespaceContext.pushContext();
      ++this.depth;
      String defaultNamespaceURI = this.namespaceContext.getNamespaceURI("");
      if (defaultNamespaceURI == null) {
         this.storeQualifiedName(32, (String)null, (String)null, localName);
      } else {
         this.storeQualifiedName(32, (String)null, defaultNamespaceURI, localName);
      }

   }

   public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
      this.namespaceContext.pushContext();
      ++this.depth;
      String prefix = this.namespaceContext.getPrefix(namespaceURI);
      if (prefix == null) {
         throw new XMLStreamException();
      } else {
         this.namespaceContext.pushContext();
         this.storeQualifiedName(32, prefix, namespaceURI, localName);
      }
   }

   public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
      this.namespaceContext.pushContext();
      ++this.depth;
      this.storeQualifiedName(32, prefix, namespaceURI, localName);
   }

   public void writeEmptyElement(String localName) throws XMLStreamException {
      this.writeStartElement(localName);
      this.writeEndElement();
   }

   public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
      this.writeStartElement(namespaceURI, localName);
      this.writeEndElement();
   }

   public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
      this.writeStartElement(prefix, localName, namespaceURI);
      this.writeEndElement();
   }

   public void writeEndElement() throws XMLStreamException {
      this.namespaceContext.popContext();
      this.storeStructure(144);
      if (--this.depth == 0) {
         this.increaseTreeCount();
      }

   }

   public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
      this.storeNamespaceAttribute((String)null, namespaceURI);
   }

   public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
      if ("xmlns".equals(prefix)) {
         prefix = null;
      }

      this.storeNamespaceAttribute(prefix, namespaceURI);
   }

   public void writeAttribute(String localName, String value) throws XMLStreamException {
      this.storeAttribute((String)null, (String)null, localName, "CDATA", value);
   }

   public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
      String prefix = this.namespaceContext.getPrefix(namespaceURI);
      if (prefix == null) {
         throw new XMLStreamException();
      } else {
         this.writeAttribute(prefix, namespaceURI, localName, value);
      }
   }

   public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
      this.storeAttribute(prefix, namespaceURI, localName, "CDATA", value);
   }

   public void writeCData(String data) throws XMLStreamException {
      this.storeStructure(88);
      this.storeContentString(data);
   }

   public void writeCharacters(String charData) throws XMLStreamException {
      this.storeStructure(88);
      this.storeContentString(charData);
   }

   public void writeCharacters(char[] buf, int start, int len) throws XMLStreamException {
      this.storeContentCharacters(80, buf, start, len);
   }

   public void writeComment(String str) throws XMLStreamException {
      this.storeStructure(104);
      this.storeContentString(str);
   }

   public void writeDTD(String str) throws XMLStreamException {
   }

   public void writeEntityRef(String str) throws XMLStreamException {
      this.storeStructure(128);
      this.storeContentString(str);
   }

   public void writeProcessingInstruction(String target) throws XMLStreamException {
      this.writeProcessingInstruction(target, "");
   }

   public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
      this.storeProcessingInstruction(target, data);
   }

   public void writePCDATA(CharSequence charSequence) throws XMLStreamException {
      if (charSequence instanceof Base64Data) {
         this.storeStructure(92);
         this.storeContentObject(((Base64Data)charSequence).clone());
      } else {
         this.writeCharacters(charSequence.toString());
      }

   }

   public void writeBinary(byte[] bytes, int offset, int length, String endpointURL) throws XMLStreamException {
      Base64Data d = new Base64Data();
      byte[] b = new byte[length];
      System.arraycopy(bytes, offset, b, 0, length);
      d.set(b, length, (String)null, true);
      this.storeStructure(92);
      this.storeContentObject(d);
   }

   public void writeBinary(DataHandler dataHandler) throws XMLStreamException {
      Base64Data d = new Base64Data();
      d.set(dataHandler);
      this.storeStructure(92);
      this.storeContentObject(d);
   }

   public OutputStream writeBinary(String endpointURL) throws XMLStreamException {
      throw new UnsupportedOperationException();
   }
}
