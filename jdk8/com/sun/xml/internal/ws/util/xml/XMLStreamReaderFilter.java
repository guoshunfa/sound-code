package com.sun.xml.internal.ws.util.xml;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderFilter implements XMLStreamReaderFactory.RecycleAware, XMLStreamReader {
   protected XMLStreamReader reader;

   public XMLStreamReaderFilter(XMLStreamReader core) {
      this.reader = core;
   }

   public void onRecycled() {
      XMLStreamReaderFactory.recycle(this.reader);
      this.reader = null;
   }

   public int getAttributeCount() {
      return this.reader.getAttributeCount();
   }

   public int getEventType() {
      return this.reader.getEventType();
   }

   public int getNamespaceCount() {
      return this.reader.getNamespaceCount();
   }

   public int getTextLength() {
      return this.reader.getTextLength();
   }

   public int getTextStart() {
      return this.reader.getTextStart();
   }

   public int next() throws XMLStreamException {
      return this.reader.next();
   }

   public int nextTag() throws XMLStreamException {
      return this.reader.nextTag();
   }

   public void close() throws XMLStreamException {
      this.reader.close();
   }

   public boolean hasName() {
      return this.reader.hasName();
   }

   public boolean hasNext() throws XMLStreamException {
      return this.reader.hasNext();
   }

   public boolean hasText() {
      return this.reader.hasText();
   }

   public boolean isCharacters() {
      return this.reader.isCharacters();
   }

   public boolean isEndElement() {
      return this.reader.isEndElement();
   }

   public boolean isStandalone() {
      return this.reader.isStandalone();
   }

   public boolean isStartElement() {
      return this.reader.isStartElement();
   }

   public boolean isWhiteSpace() {
      return this.reader.isWhiteSpace();
   }

   public boolean standaloneSet() {
      return this.reader.standaloneSet();
   }

   public char[] getTextCharacters() {
      return this.reader.getTextCharacters();
   }

   public boolean isAttributeSpecified(int index) {
      return this.reader.isAttributeSpecified(index);
   }

   public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
      return this.reader.getTextCharacters(sourceStart, target, targetStart, length);
   }

   public String getCharacterEncodingScheme() {
      return this.reader.getCharacterEncodingScheme();
   }

   public String getElementText() throws XMLStreamException {
      return this.reader.getElementText();
   }

   public String getEncoding() {
      return this.reader.getEncoding();
   }

   public String getLocalName() {
      return this.reader.getLocalName();
   }

   public String getNamespaceURI() {
      return this.reader.getNamespaceURI();
   }

   public String getPIData() {
      return this.reader.getPIData();
   }

   public String getPITarget() {
      return this.reader.getPITarget();
   }

   public String getPrefix() {
      return this.reader.getPrefix();
   }

   public String getText() {
      return this.reader.getText();
   }

   public String getVersion() {
      return this.reader.getVersion();
   }

   public String getAttributeLocalName(int index) {
      return this.reader.getAttributeLocalName(index);
   }

   public String getAttributeNamespace(int index) {
      return this.reader.getAttributeNamespace(index);
   }

   public String getAttributePrefix(int index) {
      return this.reader.getAttributePrefix(index);
   }

   public String getAttributeType(int index) {
      return this.reader.getAttributeType(index);
   }

   public String getAttributeValue(int index) {
      return this.reader.getAttributeValue(index);
   }

   public String getNamespacePrefix(int index) {
      return this.reader.getNamespacePrefix(index);
   }

   public String getNamespaceURI(int index) {
      return this.reader.getNamespaceURI(index);
   }

   public NamespaceContext getNamespaceContext() {
      return this.reader.getNamespaceContext();
   }

   public QName getName() {
      return this.reader.getName();
   }

   public QName getAttributeName(int index) {
      return this.reader.getAttributeName(index);
   }

   public Location getLocation() {
      return this.reader.getLocation();
   }

   public Object getProperty(String name) throws IllegalArgumentException {
      return this.reader.getProperty(name);
   }

   public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
      this.reader.require(type, namespaceURI, localName);
   }

   public String getNamespaceURI(String prefix) {
      return this.reader.getNamespaceURI(prefix);
   }

   public String getAttributeValue(String namespaceURI, String localName) {
      return this.reader.getAttributeValue(namespaceURI, localName);
   }
}
