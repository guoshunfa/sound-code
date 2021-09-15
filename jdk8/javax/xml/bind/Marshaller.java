package javax.xml.bind;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public interface Marshaller {
   String JAXB_ENCODING = "jaxb.encoding";
   String JAXB_FORMATTED_OUTPUT = "jaxb.formatted.output";
   String JAXB_SCHEMA_LOCATION = "jaxb.schemaLocation";
   String JAXB_NO_NAMESPACE_SCHEMA_LOCATION = "jaxb.noNamespaceSchemaLocation";
   String JAXB_FRAGMENT = "jaxb.fragment";

   void marshal(Object var1, Result var2) throws JAXBException;

   void marshal(Object var1, OutputStream var2) throws JAXBException;

   void marshal(Object var1, File var2) throws JAXBException;

   void marshal(Object var1, Writer var2) throws JAXBException;

   void marshal(Object var1, ContentHandler var2) throws JAXBException;

   void marshal(Object var1, Node var2) throws JAXBException;

   void marshal(Object var1, XMLStreamWriter var2) throws JAXBException;

   void marshal(Object var1, XMLEventWriter var2) throws JAXBException;

   Node getNode(Object var1) throws JAXBException;

   void setProperty(String var1, Object var2) throws PropertyException;

   Object getProperty(String var1) throws PropertyException;

   void setEventHandler(ValidationEventHandler var1) throws JAXBException;

   ValidationEventHandler getEventHandler() throws JAXBException;

   void setAdapter(XmlAdapter var1);

   <A extends XmlAdapter> void setAdapter(Class<A> var1, A var2);

   <A extends XmlAdapter> A getAdapter(Class<A> var1);

   void setAttachmentMarshaller(AttachmentMarshaller var1);

   AttachmentMarshaller getAttachmentMarshaller();

   void setSchema(Schema var1);

   Schema getSchema();

   void setListener(Marshaller.Listener var1);

   Marshaller.Listener getListener();

   public abstract static class Listener {
      public void beforeMarshal(Object source) {
      }

      public void afterMarshal(Object source) {
      }
   }
}
