package javax.xml.bind.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class JAXBSource extends SAXSource {
   private final Marshaller marshaller;
   private final Object contentObject;
   private final XMLReader pseudoParser;

   public JAXBSource(JAXBContext context, Object contentObject) throws JAXBException {
      this(context == null ? assertionFailed(Messages.format("JAXBSource.NullContext")) : context.createMarshaller(), contentObject == null ? assertionFailed(Messages.format("JAXBSource.NullContent")) : contentObject);
   }

   public JAXBSource(Marshaller marshaller, Object contentObject) throws JAXBException {
      this.pseudoParser = new XMLReader() {
         private LexicalHandler lexicalHandler;
         private EntityResolver entityResolver;
         private DTDHandler dtdHandler;
         private XMLFilter repeater = new XMLFilterImpl();
         private ErrorHandler errorHandler;

         public boolean getFeature(String name) throws SAXNotRecognizedException {
            if (name.equals("http://xml.org/sax/features/namespaces")) {
               return true;
            } else if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
               return false;
            } else {
               throw new SAXNotRecognizedException(name);
            }
         }

         public void setFeature(String name, boolean value) throws SAXNotRecognizedException {
            if (!name.equals("http://xml.org/sax/features/namespaces") || !value) {
               if (!name.equals("http://xml.org/sax/features/namespace-prefixes") || value) {
                  throw new SAXNotRecognizedException(name);
               }
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
            this.repeater.setContentHandler(handler);
         }

         public ContentHandler getContentHandler() {
            return this.repeater.getContentHandler();
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
               JAXBSource.this.marshaller.marshal(JAXBSource.this.contentObject, (ContentHandler)((XMLFilterImpl)this.repeater));
            } catch (JAXBException var3) {
               SAXParseException se = new SAXParseException(var3.getMessage(), (String)null, (String)null, -1, -1, var3);
               if (this.errorHandler != null) {
                  this.errorHandler.fatalError(se);
               }

               throw se;
            }
         }
      };
      if (marshaller == null) {
         throw new JAXBException(Messages.format("JAXBSource.NullMarshaller"));
      } else if (contentObject == null) {
         throw new JAXBException(Messages.format("JAXBSource.NullContent"));
      } else {
         this.marshaller = marshaller;
         this.contentObject = contentObject;
         super.setXMLReader(this.pseudoParser);
         super.setInputSource(new InputSource());
      }
   }

   private static Marshaller assertionFailed(String message) throws JAXBException {
      throw new JAXBException(message);
   }
}
