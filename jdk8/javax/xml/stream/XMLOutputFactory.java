package javax.xml.stream;

import java.io.OutputStream;
import java.io.Writer;
import javax.xml.transform.Result;

public abstract class XMLOutputFactory {
   public static final String IS_REPAIRING_NAMESPACES = "javax.xml.stream.isRepairingNamespaces";
   static final String DEFAULIMPL = "com.sun.xml.internal.stream.XMLOutputFactoryImpl";

   protected XMLOutputFactory() {
   }

   public static XMLOutputFactory newInstance() throws FactoryConfigurationError {
      return (XMLOutputFactory)FactoryFinder.find(XMLOutputFactory.class, "com.sun.xml.internal.stream.XMLOutputFactoryImpl");
   }

   public static XMLOutputFactory newFactory() throws FactoryConfigurationError {
      return (XMLOutputFactory)FactoryFinder.find(XMLOutputFactory.class, "com.sun.xml.internal.stream.XMLOutputFactoryImpl");
   }

   /** @deprecated */
   public static XMLInputFactory newInstance(String factoryId, ClassLoader classLoader) throws FactoryConfigurationError {
      return (XMLInputFactory)FactoryFinder.find(XMLInputFactory.class, factoryId, classLoader, (String)null);
   }

   public static XMLOutputFactory newFactory(String factoryId, ClassLoader classLoader) throws FactoryConfigurationError {
      return (XMLOutputFactory)FactoryFinder.find(XMLOutputFactory.class, factoryId, classLoader, (String)null);
   }

   public abstract XMLStreamWriter createXMLStreamWriter(Writer var1) throws XMLStreamException;

   public abstract XMLStreamWriter createXMLStreamWriter(OutputStream var1) throws XMLStreamException;

   public abstract XMLStreamWriter createXMLStreamWriter(OutputStream var1, String var2) throws XMLStreamException;

   public abstract XMLStreamWriter createXMLStreamWriter(Result var1) throws XMLStreamException;

   public abstract XMLEventWriter createXMLEventWriter(Result var1) throws XMLStreamException;

   public abstract XMLEventWriter createXMLEventWriter(OutputStream var1) throws XMLStreamException;

   public abstract XMLEventWriter createXMLEventWriter(OutputStream var1, String var2) throws XMLStreamException;

   public abstract XMLEventWriter createXMLEventWriter(Writer var1) throws XMLStreamException;

   public abstract void setProperty(String var1, Object var2) throws IllegalArgumentException;

   public abstract Object getProperty(String var1) throws IllegalArgumentException;

   public abstract boolean isPropertySupported(String var1);
}
