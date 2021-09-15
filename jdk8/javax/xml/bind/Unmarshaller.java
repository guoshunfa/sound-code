package javax.xml.bind;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public interface Unmarshaller {
   Object unmarshal(File var1) throws JAXBException;

   Object unmarshal(InputStream var1) throws JAXBException;

   Object unmarshal(Reader var1) throws JAXBException;

   Object unmarshal(URL var1) throws JAXBException;

   Object unmarshal(InputSource var1) throws JAXBException;

   Object unmarshal(Node var1) throws JAXBException;

   <T> JAXBElement<T> unmarshal(Node var1, Class<T> var2) throws JAXBException;

   Object unmarshal(Source var1) throws JAXBException;

   <T> JAXBElement<T> unmarshal(Source var1, Class<T> var2) throws JAXBException;

   Object unmarshal(XMLStreamReader var1) throws JAXBException;

   <T> JAXBElement<T> unmarshal(XMLStreamReader var1, Class<T> var2) throws JAXBException;

   Object unmarshal(XMLEventReader var1) throws JAXBException;

   <T> JAXBElement<T> unmarshal(XMLEventReader var1, Class<T> var2) throws JAXBException;

   UnmarshallerHandler getUnmarshallerHandler();

   /** @deprecated */
   void setValidating(boolean var1) throws JAXBException;

   /** @deprecated */
   boolean isValidating() throws JAXBException;

   void setEventHandler(ValidationEventHandler var1) throws JAXBException;

   ValidationEventHandler getEventHandler() throws JAXBException;

   void setProperty(String var1, Object var2) throws PropertyException;

   Object getProperty(String var1) throws PropertyException;

   void setSchema(Schema var1);

   Schema getSchema();

   void setAdapter(XmlAdapter var1);

   <A extends XmlAdapter> void setAdapter(Class<A> var1, A var2);

   <A extends XmlAdapter> A getAdapter(Class<A> var1);

   void setAttachmentUnmarshaller(AttachmentUnmarshaller var1);

   AttachmentUnmarshaller getAttachmentUnmarshaller();

   void setListener(Unmarshaller.Listener var1);

   Unmarshaller.Listener getListener();

   public abstract static class Listener {
      public void beforeUnmarshal(Object target, Object parent) {
      }

      public void afterUnmarshal(Object target, Object parent) {
      }
   }
}
