package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;

public class StaxXMLInputSource {
   XMLStreamReader fStreamReader;
   XMLEventReader fEventReader;
   XMLInputSource fInputSource;
   boolean fHasResolver = false;

   public StaxXMLInputSource(XMLStreamReader streamReader) {
      this.fStreamReader = streamReader;
   }

   public StaxXMLInputSource(XMLEventReader eventReader) {
      this.fEventReader = eventReader;
   }

   public StaxXMLInputSource(XMLInputSource inputSource) {
      this.fInputSource = inputSource;
   }

   public StaxXMLInputSource(XMLInputSource inputSource, boolean hasResolver) {
      this.fInputSource = inputSource;
      this.fHasResolver = hasResolver;
   }

   public XMLStreamReader getXMLStreamReader() {
      return this.fStreamReader;
   }

   public XMLEventReader getXMLEventReader() {
      return this.fEventReader;
   }

   public XMLInputSource getXMLInputSource() {
      return this.fInputSource;
   }

   public boolean hasXMLStreamOrXMLEventReader() {
      return this.fStreamReader != null || this.fEventReader != null;
   }

   public boolean hasResolver() {
      return this.fHasResolver;
   }
}
