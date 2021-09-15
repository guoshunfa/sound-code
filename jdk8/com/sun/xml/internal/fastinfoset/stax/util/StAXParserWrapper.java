package com.sun.xml.internal.fastinfoset.stax.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StAXParserWrapper implements XMLStreamReader {
   private XMLStreamReader _reader;

   public StAXParserWrapper() {
   }

   public StAXParserWrapper(XMLStreamReader reader) {
      this._reader = reader;
   }

   public void setReader(XMLStreamReader reader) {
      this._reader = reader;
   }

   public XMLStreamReader getReader() {
      return this._reader;
   }

   public int next() throws XMLStreamException {
      return this._reader.next();
   }

   public int nextTag() throws XMLStreamException {
      return this._reader.nextTag();
   }

   public String getElementText() throws XMLStreamException {
      return this._reader.getElementText();
   }

   public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
      this._reader.require(type, namespaceURI, localName);
   }

   public boolean hasNext() throws XMLStreamException {
      return this._reader.hasNext();
   }

   public void close() throws XMLStreamException {
      this._reader.close();
   }

   public String getNamespaceURI(String prefix) {
      return this._reader.getNamespaceURI(prefix);
   }

   public NamespaceContext getNamespaceContext() {
      return this._reader.getNamespaceContext();
   }

   public boolean isStartElement() {
      return this._reader.isStartElement();
   }

   public boolean isEndElement() {
      return this._reader.isEndElement();
   }

   public boolean isCharacters() {
      return this._reader.isCharacters();
   }

   public boolean isWhiteSpace() {
      return this._reader.isWhiteSpace();
   }

   public QName getAttributeName(int index) {
      return this._reader.getAttributeName(index);
   }

   public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
      return this._reader.getTextCharacters(sourceStart, target, targetStart, length);
   }

   public String getAttributeValue(String namespaceUri, String localName) {
      return this._reader.getAttributeValue(namespaceUri, localName);
   }

   public int getAttributeCount() {
      return this._reader.getAttributeCount();
   }

   public String getAttributePrefix(int index) {
      return this._reader.getAttributePrefix(index);
   }

   public String getAttributeNamespace(int index) {
      return this._reader.getAttributeNamespace(index);
   }

   public String getAttributeLocalName(int index) {
      return this._reader.getAttributeLocalName(index);
   }

   public String getAttributeType(int index) {
      return this._reader.getAttributeType(index);
   }

   public String getAttributeValue(int index) {
      return this._reader.getAttributeValue(index);
   }

   public boolean isAttributeSpecified(int index) {
      return this._reader.isAttributeSpecified(index);
   }

   public int getNamespaceCount() {
      return this._reader.getNamespaceCount();
   }

   public String getNamespacePrefix(int index) {
      return this._reader.getNamespacePrefix(index);
   }

   public String getNamespaceURI(int index) {
      return this._reader.getNamespaceURI(index);
   }

   public int getEventType() {
      return this._reader.getEventType();
   }

   public String getText() {
      return this._reader.getText();
   }

   public char[] getTextCharacters() {
      return this._reader.getTextCharacters();
   }

   public int getTextStart() {
      return this._reader.getTextStart();
   }

   public int getTextLength() {
      return this._reader.getTextLength();
   }

   public String getEncoding() {
      return this._reader.getEncoding();
   }

   public boolean hasText() {
      return this._reader.hasText();
   }

   public Location getLocation() {
      return this._reader.getLocation();
   }

   public QName getName() {
      return this._reader.getName();
   }

   public String getLocalName() {
      return this._reader.getLocalName();
   }

   public boolean hasName() {
      return this._reader.hasName();
   }

   public String getNamespaceURI() {
      return this._reader.getNamespaceURI();
   }

   public String getPrefix() {
      return this._reader.getPrefix();
   }

   public String getVersion() {
      return this._reader.getVersion();
   }

   public boolean isStandalone() {
      return this._reader.isStandalone();
   }

   public boolean standaloneSet() {
      return this._reader.standaloneSet();
   }

   public String getCharacterEncodingScheme() {
      return this._reader.getCharacterEncodingScheme();
   }

   public String getPITarget() {
      return this._reader.getPITarget();
   }

   public String getPIData() {
      return this._reader.getPIData();
   }

   public Object getProperty(String name) {
      return this._reader.getProperty(name);
   }
}
