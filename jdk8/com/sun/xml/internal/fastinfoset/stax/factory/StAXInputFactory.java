package com.sun.xml.internal.fastinfoset.stax.factory;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.internal.fastinfoset.stax.StAXManager;
import com.sun.xml.internal.fastinfoset.stax.events.StAXEventReader;
import com.sun.xml.internal.fastinfoset.stax.events.StAXFilteredEvent;
import com.sun.xml.internal.fastinfoset.stax.util.StAXFilteredParser;
import com.sun.xml.internal.fastinfoset.tools.XML_SAX_FI;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.stream.EventFilter;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.Source;

public class StAXInputFactory extends XMLInputFactory {
   private StAXManager _manager = new StAXManager(1);

   public static XMLInputFactory newInstance() {
      return XMLInputFactory.newInstance();
   }

   public XMLStreamReader createXMLStreamReader(Reader xmlfile) throws XMLStreamException {
      return this.getXMLStreamReader(xmlfile);
   }

   public XMLStreamReader createXMLStreamReader(InputStream s) throws XMLStreamException {
      return new StAXDocumentParser(s, this._manager);
   }

   public XMLStreamReader createXMLStreamReader(String systemId, Reader xmlfile) throws XMLStreamException {
      return this.getXMLStreamReader(xmlfile);
   }

   public XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException {
      return null;
   }

   public XMLStreamReader createXMLStreamReader(String systemId, InputStream inputstream) throws XMLStreamException {
      return this.createXMLStreamReader(inputstream);
   }

   public XMLStreamReader createXMLStreamReader(InputStream inputstream, String encoding) throws XMLStreamException {
      return this.createXMLStreamReader(inputstream);
   }

   XMLStreamReader getXMLStreamReader(String systemId, InputStream inputstream, String encoding) throws XMLStreamException {
      return this.createXMLStreamReader(inputstream);
   }

   XMLStreamReader getXMLStreamReader(Reader xmlfile) throws XMLStreamException {
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      BufferedOutputStream bufferedStream = new BufferedOutputStream(byteStream);
      StAXDocumentParser sr = null;

      try {
         XML_SAX_FI convertor = new XML_SAX_FI();
         convertor.convert(xmlfile, bufferedStream);
         ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteStream.toByteArray());
         InputStream document = new BufferedInputStream(byteInputStream);
         sr = new StAXDocumentParser();
         sr.setInputStream(document);
         sr.setManager(this._manager);
         return sr;
      } catch (Exception var8) {
         return null;
      }
   }

   public XMLEventReader createXMLEventReader(InputStream inputstream) throws XMLStreamException {
      return new StAXEventReader(this.createXMLStreamReader(inputstream));
   }

   public XMLEventReader createXMLEventReader(Reader reader) throws XMLStreamException {
      return new StAXEventReader(this.createXMLStreamReader(reader));
   }

   public XMLEventReader createXMLEventReader(Source source) throws XMLStreamException {
      return new StAXEventReader(this.createXMLStreamReader(source));
   }

   public XMLEventReader createXMLEventReader(String systemId, InputStream inputstream) throws XMLStreamException {
      return new StAXEventReader(this.createXMLStreamReader(systemId, inputstream));
   }

   public XMLEventReader createXMLEventReader(InputStream stream, String encoding) throws XMLStreamException {
      return new StAXEventReader(this.createXMLStreamReader(stream, encoding));
   }

   public XMLEventReader createXMLEventReader(String systemId, Reader reader) throws XMLStreamException {
      return new StAXEventReader(this.createXMLStreamReader(systemId, reader));
   }

   public XMLEventReader createXMLEventReader(XMLStreamReader streamReader) throws XMLStreamException {
      return new StAXEventReader(streamReader);
   }

   public XMLEventAllocator getEventAllocator() {
      return (XMLEventAllocator)this.getProperty("javax.xml.stream.allocator");
   }

   public XMLReporter getXMLReporter() {
      return (XMLReporter)this._manager.getProperty("javax.xml.stream.reporter");
   }

   public XMLResolver getXMLResolver() {
      Object object = this._manager.getProperty("javax.xml.stream.resolver");
      return (XMLResolver)object;
   }

   public void setXMLReporter(XMLReporter xmlreporter) {
      this._manager.setProperty("javax.xml.stream.reporter", xmlreporter);
   }

   public void setXMLResolver(XMLResolver xmlresolver) {
      this._manager.setProperty("javax.xml.stream.resolver", xmlresolver);
   }

   public XMLEventReader createFilteredReader(XMLEventReader reader, EventFilter filter) throws XMLStreamException {
      return new StAXFilteredEvent(reader, filter);
   }

   public XMLStreamReader createFilteredReader(XMLStreamReader reader, StreamFilter filter) throws XMLStreamException {
      return reader != null && filter != null ? new StAXFilteredParser(reader, filter) : null;
   }

   public Object getProperty(String name) throws IllegalArgumentException {
      if (name == null) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.nullPropertyName"));
      } else if (this._manager.containsProperty(name)) {
         return this._manager.getProperty(name);
      } else {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[]{name}));
      }
   }

   public boolean isPropertySupported(String name) {
      return name == null ? false : this._manager.containsProperty(name);
   }

   public void setEventAllocator(XMLEventAllocator allocator) {
      this._manager.setProperty("javax.xml.stream.allocator", allocator);
   }

   public void setProperty(String name, Object value) throws IllegalArgumentException {
      this._manager.setProperty(name, value);
   }
}
