package com.sun.org.apache.xerces.internal.impl;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamFilterImpl implements XMLStreamReader {
   private StreamFilter fStreamFilter = null;
   private XMLStreamReader fStreamReader = null;
   private int fCurrentEvent;
   private boolean fEventAccepted = false;
   private boolean fStreamAdvancedByHasNext = false;

   public XMLStreamFilterImpl(XMLStreamReader reader, StreamFilter filter) {
      this.fStreamReader = reader;
      this.fStreamFilter = filter;

      try {
         if (this.fStreamFilter.accept(this.fStreamReader)) {
            this.fEventAccepted = true;
         } else {
            this.findNextEvent();
         }
      } catch (XMLStreamException var4) {
         System.err.println("Error while creating a stream Filter" + var4);
      }

   }

   protected void setStreamFilter(StreamFilter sf) {
      this.fStreamFilter = sf;
   }

   public int next() throws XMLStreamException {
      if (this.fStreamAdvancedByHasNext && this.fEventAccepted) {
         this.fStreamAdvancedByHasNext = false;
         return this.fCurrentEvent;
      } else {
         int event = this.findNextEvent();
         if (event != -1) {
            return event;
         } else {
            throw new IllegalStateException("The stream reader has reached the end of the document, or there are no more  items to return");
         }
      }
   }

   public int nextTag() throws XMLStreamException {
      if (!this.fStreamAdvancedByHasNext || !this.fEventAccepted || this.fCurrentEvent != 1 && this.fCurrentEvent != 1) {
         int event = this.findNextTag();
         if (event != -1) {
            return event;
         } else {
            throw new IllegalStateException("The stream reader has reached the end of the document, or there are no more  items to return");
         }
      } else {
         this.fStreamAdvancedByHasNext = false;
         return this.fCurrentEvent;
      }
   }

   public boolean hasNext() throws XMLStreamException {
      if (this.fStreamReader.hasNext()) {
         if (!this.fEventAccepted) {
            if ((this.fCurrentEvent = this.findNextEvent()) == -1) {
               return false;
            }

            this.fStreamAdvancedByHasNext = true;
         }

         return true;
      } else {
         return false;
      }
   }

   private int findNextEvent() throws XMLStreamException {
      this.fStreamAdvancedByHasNext = false;

      do {
         if (!this.fStreamReader.hasNext()) {
            if (this.fCurrentEvent == 8) {
               return this.fCurrentEvent;
            }

            return -1;
         }

         this.fCurrentEvent = this.fStreamReader.next();
      } while(!this.fStreamFilter.accept(this.fStreamReader));

      this.fEventAccepted = true;
      return this.fCurrentEvent;
   }

   private int findNextTag() throws XMLStreamException {
      this.fStreamAdvancedByHasNext = false;

      do {
         if (!this.fStreamReader.hasNext()) {
            if (this.fCurrentEvent == 8) {
               return this.fCurrentEvent;
            }

            return -1;
         }

         this.fCurrentEvent = this.fStreamReader.nextTag();
      } while(!this.fStreamFilter.accept(this.fStreamReader));

      this.fEventAccepted = true;
      return this.fCurrentEvent;
   }

   public void close() throws XMLStreamException {
      this.fStreamReader.close();
   }

   public int getAttributeCount() {
      return this.fStreamReader.getAttributeCount();
   }

   public QName getAttributeName(int index) {
      return this.fStreamReader.getAttributeName(index);
   }

   public String getAttributeNamespace(int index) {
      return this.fStreamReader.getAttributeNamespace(index);
   }

   public String getAttributePrefix(int index) {
      return this.fStreamReader.getAttributePrefix(index);
   }

   public String getAttributeType(int index) {
      return this.fStreamReader.getAttributeType(index);
   }

   public String getAttributeValue(int index) {
      return this.fStreamReader.getAttributeValue(index);
   }

   public String getAttributeValue(String namespaceURI, String localName) {
      return this.fStreamReader.getAttributeValue(namespaceURI, localName);
   }

   public String getCharacterEncodingScheme() {
      return this.fStreamReader.getCharacterEncodingScheme();
   }

   public String getElementText() throws XMLStreamException {
      return this.fStreamReader.getElementText();
   }

   public String getEncoding() {
      return this.fStreamReader.getEncoding();
   }

   public int getEventType() {
      return this.fStreamReader.getEventType();
   }

   public String getLocalName() {
      return this.fStreamReader.getLocalName();
   }

   public Location getLocation() {
      return this.fStreamReader.getLocation();
   }

   public QName getName() {
      return this.fStreamReader.getName();
   }

   public NamespaceContext getNamespaceContext() {
      return this.fStreamReader.getNamespaceContext();
   }

   public int getNamespaceCount() {
      return this.fStreamReader.getNamespaceCount();
   }

   public String getNamespacePrefix(int index) {
      return this.fStreamReader.getNamespacePrefix(index);
   }

   public String getNamespaceURI() {
      return this.fStreamReader.getNamespaceURI();
   }

   public String getNamespaceURI(int index) {
      return this.fStreamReader.getNamespaceURI(index);
   }

   public String getNamespaceURI(String prefix) {
      return this.fStreamReader.getNamespaceURI(prefix);
   }

   public String getPIData() {
      return this.fStreamReader.getPIData();
   }

   public String getPITarget() {
      return this.fStreamReader.getPITarget();
   }

   public String getPrefix() {
      return this.fStreamReader.getPrefix();
   }

   public Object getProperty(String name) throws IllegalArgumentException {
      return this.fStreamReader.getProperty(name);
   }

   public String getText() {
      return this.fStreamReader.getText();
   }

   public char[] getTextCharacters() {
      return this.fStreamReader.getTextCharacters();
   }

   public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
      return this.fStreamReader.getTextCharacters(sourceStart, target, targetStart, length);
   }

   public int getTextLength() {
      return this.fStreamReader.getTextLength();
   }

   public int getTextStart() {
      return this.fStreamReader.getTextStart();
   }

   public String getVersion() {
      return this.fStreamReader.getVersion();
   }

   public boolean hasName() {
      return this.fStreamReader.hasName();
   }

   public boolean hasText() {
      return this.fStreamReader.hasText();
   }

   public boolean isAttributeSpecified(int index) {
      return this.fStreamReader.isAttributeSpecified(index);
   }

   public boolean isCharacters() {
      return this.fStreamReader.isCharacters();
   }

   public boolean isEndElement() {
      return this.fStreamReader.isEndElement();
   }

   public boolean isStandalone() {
      return this.fStreamReader.isStandalone();
   }

   public boolean isStartElement() {
      return this.fStreamReader.isStartElement();
   }

   public boolean isWhiteSpace() {
      return this.fStreamReader.isWhiteSpace();
   }

   public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
      this.fStreamReader.require(type, namespaceURI, localName);
   }

   public boolean standaloneSet() {
      return this.fStreamReader.standaloneSet();
   }

   public String getAttributeLocalName(int index) {
      return this.fStreamReader.getAttributeLocalName(index);
   }
}
