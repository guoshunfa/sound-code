package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.IDResolver;
import com.sun.xml.internal.bind.api.ClassResolver;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import com.sun.xml.internal.bind.unmarshaller.InfosetScanner;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.runtime.AssociationMap;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.bind.helpers.AbstractUnmarshallerImpl;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public final class UnmarshallerImpl extends AbstractUnmarshallerImpl implements ValidationEventHandler, Closeable {
   protected final JAXBContextImpl context;
   private Schema schema;
   public final UnmarshallingContext coordinator;
   private Unmarshaller.Listener externalListener;
   private AttachmentUnmarshaller attachmentUnmarshaller;
   private IDResolver idResolver = new DefaultIDResolver();
   private XMLReader reader = null;
   private static final DefaultHandler dummyHandler = new DefaultHandler();
   public static final String FACTORY = "com.sun.xml.internal.bind.ObjectFactory";

   public UnmarshallerImpl(JAXBContextImpl context, AssociationMap assoc) {
      this.context = context;
      this.coordinator = new UnmarshallingContext(this, assoc);

      try {
         this.setEventHandler(this);
      } catch (JAXBException var4) {
         throw new AssertionError(var4);
      }
   }

   public UnmarshallerHandler getUnmarshallerHandler() {
      return this.getUnmarshallerHandler(true, (JaxBeanInfo)null);
   }

   protected XMLReader getXMLReader() throws JAXBException {
      if (this.reader == null) {
         try {
            SAXParserFactory parserFactory = XmlFactory.createParserFactory(this.context.disableSecurityProcessing);
            parserFactory.setValidating(false);
            this.reader = parserFactory.newSAXParser().getXMLReader();
         } catch (ParserConfigurationException var2) {
            throw new JAXBException(var2);
         } catch (SAXException var3) {
            throw new JAXBException(var3);
         }
      }

      return this.reader;
   }

   private SAXConnector getUnmarshallerHandler(boolean intern, JaxBeanInfo expectedType) {
      XmlVisitor h = this.createUnmarshallerHandler((InfosetScanner)null, false, expectedType);
      if (intern) {
         h = new InterningXmlVisitor((XmlVisitor)h);
      }

      return new SAXConnector((XmlVisitor)h, (LocatorEx)null);
   }

   public final XmlVisitor createUnmarshallerHandler(InfosetScanner scanner, boolean inplace, JaxBeanInfo expectedType) {
      this.coordinator.reset(scanner, inplace, expectedType, this.idResolver);
      XmlVisitor unmarshaller = this.coordinator;
      if (this.schema != null) {
         unmarshaller = new ValidatingUnmarshaller(this.schema, (XmlVisitor)unmarshaller);
      }

      if (this.attachmentUnmarshaller != null && this.attachmentUnmarshaller.isXOPPackage()) {
         unmarshaller = new MTOMDecorator(this, (XmlVisitor)unmarshaller, this.attachmentUnmarshaller);
      }

      return (XmlVisitor)unmarshaller;
   }

   public static boolean needsInterning(XMLReader reader) {
      try {
         reader.setFeature("http://xml.org/sax/features/string-interning", true);
      } catch (SAXException var3) {
      }

      try {
         if (reader.getFeature("http://xml.org/sax/features/string-interning")) {
            return false;
         }
      } catch (SAXException var2) {
      }

      return true;
   }

   protected Object unmarshal(XMLReader reader, InputSource source) throws JAXBException {
      return this.unmarshal0(reader, source, (JaxBeanInfo)null);
   }

   protected <T> JAXBElement<T> unmarshal(XMLReader reader, InputSource source, Class<T> expectedType) throws JAXBException {
      if (expectedType == null) {
         throw new IllegalArgumentException();
      } else {
         return (JAXBElement)this.unmarshal0(reader, source, this.getBeanInfo(expectedType));
      }
   }

   private Object unmarshal0(XMLReader reader, InputSource source, JaxBeanInfo expectedType) throws JAXBException {
      SAXConnector connector = this.getUnmarshallerHandler(needsInterning(reader), expectedType);
      reader.setContentHandler(connector);
      reader.setErrorHandler(this.coordinator);

      try {
         reader.parse(source);
      } catch (IOException var6) {
         this.coordinator.clearStates();
         throw new UnmarshalException(var6);
      } catch (SAXException var7) {
         this.coordinator.clearStates();
         throw this.createUnmarshalException(var7);
      }

      Object result = connector.getResult();
      reader.setContentHandler(dummyHandler);
      reader.setErrorHandler(dummyHandler);
      return result;
   }

   public <T> JAXBElement<T> unmarshal(Source source, Class<T> expectedType) throws JAXBException {
      if (source instanceof SAXSource) {
         SAXSource ss = (SAXSource)source;
         XMLReader locReader = ss.getXMLReader();
         if (locReader == null) {
            locReader = this.getXMLReader();
         }

         return this.unmarshal(locReader, ss.getInputSource(), expectedType);
      } else if (source instanceof StreamSource) {
         return this.unmarshal(this.getXMLReader(), streamSourceToInputSource((StreamSource)source), expectedType);
      } else if (source instanceof DOMSource) {
         return this.unmarshal(((DOMSource)source).getNode(), expectedType);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Object unmarshal0(Source source, JaxBeanInfo expectedType) throws JAXBException {
      if (source instanceof SAXSource) {
         SAXSource ss = (SAXSource)source;
         XMLReader locReader = ss.getXMLReader();
         if (locReader == null) {
            locReader = this.getXMLReader();
         }

         return this.unmarshal0(locReader, ss.getInputSource(), expectedType);
      } else if (source instanceof StreamSource) {
         return this.unmarshal0(this.getXMLReader(), streamSourceToInputSource((StreamSource)source), expectedType);
      } else if (source instanceof DOMSource) {
         return this.unmarshal0(((DOMSource)source).getNode(), expectedType);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final ValidationEventHandler getEventHandler() {
      try {
         return super.getEventHandler();
      } catch (JAXBException var2) {
         throw new AssertionError();
      }
   }

   public final boolean hasEventHandler() {
      return this.getEventHandler() != this;
   }

   public <T> JAXBElement<T> unmarshal(Node node, Class<T> expectedType) throws JAXBException {
      if (expectedType == null) {
         throw new IllegalArgumentException();
      } else {
         return (JAXBElement)this.unmarshal0(node, this.getBeanInfo(expectedType));
      }
   }

   public final Object unmarshal(Node node) throws JAXBException {
      return this.unmarshal0((Node)node, (JaxBeanInfo)null);
   }

   /** @deprecated */
   @Deprecated
   public final Object unmarshal(SAXSource source) throws JAXBException {
      return super.unmarshal((Source)source);
   }

   public final Object unmarshal0(Node node, JaxBeanInfo expectedType) throws JAXBException {
      try {
         DOMScanner scanner = new DOMScanner();
         InterningXmlVisitor handler = new InterningXmlVisitor(this.createUnmarshallerHandler((InfosetScanner)null, false, expectedType));
         scanner.setContentHandler(new SAXConnector(handler, scanner));
         if (node.getNodeType() == 1) {
            scanner.scan((Element)node);
         } else {
            if (node.getNodeType() != 9) {
               throw new IllegalArgumentException("Unexpected node type: " + node);
            }

            scanner.scan((Document)node);
         }

         Object retVal = handler.getContext().getResult();
         handler.getContext().clearResult();
         return retVal;
      } catch (SAXException var6) {
         throw this.createUnmarshalException(var6);
      }
   }

   public Object unmarshal(XMLStreamReader reader) throws JAXBException {
      return this.unmarshal0((XMLStreamReader)reader, (JaxBeanInfo)null);
   }

   public <T> JAXBElement<T> unmarshal(XMLStreamReader reader, Class<T> expectedType) throws JAXBException {
      if (expectedType == null) {
         throw new IllegalArgumentException();
      } else {
         return (JAXBElement)this.unmarshal0(reader, this.getBeanInfo(expectedType));
      }
   }

   public Object unmarshal0(XMLStreamReader reader, JaxBeanInfo expectedType) throws JAXBException {
      if (reader == null) {
         throw new IllegalArgumentException(com.sun.xml.internal.bind.unmarshaller.Messages.format("Unmarshaller.NullReader"));
      } else {
         int eventType = reader.getEventType();
         if (eventType != 1 && eventType != 7) {
            throw new IllegalStateException(com.sun.xml.internal.bind.unmarshaller.Messages.format("Unmarshaller.IllegalReaderState", (Object)eventType));
         } else {
            XmlVisitor h = this.createUnmarshallerHandler((InfosetScanner)null, false, expectedType);
            StAXConnector connector = StAXStreamConnector.create(reader, h);

            try {
               connector.bridge();
            } catch (XMLStreamException var7) {
               throw handleStreamException(var7);
            }

            Object retVal = h.getContext().getResult();
            h.getContext().clearResult();
            return retVal;
         }
      }
   }

   public <T> JAXBElement<T> unmarshal(XMLEventReader reader, Class<T> expectedType) throws JAXBException {
      if (expectedType == null) {
         throw new IllegalArgumentException();
      } else {
         return (JAXBElement)this.unmarshal0(reader, this.getBeanInfo(expectedType));
      }
   }

   public Object unmarshal(XMLEventReader reader) throws JAXBException {
      return this.unmarshal0((XMLEventReader)reader, (JaxBeanInfo)null);
   }

   private Object unmarshal0(XMLEventReader reader, JaxBeanInfo expectedType) throws JAXBException {
      if (reader == null) {
         throw new IllegalArgumentException(com.sun.xml.internal.bind.unmarshaller.Messages.format("Unmarshaller.NullReader"));
      } else {
         try {
            XMLEvent event = reader.peek();
            if (!event.isStartElement() && !event.isStartDocument()) {
               throw new IllegalStateException(com.sun.xml.internal.bind.unmarshaller.Messages.format("Unmarshaller.IllegalReaderState", (Object)event.getEventType()));
            } else {
               boolean isZephyr = reader.getClass().getName().equals("com.sun.xml.internal.stream.XMLReaderImpl");
               XmlVisitor h = this.createUnmarshallerHandler((InfosetScanner)null, false, expectedType);
               if (!isZephyr) {
                  h = new InterningXmlVisitor((XmlVisitor)h);
               }

               (new StAXEventConnector(reader, (XmlVisitor)h)).bridge();
               return ((XmlVisitor)h).getContext().getResult();
            }
         } catch (XMLStreamException var6) {
            throw handleStreamException(var6);
         }
      }
   }

   public Object unmarshal0(InputStream input, JaxBeanInfo expectedType) throws JAXBException {
      return this.unmarshal0(this.getXMLReader(), new InputSource(input), expectedType);
   }

   private static JAXBException handleStreamException(XMLStreamException e) {
      Throwable ne = e.getNestedException();
      if (ne instanceof JAXBException) {
         return (JAXBException)ne;
      } else {
         return ne instanceof SAXException ? new UnmarshalException(ne) : new UnmarshalException(e);
      }
   }

   public Object getProperty(String name) throws PropertyException {
      return name.equals(IDResolver.class.getName()) ? this.idResolver : super.getProperty(name);
   }

   public void setProperty(String name, Object value) throws PropertyException {
      if (name.equals("com.sun.xml.internal.bind.ObjectFactory")) {
         this.coordinator.setFactories(value);
      } else if (name.equals(IDResolver.class.getName())) {
         this.idResolver = (IDResolver)value;
      } else if (name.equals(ClassResolver.class.getName())) {
         this.coordinator.classResolver = (ClassResolver)value;
      } else if (name.equals(ClassLoader.class.getName())) {
         this.coordinator.classLoader = (ClassLoader)value;
      } else {
         super.setProperty(name, value);
      }
   }

   public void setSchema(Schema schema) {
      this.schema = schema;
   }

   public Schema getSchema() {
      return this.schema;
   }

   public AttachmentUnmarshaller getAttachmentUnmarshaller() {
      return this.attachmentUnmarshaller;
   }

   public void setAttachmentUnmarshaller(AttachmentUnmarshaller au) {
      this.attachmentUnmarshaller = au;
   }

   /** @deprecated */
   public boolean isValidating() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   public void setValidating(boolean validating) {
      throw new UnsupportedOperationException();
   }

   public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
      if (type == null) {
         throw new IllegalArgumentException();
      } else {
         this.coordinator.putAdapter(type, adapter);
      }
   }

   public <A extends XmlAdapter> A getAdapter(Class<A> type) {
      if (type == null) {
         throw new IllegalArgumentException();
      } else {
         return this.coordinator.containsAdapter(type) ? this.coordinator.getAdapter(type) : null;
      }
   }

   public UnmarshalException createUnmarshalException(SAXException e) {
      return super.createUnmarshalException(e);
   }

   public boolean handleEvent(ValidationEvent event) {
      return event.getSeverity() != 2;
   }

   private static InputSource streamSourceToInputSource(StreamSource ss) {
      InputSource is = new InputSource();
      is.setSystemId(ss.getSystemId());
      is.setByteStream(ss.getInputStream());
      is.setCharacterStream(ss.getReader());
      return is;
   }

   public <T> JaxBeanInfo<T> getBeanInfo(Class<T> clazz) throws JAXBException {
      return this.context.getBeanInfo(clazz, true);
   }

   public Unmarshaller.Listener getListener() {
      return this.externalListener;
   }

   public void setListener(Unmarshaller.Listener listener) {
      this.externalListener = listener;
   }

   public UnmarshallingContext getContext() {
      return this.coordinator;
   }

   protected void finalize() throws Throwable {
      try {
         ClassFactory.cleanCache();
      } finally {
         super.finalize();
      }

   }

   public void close() throws IOException {
      ClassFactory.cleanCache();
   }
}
