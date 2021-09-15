package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import com.sun.org.apache.xerces.internal.impl.XMLStreamFilterImpl;
import com.sun.org.apache.xerces.internal.impl.XMLStreamReaderImpl;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
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
import javax.xml.transform.stream.StreamSource;

public class XMLInputFactoryImpl extends XMLInputFactory {
   private PropertyManager fPropertyManager = new PropertyManager(1);
   private static final boolean DEBUG = false;
   private XMLStreamReaderImpl fTempReader = null;
   boolean fPropertyChanged = false;
   boolean fReuseInstance = false;

   void initEventReader() {
      this.fPropertyChanged = true;
   }

   public XMLEventReader createXMLEventReader(InputStream inputstream) throws XMLStreamException {
      this.initEventReader();
      return new XMLEventReaderImpl(this.createXMLStreamReader(inputstream));
   }

   public XMLEventReader createXMLEventReader(Reader reader) throws XMLStreamException {
      this.initEventReader();
      return new XMLEventReaderImpl(this.createXMLStreamReader(reader));
   }

   public XMLEventReader createXMLEventReader(Source source) throws XMLStreamException {
      this.initEventReader();
      return new XMLEventReaderImpl(this.createXMLStreamReader(source));
   }

   public XMLEventReader createXMLEventReader(String systemId, InputStream inputstream) throws XMLStreamException {
      this.initEventReader();
      return new XMLEventReaderImpl(this.createXMLStreamReader(systemId, inputstream));
   }

   public XMLEventReader createXMLEventReader(InputStream stream, String encoding) throws XMLStreamException {
      this.initEventReader();
      return new XMLEventReaderImpl(this.createXMLStreamReader(stream, encoding));
   }

   public XMLEventReader createXMLEventReader(String systemId, Reader reader) throws XMLStreamException {
      this.initEventReader();
      return new XMLEventReaderImpl(this.createXMLStreamReader(systemId, reader));
   }

   public XMLEventReader createXMLEventReader(XMLStreamReader reader) throws XMLStreamException {
      return new XMLEventReaderImpl(reader);
   }

   public XMLStreamReader createXMLStreamReader(InputStream inputstream) throws XMLStreamException {
      XMLInputSource inputSource = new XMLInputSource((String)null, (String)null, (String)null, inputstream, (String)null);
      return this.getXMLStreamReaderImpl(inputSource);
   }

   public XMLStreamReader createXMLStreamReader(Reader reader) throws XMLStreamException {
      XMLInputSource inputSource = new XMLInputSource((String)null, (String)null, (String)null, reader, (String)null);
      return this.getXMLStreamReaderImpl(inputSource);
   }

   public XMLStreamReader createXMLStreamReader(String systemId, Reader reader) throws XMLStreamException {
      XMLInputSource inputSource = new XMLInputSource((String)null, systemId, (String)null, reader, (String)null);
      return this.getXMLStreamReaderImpl(inputSource);
   }

   public XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException {
      return new XMLStreamReaderImpl(this.jaxpSourcetoXMLInputSource(source), new PropertyManager(this.fPropertyManager));
   }

   public XMLStreamReader createXMLStreamReader(String systemId, InputStream inputstream) throws XMLStreamException {
      XMLInputSource inputSource = new XMLInputSource((String)null, systemId, (String)null, inputstream, (String)null);
      return this.getXMLStreamReaderImpl(inputSource);
   }

   public XMLStreamReader createXMLStreamReader(InputStream inputstream, String encoding) throws XMLStreamException {
      XMLInputSource inputSource = new XMLInputSource((String)null, (String)null, (String)null, inputstream, encoding);
      return this.getXMLStreamReaderImpl(inputSource);
   }

   public XMLEventAllocator getEventAllocator() {
      return (XMLEventAllocator)this.getProperty("javax.xml.stream.allocator");
   }

   public XMLReporter getXMLReporter() {
      return (XMLReporter)this.fPropertyManager.getProperty("javax.xml.stream.reporter");
   }

   public XMLResolver getXMLResolver() {
      Object object = this.fPropertyManager.getProperty("javax.xml.stream.resolver");
      return (XMLResolver)object;
   }

   public void setXMLReporter(XMLReporter xmlreporter) {
      this.fPropertyManager.setProperty("javax.xml.stream.reporter", xmlreporter);
   }

   public void setXMLResolver(XMLResolver xmlresolver) {
      this.fPropertyManager.setProperty("javax.xml.stream.resolver", xmlresolver);
   }

   public XMLEventReader createFilteredReader(XMLEventReader reader, EventFilter filter) throws XMLStreamException {
      return new EventFilterSupport(reader, filter);
   }

   public XMLStreamReader createFilteredReader(XMLStreamReader reader, StreamFilter filter) throws XMLStreamException {
      return reader != null && filter != null ? new XMLStreamFilterImpl(reader, filter) : null;
   }

   public Object getProperty(String name) throws IllegalArgumentException {
      if (name == null) {
         throw new IllegalArgumentException("Property not supported");
      } else if (this.fPropertyManager.containsProperty(name)) {
         return this.fPropertyManager.getProperty(name);
      } else {
         throw new IllegalArgumentException("Property not supported");
      }
   }

   public boolean isPropertySupported(String name) {
      return name == null ? false : this.fPropertyManager.containsProperty(name);
   }

   public void setEventAllocator(XMLEventAllocator allocator) {
      this.fPropertyManager.setProperty("javax.xml.stream.allocator", allocator);
   }

   public void setProperty(String name, Object value) throws IllegalArgumentException {
      if (name != null && value != null && this.fPropertyManager.containsProperty(name)) {
         if (name != "reuse-instance" && !name.equals("reuse-instance")) {
            this.fPropertyChanged = true;
         } else {
            this.fReuseInstance = (Boolean)value;
         }

         this.fPropertyManager.setProperty(name, value);
      } else {
         throw new IllegalArgumentException("Property " + name + " is not supported");
      }
   }

   XMLStreamReader getXMLStreamReaderImpl(XMLInputSource inputSource) throws XMLStreamException {
      if (this.fTempReader == null) {
         this.fPropertyChanged = false;
         return this.fTempReader = new XMLStreamReaderImpl(inputSource, new PropertyManager(this.fPropertyManager));
      } else if (this.fReuseInstance && this.fTempReader.canReuse() && !this.fPropertyChanged) {
         this.fTempReader.reset();
         this.fTempReader.setInputSource(inputSource);
         this.fPropertyChanged = false;
         return this.fTempReader;
      } else {
         this.fPropertyChanged = false;
         return this.fTempReader = new XMLStreamReaderImpl(inputSource, new PropertyManager(this.fPropertyManager));
      }
   }

   XMLInputSource jaxpSourcetoXMLInputSource(Source source) {
      if (source instanceof StreamSource) {
         StreamSource stSource = (StreamSource)source;
         String systemId = stSource.getSystemId();
         String publicId = stSource.getPublicId();
         InputStream istream = stSource.getInputStream();
         Reader reader = stSource.getReader();
         if (istream != null) {
            return new XMLInputSource(publicId, systemId, (String)null, istream, (String)null);
         } else {
            return reader != null ? new XMLInputSource(publicId, systemId, (String)null, reader, (String)null) : new XMLInputSource(publicId, systemId, (String)null);
         }
      } else {
         throw new UnsupportedOperationException("Cannot create XMLStreamReader or XMLEventReader from a " + source.getClass().getName());
      }
   }
}
