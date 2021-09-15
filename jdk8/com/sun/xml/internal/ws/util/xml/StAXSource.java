package com.sun.xml.internal.ws.util.xml;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.SAXParseException2;
import com.sun.istack.internal.XMLStreamReaderToContentHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class StAXSource extends SAXSource {
   private final XMLStreamReaderToContentHandler reader;
   private final XMLStreamReader staxReader;
   private final XMLFilterImpl repeater;
   private final XMLReader pseudoParser;

   public StAXSource(XMLStreamReader reader, boolean eagerQuit) {
      this(reader, eagerQuit, new String[0]);
   }

   public StAXSource(XMLStreamReader reader, boolean eagerQuit, @NotNull String[] inscope) {
      this.repeater = new XMLFilterImpl();
      this.pseudoParser = new XMLReader() {
         private LexicalHandler lexicalHandler;
         private EntityResolver entityResolver;
         private DTDHandler dtdHandler;
         private ErrorHandler errorHandler;

         public boolean getFeature(String name) throws SAXNotRecognizedException {
            throw new SAXNotRecognizedException(name);
         }

         public void setFeature(String name, boolean value) throws SAXNotRecognizedException {
            if ((!name.equals("http://xml.org/sax/features/namespaces") || !value) && (!name.equals("http://xml.org/sax/features/namespace-prefixes") || value)) {
               throw new SAXNotRecognizedException(name);
            }
         }

         public Object getProperty(String name) throws SAXNotRecognizedException {
            if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
               return this.lexicalHandler;
            } else {
               throw new SAXNotRecognizedException(name);
            }
         }

         public void setProperty(String name, Object value) throws SAXNotRecognizedException {
            if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
               this.lexicalHandler = (LexicalHandler)value;
            } else {
               throw new SAXNotRecognizedException(name);
            }
         }

         public void setEntityResolver(EntityResolver resolver) {
            this.entityResolver = resolver;
         }

         public EntityResolver getEntityResolver() {
            return this.entityResolver;
         }

         public void setDTDHandler(DTDHandler handler) {
            this.dtdHandler = handler;
         }

         public DTDHandler getDTDHandler() {
            return this.dtdHandler;
         }

         public void setContentHandler(ContentHandler handler) {
            StAXSource.this.repeater.setContentHandler(handler);
         }

         public ContentHandler getContentHandler() {
            return StAXSource.this.repeater.getContentHandler();
         }

         public void setErrorHandler(ErrorHandler handler) {
            this.errorHandler = handler;
         }

         public ErrorHandler getErrorHandler() {
            return this.errorHandler;
         }

         public void parse(InputSource input) throws SAXException {
            this.parse();
         }

         public void parse(String systemId) throws SAXException {
            this.parse();
         }

         public void parse() throws SAXException {
            try {
               StAXSource.this.reader.bridge();
            } catch (XMLStreamException var10) {
               SAXParseException se = new SAXParseException2(var10.getMessage(), (String)null, (String)null, var10.getLocation() == null ? -1 : var10.getLocation().getLineNumber(), var10.getLocation() == null ? -1 : var10.getLocation().getColumnNumber(), var10);
               if (this.errorHandler != null) {
                  this.errorHandler.fatalError(se);
               }

               throw se;
            } finally {
               try {
                  StAXSource.this.staxReader.close();
               } catch (XMLStreamException var9) {
               }

            }

         }
      };
      if (reader == null) {
         throw new IllegalArgumentException();
      } else {
         this.staxReader = reader;
         int eventType = reader.getEventType();
         if (eventType != 7 && eventType != 1) {
            throw new IllegalStateException();
         } else {
            this.reader = new XMLStreamReaderToContentHandler(reader, this.repeater, eagerQuit, false, inscope);
            super.setXMLReader(this.pseudoParser);
            super.setInputSource(new InputSource());
         }
      }
   }
}
