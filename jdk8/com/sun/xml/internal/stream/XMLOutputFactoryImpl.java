package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import com.sun.xml.internal.stream.writers.XMLDOMWriterImpl;
import com.sun.xml.internal.stream.writers.XMLEventWriterImpl;
import com.sun.xml.internal.stream.writers.XMLStreamWriterImpl;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stream.StreamResult;

public class XMLOutputFactoryImpl extends XMLOutputFactory {
   private PropertyManager fPropertyManager = new PropertyManager(2);
   private XMLStreamWriterImpl fStreamWriter = null;
   boolean fReuseInstance = false;
   private static final boolean DEBUG = false;
   private boolean fPropertyChanged;

   public XMLEventWriter createXMLEventWriter(OutputStream outputStream) throws XMLStreamException {
      return this.createXMLEventWriter(outputStream, (String)null);
   }

   public XMLEventWriter createXMLEventWriter(OutputStream outputStream, String encoding) throws XMLStreamException {
      return new XMLEventWriterImpl(this.createXMLStreamWriter(outputStream, encoding));
   }

   public XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
      return (XMLEventWriter)(result instanceof StAXResult && ((StAXResult)result).getXMLEventWriter() != null ? ((StAXResult)result).getXMLEventWriter() : new XMLEventWriterImpl(this.createXMLStreamWriter(result)));
   }

   public XMLEventWriter createXMLEventWriter(Writer writer) throws XMLStreamException {
      return new XMLEventWriterImpl(this.createXMLStreamWriter(writer));
   }

   public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
      if (result instanceof StreamResult) {
         return this.createXMLStreamWriter((StreamResult)((StreamResult)result), (String)null);
      } else if (result instanceof DOMResult) {
         return new XMLDOMWriterImpl((DOMResult)result);
      } else if (result instanceof StAXResult) {
         if (((StAXResult)result).getXMLStreamWriter() != null) {
            return ((StAXResult)result).getXMLStreamWriter();
         } else {
            throw new UnsupportedOperationException("Result of type " + result + " is not supported");
         }
      } else if (result.getSystemId() != null) {
         return this.createXMLStreamWriter((Result)(new StreamResult(result.getSystemId())));
      } else {
         throw new UnsupportedOperationException("Result of type " + result + " is not supported. Supported result types are: DOMResult, StAXResult and StreamResult.");
      }
   }

   public XMLStreamWriter createXMLStreamWriter(Writer writer) throws XMLStreamException {
      return this.createXMLStreamWriter((StreamResult)this.toStreamResult((OutputStream)null, writer, (String)null), (String)null);
   }

   public XMLStreamWriter createXMLStreamWriter(OutputStream outputStream) throws XMLStreamException {
      return this.createXMLStreamWriter((OutputStream)outputStream, (String)null);
   }

   public XMLStreamWriter createXMLStreamWriter(OutputStream outputStream, String encoding) throws XMLStreamException {
      return this.createXMLStreamWriter(this.toStreamResult(outputStream, (Writer)null, (String)null), encoding);
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

   public void setProperty(String name, Object value) throws IllegalArgumentException {
      if (name != null && value != null && this.fPropertyManager.containsProperty(name)) {
         if (name != "reuse-instance" && !name.equals("reuse-instance")) {
            this.fPropertyChanged = true;
         } else {
            this.fReuseInstance = (Boolean)value;
            if (this.fReuseInstance) {
               throw new IllegalArgumentException("Property " + name + " is not supported: XMLStreamWriters are not Thread safe");
            }
         }

         this.fPropertyManager.setProperty(name, value);
      } else {
         throw new IllegalArgumentException("Property " + name + "is not supported");
      }
   }

   StreamResult toStreamResult(OutputStream os, Writer writer, String systemId) {
      StreamResult sr = new StreamResult();
      sr.setOutputStream(os);
      sr.setWriter(writer);
      sr.setSystemId(systemId);
      return sr;
   }

   XMLStreamWriter createXMLStreamWriter(StreamResult sr, String encoding) throws XMLStreamException {
      try {
         if (this.fReuseInstance && this.fStreamWriter != null && this.fStreamWriter.canReuse() && !this.fPropertyChanged) {
            this.fStreamWriter.reset();
            this.fStreamWriter.setOutput(sr, encoding);
            return this.fStreamWriter;
         } else {
            return this.fStreamWriter = new XMLStreamWriterImpl(sr, encoding, new PropertyManager(this.fPropertyManager));
         }
      } catch (IOException var4) {
         throw new XMLStreamException(var4);
      }
   }
}
